package com.venom.lingopro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.venom.lingopro.ui.components.sections.TranslationSection
import com.venom.lingopro.ui.viewmodel.TranslateViewModel
import com.venom.lingopro.ui.viewmodel.TranslationActions
import com.venom.ui.components.dialogs.DraggableDialog
import com.venom.ui.components.dialogs.FullscreenTextDialog
import com.venom.ui.components.speech.SpeechToTextDialog
import com.venom.ui.screen.dictionary.DictionaryScreen
import com.venom.ui.screen.dictionary.TranslationsCard
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
    onNavigateToOcr: () -> Unit,
    initialText: String? = null,
    isDialog: Boolean = false,
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val langSelectorState by langSelectorViewModel.state.collectAsStateWithLifecycle()
    val ttsState by ttsViewModel.uiState.collectAsStateWithLifecycle()
    val transcription by sttViewModel.transcription.collectAsStateWithLifecycle()


    var sourceTextFieldValue by remember {
        mutableStateOf(TextFieldValue(initialText ?: state.sourceText))
    }

    var showDictionaryDialog by remember { mutableStateOf(false) }
    var showSpeechToTextDialog by remember { mutableStateOf(false) }
    var fullscreenState by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state.clearTextFlag) {
        if (state.clearTextFlag) {
            sourceTextFieldValue = TextFieldValue("")
            viewModel.clearTranslation()
        }
    }

    LaunchedEffect(langSelectorState) {
        viewModel.updateLanguages(langSelectorState.sourceLang, langSelectorState.targetLang)

        if (sourceTextFieldValue.text.isNotBlank()) {
            // Preserve cursor position and selection when swapping
            val currentSelection = sourceTextFieldValue.selection
            val currentComposition = sourceTextFieldValue.composition

            viewModel.onSourceTextChanged(sourceTextFieldValue.text)
            sourceTextFieldValue = TextFieldValue(
                text = sourceTextFieldValue.text,
                selection = currentSelection,
                composition = currentComposition
            )
            langSelectorViewModel.resetSwapFlag()
        }
    }

    val actions = remember(context) {
        TranslationActions(onTextChange = { newValue ->
            sourceTextFieldValue = newValue
            viewModel.onSourceTextChanged(newValue.text)
        },
            onClearText = {
                sourceTextFieldValue = TextFieldValue("")
                viewModel.clearTranslation()
            },
            onCopy = { text -> context.copyToClipboard(text) },
            onShare = { text -> context.shareText(text) },
            onSpeak = ttsViewModel::speak,
            onBookmark = viewModel::toggleBookmark,
            onSpeechToText = { showSpeechToTextDialog = true },
            onPaste = {
                context.pasteFromClipboard()?.let { text ->
                    sourceTextFieldValue = TextFieldValue(text)
                    viewModel.onSourceTextChanged(text)
                }
            },
            onOcr = onNavigateToOcr,
            onFullscreen = { fullscreenState = it })
    }

    @Composable
    fun TranslationContent(modifier: Modifier = Modifier) {
        Column(modifier = modifier) {
            TranslationSection(
                viewModel = langSelectorViewModel,
                sourceTextValue = sourceTextFieldValue,
                translatedTextValue = TextFieldValue(state.translatedText),
                actions = actions,
                isLoading = state.isLoading,
                isSpeaking = ttsState.isSpeaking,
                isBookmarked = state.isBookmarked,
            )

            if (!isDialog && state.synonyms.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                state.translationResult.dict?.let { translations ->
                    TranslationsCard(
                        translations = translations,
                        onWordClick = { selectedWord ->
                            sourceTextFieldValue = TextFieldValue(selectedWord)
                            viewModel.onSourceTextChanged(selectedWord)
                        },
                        onExpand = { showDictionaryDialog = true },
                        onSpeak = ttsViewModel::speak,
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                    )
                }
            }
        }
    }


    if (isDialog) DraggableDialog(onDismissRequest = onDismiss) {
        TranslationContent()
    }
    else TranslationContent(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
    )

    if (showDictionaryDialog) Dialog(
        onDismissRequest = { showDictionaryDialog = false }, properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
            dismissOnBackPress = true
        )
    ) {
        DictionaryScreen(translationResponse = state.translationResult,
            onWordClick = { selectedWord ->
//                sourceTextFieldValue = TextFieldValue(selectedWord)
//                viewModel.onSourceTextChanged(selectedWord)
            },
            onSpeak = ttsViewModel::speak,
            onCopy = actions.onCopy,
            onDismiss = { showDictionaryDialog = false })
    }

    if (showSpeechToTextDialog) SpeechToTextDialog(
        sttViewModel = sttViewModel,
        onDismiss = {
            showSpeechToTextDialog = false
            transcription.let {
                sourceTextFieldValue = TextFieldValue(it)
                viewModel.onSourceTextChanged(it)
                sttViewModel.stopRecognition()
            }
        },
    )

    fullscreenState?.let { text ->
        FullscreenTextDialog(
            textValue = TextFieldValue(text),
            onDismiss = { fullscreenState = null },
            onCopy = actions.onCopy,
            onShare = actions.onShare,
            onSpeak = actions.onSpeak
        )
    }
}