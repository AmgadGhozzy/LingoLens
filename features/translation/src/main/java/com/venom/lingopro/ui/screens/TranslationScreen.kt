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
import com.venom.lingopro.ui.viewmodel.TranslationActions
import com.venom.ui.components.dialogs.DraggableDialog
import com.venom.ui.components.dialogs.FullscreenTextDialog
import com.venom.ui.components.speech.SpeechToTextDialog
import com.venom.ui.screen.dictionary.DictionaryScreen
import com.venom.ui.screen.dictionary.TranslationsCard
import com.venom.ui.screen.langselector.LangSelectorViewModel
import com.venom.ui.viewmodel.STTViewModel
import com.venom.ui.viewmodel.TTSViewModel
import com.venom.ui.viewmodel.TranslateViewModel
import com.venom.utils.Extensions.copyToClipboard
import com.venom.utils.Extensions.pasteFromClipboard
import com.venom.utils.Extensions.shareText

@Composable
fun TranslationScreen(
    viewModel: TranslateViewModel = hiltViewModel(),
    ttsViewModel: TTSViewModel = hiltViewModel(),
    sttViewModel: STTViewModel = hiltViewModel(),
    langSelectorViewModel: LangSelectorViewModel = hiltViewModel(),
    onNavigateToOcr: () -> Unit = {},
    onNavigateToSentence: (String) -> Unit = {},
    initialText: String? = null,
    isDialog: Boolean = false,
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current

    // Collect states
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val langSelectorState by langSelectorViewModel.state.collectAsStateWithLifecycle()
    val ttsState by ttsViewModel.uiState.collectAsStateWithLifecycle()
    val transcription by sttViewModel.transcription.collectAsStateWithLifecycle()

    // Local UI state
    var sourceTextFieldValue by remember { mutableStateOf(TextFieldValue()) }
    val translatedTextFieldValue by remember { derivedStateOf { TextFieldValue(state.translatedText) } }
    var showDictionaryDialog by remember { mutableStateOf(false) }
    var showSpeechToTextDialog by remember { mutableStateOf(false) }
    var fullscreenState by remember { mutableStateOf<String?>(null) }

    // Initialize with initial text or restored state
    LaunchedEffect(Unit) {
        val textToSet = initialText?.takeIf { it.isNotBlank() } ?: state.sourceText
        if (textToSet.isNotBlank() && sourceTextFieldValue.text != textToSet) {
            sourceTextFieldValue = TextFieldValue(textToSet)
            // Always trigger translation for initial text
            viewModel.onSourceTextChanged(textToSet)
        }
    }

    // Sync languages when they change
    LaunchedEffect(langSelectorState.sourceLang, langSelectorState.targetLang) {
        viewModel.updateLanguages(
            langSelectorState.sourceLang,
            langSelectorState.targetLang
        )
    }

    // Update text field when ViewModel state changes (e.g., from swap operation)
    LaunchedEffect(state.sourceText) {
        if (state.sourceText != sourceTextFieldValue.text) {
            sourceTextFieldValue = TextFieldValue(
                text = state.sourceText,
                selection = sourceTextFieldValue.selection
            )
        }
    }

    // Handle speech-to-text result
    LaunchedEffect(transcription) {
        if (transcription.isNotBlank() && !showSpeechToTextDialog) {
            sourceTextFieldValue = TextFieldValue(transcription)
            viewModel.onSourceTextChanged(transcription)
            sttViewModel.stopRecognition()
        }
    }

    // Debug: Log state changes (remove in production)
    LaunchedEffect(state.sourceText, state.translatedText, state.isLoading, state.error) {
        println("TranslationScreen - Source: '${state.sourceText}', Translated: '${state.translatedText}', Loading: ${state.isLoading}, Error: ${state.error}")
    }

    // Create actions
    val actions = remember(context, viewModel, ttsViewModel) {
        TranslationActions(
            onTextChange = { newValue ->
                sourceTextFieldValue = newValue
                viewModel.onSourceTextChanged(newValue.text)
            },
            onClearText = {
                sourceTextFieldValue = TextFieldValue("")
                viewModel.clearAll()
            },
            onCopy = { text -> context.copyToClipboard(text) },
            onShare = { text -> context.shareText(text) },
            onSpeak = ttsViewModel::speak,
            onBookmark = viewModel::toggleBookmark,
            onSpeechToText = { showSpeechToTextDialog = true },
            onPaste = {
                context.pasteFromClipboard()?.let { text ->
                    if (text.isNotBlank() && text != sourceTextFieldValue.text) {
                        sourceTextFieldValue = TextFieldValue(text)
                        viewModel.onSourceTextChanged(text)
                    }
                }
            },
            onMoveUp = viewModel::moveUp,
            onOcr = onNavigateToOcr,
            onFullscreen = { fullscreenState = it },
            onSentenceExplorer = { word -> onNavigateToSentence(word) }
        )
    }

    @Composable
    fun TranslationContent(modifier: Modifier = Modifier) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TranslationSection(
                viewModel = langSelectorViewModel,
                sourceTextValue = sourceTextFieldValue,
                translatedTextValue = translatedTextFieldValue,
                actions = actions,
                isLoading = state.isLoading,
                isSpeaking = ttsState.isSpeaking,
                isBookmarked = state.isBookmarked
            )

            // Dictionary results
            if (state.dict.isNotEmpty()) {
                TranslationsCard(
                    translations = state.dict,
                    onWordClick = { selectedWord ->
                        if (selectedWord.isNotBlank() && selectedWord != sourceTextFieldValue.text) {
                            sourceTextFieldValue = TextFieldValue(selectedWord)
                            viewModel.onSourceTextChanged(selectedWord)
                        }
                    },
                    onExpand = { showDictionaryDialog = true },
                    onSpeak = ttsViewModel::speak,
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                )
            }
        }
    }

    // Main content
    if (isDialog) {
        DraggableDialog(onDismissRequest = onDismiss) {
            TranslationContent()
        }
    } else {
        TranslationContent(
            modifier = Modifier.padding(8.dp)
        )
    }

    // Dictionary dialog
    if (showDictionaryDialog && state.translationResult != null) {
        Dialog(
            onDismissRequest = { showDictionaryDialog = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false,
                dismissOnBackPress = true
            )
        ) {
            DictionaryScreen(
                translationResponse = state.translationResult!!,
                onWordClick = { selectedWord ->
                    if (selectedWord.isNotBlank()) {
                        sourceTextFieldValue = TextFieldValue(selectedWord)
                        viewModel.onSourceTextChanged(selectedWord)
                        showDictionaryDialog = false
                    }
                },
                onSpeak = ttsViewModel::speak,
                onCopy = actions.onCopy,
                onDismiss = { showDictionaryDialog = false }
            )
        }
    }

    // Speech-to-text dialog
    if (showSpeechToTextDialog) {
        SpeechToTextDialog(
            sttViewModel = sttViewModel,
            onDismiss = {
                showSpeechToTextDialog = false
                sttViewModel.stopRecognition()
            }
        )
    }

    // Fullscreen text dialog
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