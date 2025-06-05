package com.venom.dialog.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.dialog.data.local.model.DialogMessage
import com.venom.dialog.ui.component.sections.*
import com.venom.dialog.ui.viewmodel.DialogViewModel
import com.venom.domain.model.*
import com.venom.ui.screen.langselector.LangSelectorViewModel
import com.venom.ui.viewmodel.*
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun DialogScreen(
    dialogViewModel: DialogViewModel = hiltViewModel(),
    translateViewModel: TranslateViewModel = hiltViewModel(),
    ttsViewModel: TTSViewModel = hiltViewModel(),
    sttViewModel: STTViewModel = hiltViewModel(),
    langSelectorViewModel: LangSelectorViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val dialogUiState by dialogViewModel.uiState.collectAsState()
    val languageState by langSelectorViewModel.state.collectAsState()
    val speechState by sttViewModel.speechState.collectAsState()
    val translateState by translateViewModel.uiState.collectAsState()
    val transcription by sttViewModel.transcription.collectAsState()

    var activeLanguageCode by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(languageState) {
        translateViewModel.updateLanguages(languageState.sourceLang, languageState.targetLang)
    }

    LaunchedEffect(transcription) {
        if (transcription.isNotBlank()) {
            translateViewModel.onSourceTextChanged(transcription)
        }
    }

    LaunchedEffect(speechState) {
        when (speechState) {
            is SpeechState.Paused -> {
                val isSourceLanguage = activeLanguageCode == languageState.sourceLang.code
                val message = DialogMessage(
                    id = UUID.randomUUID().toString(),
                    sourceText = transcription,
                    translatedText = translateState.translatedText,
                    sourceLanguageCode = if (isSourceLanguage) languageState.sourceLang.code else languageState.targetLang.code,
                    sourceLanguageName = if (isSourceLanguage) languageState.sourceLang.englishName else languageState.targetLang.englishName,
                    targetLanguageCode = if (isSourceLanguage) languageState.targetLang.code else languageState.sourceLang.code,
                    targetLanguageName = if (isSourceLanguage) languageState.targetLang.englishName else languageState.sourceLang.englishName,
                    isSender = isSourceLanguage
                )
                dialogViewModel.addMessage(message)
                sttViewModel.stopRecognition()
                scope.launch {
                    ttsViewModel.speak(
                        text = if (isSourceLanguage) message.translatedText else message.sourceText,
                        language = if (isSourceLanguage) message.sourceLanguageCode else message.targetLanguageCode
                    )
                }
            }
            is SpeechState.Idle -> activeLanguageCode = null
            else -> Unit
        }
    }

    LaunchedEffect(dialogUiState.messages.size) {
        if (dialogUiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        ChatMessages(
            messages = dialogUiState.messages,
            listState = listState,
            onPlayClick = { message ->
                scope.launch {
                    ttsViewModel.speak(
                        text = message.translatedText,
                        language = message.targetLanguageCode
                    )
                }
            },
            onClearHistory = { dialogViewModel.clearAllMessages() },
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            VoiceLanguageBar(
                viewModel = langSelectorViewModel,
                voiceBarState = VoiceBarState(
                    isSourceListening = speechState is SpeechState.Listening && activeLanguageCode == languageState.sourceLang.code,
                    isTargetListening = speechState is SpeechState.Listening && activeLanguageCode == languageState.targetLang.code
                ),
                onSourceMicClick = {
                    handleMicClick(
                        speechState is SpeechState.Listening && activeLanguageCode == languageState.sourceLang.code,
                        sttViewModel,
                        languageState.sourceLang.code
                    ) { activeLanguageCode = languageState.sourceLang.code }
                },
                onTargetMicClick = {
                    handleMicClick(
                        speechState is SpeechState.Listening && activeLanguageCode == languageState.targetLang.code,
                        sttViewModel,
                        languageState.targetLang.code
                    ) { activeLanguageCode = languageState.targetLang.code }
                }
            )
        }

        if (dialogUiState.isLoading || translateState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

private fun handleMicClick(
    isListening: Boolean,
    sttViewModel: STTViewModel,
    languageCode: String,
    onStartListening: () -> Unit
) {
    if (isListening) {
        sttViewModel.pauseRecognition()
    } else {
        onStartListening()
        sttViewModel.startRecognition(SpeechConfig(language = languageCode))
    }
}