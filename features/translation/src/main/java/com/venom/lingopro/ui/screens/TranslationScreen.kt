package com.venom.lingopro.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.venom.lingopro.ui.components.sections.FloatingDialogActions
import com.venom.lingopro.ui.viewmodel.TranslationActions
import com.venom.resources.R
import com.venom.ui.components.common.adp
import com.venom.ui.components.dialogs.DraggableDialog
import com.venom.ui.components.dialogs.FullscreenTextDialog
import com.venom.ui.components.other.SnackbarHost
import com.venom.ui.components.other.rememberSnackbarController
import com.venom.ui.components.speech.SpeechToTextDialog
import com.venom.ui.screen.dictionary.DictionaryScreen
import com.venom.ui.screen.langselector.LangSelectorViewModel
import com.venom.ui.viewmodel.STTViewModel
import com.venom.ui.viewmodel.TTSViewModel
import com.venom.ui.viewmodel.TranslateViewModel
import com.venom.utils.Extensions.copyToClipboard
import com.venom.utils.Extensions.pasteFromClipboard
import com.venom.utils.Extensions.shareText

@Composable
fun TranslationScreen(
    viewModel: TranslateViewModel = hiltViewModel(LocalContext.current as ComponentActivity),
    ttsViewModel: TTSViewModel = hiltViewModel(LocalContext.current as ComponentActivity),
    sttViewModel: STTViewModel = hiltViewModel(LocalContext.current as ComponentActivity),
    langSelectorViewModel: LangSelectorViewModel = hiltViewModel(LocalContext.current as ComponentActivity),
    onNavigateToOcr: () -> Unit = {},
    onNavigateToSentence: (String) -> Unit = {},
    onOpenInApp: (String) -> Unit = {},
    initialText: String? = null,
    isDialog: Boolean = false,
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current
    val snackbarController = rememberSnackbarController()

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val langSelectorState by langSelectorViewModel.state.collectAsStateWithLifecycle()
    val ttsState by ttsViewModel.uiState.collectAsStateWithLifecycle()
    val transcription by sttViewModel.transcription.collectAsStateWithLifecycle()

    var sourceTextFieldValue by remember { mutableStateOf(TextFieldValue()) }
    val translatedTextFieldValue by remember { derivedStateOf { TextFieldValue(state.translatedText) } }
    var showDictionaryDialog by remember { mutableStateOf(false) }
    var showSpeechToTextDialog by remember { mutableStateOf(false) }
    var fullscreenText by remember { mutableStateOf<String?>(null) }
    var initialTextHandled by remember { mutableStateOf(false) }

    LaunchedEffect(initialText, state.sourceText) {
        if (initialText?.isNotBlank() == true && !initialTextHandled) {
            sourceTextFieldValue = TextFieldValue(initialText)
            viewModel.onSourceTextChanged(initialText)
            initialTextHandled = true
        } else if (initialText.isNullOrBlank() && !initialTextHandled) {
            val savedText = state.sourceText
            if (savedText.isNotBlank() && sourceTextFieldValue.text != savedText) {
                sourceTextFieldValue = TextFieldValue(savedText)
            }
            initialTextHandled = true
        }
    }

    LaunchedEffect(langSelectorState.sourceLang, langSelectorState.targetLang) {
        viewModel.updateLanguages(langSelectorState.sourceLang, langSelectorState.targetLang)
    }

    LaunchedEffect(state.sourceText) {
        if (state.sourceText != sourceTextFieldValue.text) {
            val newSelection =
                if (state.sourceText.length >= sourceTextFieldValue.selection.start) {
                    sourceTextFieldValue.selection
                } else {
                    TextRange(state.sourceText.length)
                }
            sourceTextFieldValue = TextFieldValue(state.sourceText, newSelection)
        }
    }

    LaunchedEffect(transcription) {
        if (transcription.isNotBlank()) {
            sourceTextFieldValue = TextFieldValue(transcription)
            viewModel.onSourceTextChanged(transcription)
            if (showSpeechToTextDialog) {
                showSpeechToTextDialog = false
                sttViewModel.stopRecognition()
            }
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarController.error(error, R.string.action_retry) {
                viewModel.retryTranslation()
            }
            viewModel.clearError()
        }
    }

    val actions = remember(context, viewModel, ttsViewModel, snackbarController) {
        TranslationActions(
            onTextChange = { newValue ->
                sourceTextFieldValue = newValue
                viewModel.onSourceTextChanged(newValue.text)
            },
            onClearText = {
                sourceTextFieldValue = TextFieldValue("")
                viewModel.clearAll()
            },
            onCopy = { text ->
                runCatching { context.copyToClipboard(text) }.onSuccess {
                    snackbarController.success(R.string.action_copy)
                }
                    .onFailure { snackbarController.error(R.string.failed_to_copy) }
            },
            onShare = { text ->
                runCatching { context.shareText(text) }.onFailure {
                    snackbarController.error(R.string.failed_to_share)
                }
            },
            onSpeak = { text ->
                ttsViewModel.speakWithGroq(
                    text,
                    langSelectorState.targetLang.code
                )
            },
            onBookmark = viewModel::toggleBookmark,
            onSpeechToTextStart = {
                sttViewModel.startRecognition()
            },
            onSpeechToTextEnd = {
                sttViewModel.stopRecognition()
            },
            isSpeechToTextActive = sttViewModel.isRecognitionActive,

            onPaste = {
                runCatching {
                    context.pasteFromClipboard()?.let { text ->
                        if (text.isNotBlank()) {
                            sourceTextFieldValue = TextFieldValue(text)
                            viewModel.onSourceTextChanged(text)
                            snackbarController.success(R.string.text_pasted)
                        }
                    }
                }.onFailure { snackbarController.error(R.string.failed_to_paste) }
            },
            onMoveUp = viewModel::moveUp,
            onOcr = onNavigateToOcr,
            onFullscreen = { fullscreenText = it },
            onSentenceExplorer = { word -> onNavigateToSentence(word) })
    }

    val handleDismiss: () -> Unit = remember(sttViewModel, ttsViewModel, onDismiss) {
        {
            sttViewModel.stopRecognition()
            ttsViewModel.stop()
            onDismiss()
        }
    }

    // State-hoisted Content
    Box(modifier = Modifier.fillMaxSize()) {
        if (isDialog) {
            DraggableDialog(
                onDismissRequest = handleDismiss
            ) {
                Box {
                    TranslationContent(
                        langSelectorViewModel = langSelectorViewModel,
                        state = state,
                        sourceTextFieldValue = sourceTextFieldValue,
                        translatedTextFieldValue = translatedTextFieldValue,
                        actions = actions,
                        isSpeakingText = ttsState::isSpeakingText,
                        onExpandDictionary = { showDictionaryDialog = true },
                        onSpeakDictionaryWord = ttsViewModel::toggle,
                        isDialog = true
                    )
                    FloatingDialogActions(
                        onDismiss = handleDismiss,
                        onOpenInApp = { onOpenInApp(sourceTextFieldValue.text) }
                    )
                }
            }
        } else {
            TranslationContent(
                langSelectorViewModel = langSelectorViewModel,
                state = state,
                sourceTextFieldValue = sourceTextFieldValue,
                translatedTextFieldValue = translatedTextFieldValue,
                actions = actions,
                isSpeakingText = ttsState::isSpeakingText,
                onExpandDictionary = { showDictionaryDialog = true },
                onSpeakDictionaryWord = ttsViewModel::toggle,
                isDialog = false,
                modifier = Modifier.padding(8.adp)
            )
        }

        SnackbarHost(controller = snackbarController)
    }

    if (showDictionaryDialog) {
        Dialog(
            onDismissRequest = { showDictionaryDialog = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false,
                dismissOnBackPress = true
            )
        ) {
            DictionaryScreen(
                translationResult = state.translationResult,
                onWordClick = { selectedWord ->
                    /* if (selectedWord.isNotBlank()) {
                         sourceTextFieldValue = TextFieldValue(selectedWord)
                         viewModel.onSourceTextChanged(selectedWord)
                         showDictionaryDialog = false
                     }*/
                },
                onSpeak = ttsViewModel::toggle,
                isSpeakingText = ttsState::isSpeakingText,
                onCopy = actions.onCopy,
                onDismiss = { showDictionaryDialog = false }
            )
        }
    }

    if (showSpeechToTextDialog) {
        SpeechToTextDialog(
            sttViewModel = sttViewModel,
            onDismiss = {
                showSpeechToTextDialog = false
                sttViewModel.stopRecognition()
            }
        )
    }

    fullscreenText?.let { text ->
        FullscreenTextDialog(
            text = text,
            onDismiss = { modifiedText ->
                if (modifiedText != text) {
                    sourceTextFieldValue = TextFieldValue(modifiedText)
                    viewModel.onSourceTextChanged(modifiedText)
                }
                fullscreenText = null
            },
            onCopy = actions.onCopy,
            onShare = actions.onShare,
            onSpeak = actions.onSpeak
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            sttViewModel.stopRecognition()
            ttsViewModel.stop()
        }
    }
}