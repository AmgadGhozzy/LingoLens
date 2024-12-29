package com.venom.lingopro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.venom.domain.model.LanguageItem
import com.venom.lingopro.ui.components.sections.ThesaurusView
import com.venom.lingopro.ui.components.sections.TranslationSection
import com.venom.lingopro.ui.viewmodel.TranslateViewModel
import com.venom.ui.components.dialogs.CustomCard
import com.venom.ui.components.dialogs.DraggableDialog
import com.venom.ui.components.dialogs.FullscreenTextDialog
import com.venom.ui.components.speech.SpeechToTextDialog
import com.venom.ui.viewmodel.LangSelectorViewModel
import com.venom.ui.viewmodel.STTViewModel
import com.venom.ui.viewmodel.TTSViewModel
import com.venom.utils.Extensions.copyToClipboard
import com.venom.utils.Extensions.pasteFromClipboard
import com.venom.utils.Extensions.shareText

@Composable
fun TranslationScreen(
    viewModel: TranslateViewModel = hiltViewModel(),
    ttsViewModel: TTSViewModel = hiltViewModel(),
    sttViewModel: STTViewModel = hiltViewModel(),
    langSelectorViewModel: LangSelectorViewModel = hiltViewModel(),
    initialText: String? = null,
    isDialog: Boolean = false,
    onDismiss: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val langSelectorState by langSelectorViewModel.state.collectAsStateWithLifecycle()
    val ttsState by ttsViewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current

    var sourceText by remember { mutableStateOf(initialText ?: state.sourceText) }
    var showSpeechToTextDialog by remember { mutableStateOf(false) }
    var showFullscreenDialog by remember { mutableStateOf(false) }

    LaunchedEffect(initialText, isDialog) {
        if (isDialog && !initialText.isNullOrBlank()) {
            viewModel.onSourceTextChanged(initialText)
        }
    }

    LaunchedEffect(langSelectorState.sourceLang, langSelectorState.targetLang) {
        viewModel.updateLanguages(langSelectorState.sourceLang, langSelectorState.targetLang)
    }

    val textChangeAction: (TextFieldValue) -> Unit = { text ->
        sourceText = text.text
        viewModel.onSourceTextChanged(sourceText)
    }

    val clearTextAction = {
        viewModel.clearText()
        sourceText = ""
    }

    // Actions for copying, sharing, and speaking
    val copyAction: (String) -> Unit = { text -> context.copyToClipboard(text) }
    val shareAction: (String) -> Unit = { text -> context.shareText(text) }
    val speakAction: (String) -> Unit = { text -> ttsViewModel.speak(text) }
    val onSaveAction: () -> Unit = { viewModel.toggleBookmark() }
    val onSpeechToTextAction: () -> Unit = { showSpeechToTextDialog = true }
    val onPasteAction: () -> Unit = {
        val pastedText = context.pasteFromClipboard()
        pastedText?.let {
            sourceText = it
            viewModel.onSourceTextChanged(it)
        }
    }

    @Composable
    fun TranslationContent(modifier: Modifier = Modifier) {
        Column(modifier = modifier) {
            TranslationSection(viewModel = langSelectorViewModel,
                sourceTextValue = TextFieldValue(sourceText),
                translatedTextValue = TextFieldValue(state.translatedText),
                onTextChange = textChangeAction,
                onClearText = clearTextAction,
                isLoading = state.isLoading,
                onCopy = copyAction,
                onShare = shareAction,
                onSpeak = speakAction,
                onPaste = onPasteAction,
                onSave = onSaveAction,
                onSpeechToText = onSpeechToTextAction,
                onFullscreen = { showFullscreenDialog = true })

            if (!isDialog && state.synonyms.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                CustomCard {
                    ThesaurusView(synonyms = state.synonyms, onWordClick = { selectedWord ->
                        sourceText = selectedWord
                        viewModel.onSourceTextChanged(selectedWord)
                    })
                }
            }
        }
    }

    if (isDialog) {
        DraggableDialog(onDismissRequest = onDismiss) {
            TranslationContent()
        }
    } else {
        TranslationContent(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        )
    }

    if (showSpeechToTextDialog) {
        SpeechToTextDialog(
            sttViewModel = sttViewModel,
            onDismiss = { showSpeechToTextDialog = false },
        )
    }

    if (showFullscreenDialog) {
        FullscreenTextDialog(
            textValue = TextFieldValue(state.translatedText),
            onDismiss = { showFullscreenDialog = false },
            onCopy = copyAction,
            onShare = shareAction,
            onSpeak = speakAction
        )
    }
}