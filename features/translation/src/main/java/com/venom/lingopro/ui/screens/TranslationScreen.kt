package com.venom.lingopro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.venom.domain.model.LANGUAGES_LIST
import com.venom.domain.model.LanguageItem
import com.venom.lingopro.ui.components.sections.ThesaurusView
import com.venom.lingopro.ui.components.sections.TranslationSection
import com.venom.lingopro.ui.viewmodel.TranslateViewModel
import com.venom.ui.components.dialogs.CustomCard
import com.venom.ui.components.dialogs.DraggableDialog
import com.venom.ui.components.dialogs.FullscreenTextDialog
import com.venom.ui.components.speech.SpeechToTextDialog
import com.venom.ui.viewmodel.STTViewModel
import com.venom.ui.viewmodel.TTSViewModel
import com.venom.utils.Extensions.copyToClipboard
import com.venom.utils.Extensions.pasteFromClipboard
import com.venom.utils.Extensions.shareText
import kotlinx.coroutines.launch

@Composable
fun TranslationScreen(
    viewModel: TranslateViewModel = hiltViewModel(),
    ttsViewModel: TTSViewModel = hiltViewModel(),
    sttViewModel: STTViewModel = hiltViewModel(),
    initialText: String? = null,
    isDialog: Boolean = false,
    onDismiss: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val ttsState by ttsViewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var sourceLang by remember { mutableStateOf(LANGUAGES_LIST[0]) }
    var targetLang by remember { mutableStateOf(LANGUAGES_LIST[1]) }

    var sourceText by remember { mutableStateOf(initialText ?: state.sourceText) }

    var showSpeechToTextDialog by remember { mutableStateOf(false) }

    var showFullscreenDialog by remember { mutableStateOf(false) }


    LaunchedEffect(initialText, isDialog) {
        if (isDialog && !initialText.isNullOrBlank()) {
            viewModel.onSourceTextChanged(initialText)
        }
    }

    val languageSelectAction: (Boolean, LanguageItem) -> Unit = { isSourceLang, selectedLang ->
        if (isSourceLang) {
            sourceLang = selectedLang
            viewModel.updateLanguages(sourceLang, targetLang)
        } else {
            targetLang = selectedLang
            viewModel.updateLanguages(sourceLang, targetLang)
        }
        viewModel.onSourceTextChanged(sourceText)
    }

    val languageSwapAction = {
        val temp = sourceLang
        sourceLang = targetLang
        targetLang = temp
        viewModel.updateLanguages(sourceLang, targetLang)
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
    val onSpeechToTextAction: () -> Unit = {
        showSpeechToTextDialog = true
    }
    val onPasteAction: () -> Unit = {
        val pastedText = context.pasteFromClipboard()
        pastedText?.let {
            sourceText = it
            viewModel.onSourceTextChanged(it)
        }
    }

    // Content composable work for both dialog and fullScreen modes
    @Composable
    fun TranslationContent(modifier: Modifier = Modifier) {
        Column(modifier = modifier) {
            TranslationSection(sourceLang = sourceLang,
                targetLang = targetLang,
                sourceTextValue = TextFieldValue(sourceText),
                translatedTextValue = TextFieldValue(state.translatedText),
                onTextChange = textChangeAction,
                onLanguageSelect = languageSelectAction,
                onSwapLanguages = languageSwapAction,
                onClearText = clearTextAction,
                isLoading = state.isLoading,

                onCopy = copyAction,
                onShare = shareAction,
                onSpeak = speakAction,
                onPaste = onPasteAction,
                onSave = onSaveAction,
                onSpeechToText = onSpeechToTextAction,
                onFullscreen = { showFullscreenDialog = true })

            // Only show thesaurus in fullScreen mode
            if (!isDialog && state.synonyms.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                CustomCard() {
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
            state = sttViewModel.speechState,
            onDismiss = { showSpeechToTextDialog = false },
            onStop = sttViewModel::stopRecognition,
            onStart = {
                coroutineScope.launch {
                    sttViewModel.startRecognition()
                }
            },
            onPause = sttViewModel::pauseRecognition
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