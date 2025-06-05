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
    onNavigateToOcr: () -> Unit,
    initialText: String? = null,
    isDialog: Boolean = false,
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    val langSelectorState by langSelectorViewModel.state.collectAsStateWithLifecycle()
    val ttsState by ttsViewModel.uiState.collectAsStateWithLifecycle()
    val transcription by sttViewModel.transcription.collectAsStateWithLifecycle()

    var sourceTextFieldValue by remember { mutableStateOf(TextFieldValue(initialText ?: state.sourceText)) }
    var showDictionaryDialog by remember { mutableStateOf(false) }
    var showSpeechToTextDialog by remember { mutableStateOf(false) }
    var fullscreenState by remember { mutableStateOf<String?>(null) }

    // Handle initial text only once
    LaunchedEffect(initialText) {
        initialText?.let { text ->
            if (text.isNotBlank() && sourceTextFieldValue.text != text) {
                sourceTextFieldValue = TextFieldValue(text)
                viewModel.onSourceTextChanged(text)
            }
        }
    }

    // Update source text field when state changes (e.g., from moveUp)
    LaunchedEffect(state.sourceText) {
        if (state.sourceText != sourceTextFieldValue.text) {
            sourceTextFieldValue = TextFieldValue(
                text = state.sourceText,
                selection = sourceTextFieldValue.selection
            )
        }
    }

    // Handle language changes - only update if languages actually changed
    LaunchedEffect(langSelectorState.sourceLang, langSelectorState.targetLang) {
        viewModel.updateLanguages(langSelectorState.sourceLang, langSelectorState.targetLang)
    }

    val actions = remember(context) {
        TranslationActions(
            onTextChange = { newValue ->
                // Only update if text actually changed
                if (newValue.text != sourceTextFieldValue.text) {
                    sourceTextFieldValue = newValue
                    viewModel.onSourceTextChanged(newValue.text)
                } else {
                    // Update only selection/composition without triggering translation
                    sourceTextFieldValue = newValue
                }
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
                    if (text != sourceTextFieldValue.text) {
                        sourceTextFieldValue = TextFieldValue(text)
                        viewModel.onSourceTextChanged(text)
                    }
                }
            },
            onMoveUp = viewModel::moveUp,
            onOcr = onNavigateToOcr,
            onFullscreen = { fullscreenState = it }
        )
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
                isBookmarked = state.isBookmarked
            )

            if (!isDialog && state.synonyms.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                state.translationResult.dict?.let { translations ->
                    TranslationsCard(
                        translations = translations,
                        onWordClick = { selectedWord ->
                            if (selectedWord != sourceTextFieldValue.text) {
                                sourceTextFieldValue = TextFieldValue(selectedWord)
                                viewModel.onSourceTextChanged(selectedWord)
                            }
                        },
                        onExpand = { showDictionaryDialog = true },
                        onSpeak = ttsViewModel::speak,
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    )
                }
            }
        }
    }

    if (isDialog) DraggableDialog(onDismissRequest = onDismiss) { TranslationContent() }
    else TranslationContent(modifier = Modifier.padding(8.dp).fillMaxSize())

    if (showDictionaryDialog) Dialog(
        onDismissRequest = { showDictionaryDialog = false },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
            dismissOnBackPress = true
        )
    ) {
        DictionaryScreen(
            translationResponse = state.translationResult,
            onWordClick = { selectedWord ->
//                sourceTextFieldValue = TextFieldValue(selectedWord)
//                viewModel.onSourceTextChanged(selectedWord)
            },
            onSpeak = ttsViewModel::speak,
            onCopy = actions.onCopy,
            onDismiss = { showDictionaryDialog = false }
        )
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
        }
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