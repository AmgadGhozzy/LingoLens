package com.venom.dialog.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.dialog.data.model.DialogMessage
import com.venom.dialog.ui.component.sections.ChatMessages
import com.venom.dialog.ui.component.sections.VoiceInputControls
import com.venom.domain.model.LanguageItem
import com.venom.domain.model.SpeechConfig
import com.venom.domain.model.SpeechState
import com.venom.ui.viewmodel.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogScreen(
    translateViewModel: TranslateViewModel = hiltViewModel(),
    ttsViewModel: TTSViewModel = hiltViewModel(),
    sttViewModel: STTViewModel = hiltViewModel(),
    langSelectorViewModel: LangSelectorViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    var messages by rememberSaveable(stateSaver = DialogMessage.getSaver()) {
        mutableStateOf(
            listOf(
                DialogMessage(
                    sourceText = "How are you.",
                    translatedText = "مرحبا.",
                    sourceLanguage = LanguageItem("en", "English"),
                    targetLanguage = LanguageItem("ar", "Arabic"),
                    isSender = false
                ), DialogMessage(
                    sourceText = "What is your name?",
                    translatedText = "ما هو اسمك؟",
                    sourceLanguage = LanguageItem("en", "English"),
                    targetLanguage = LanguageItem("ar", "Arabic"),
                    isSender = true
                ), DialogMessage(
                    sourceText = "Where are you from?",
                    translatedText = "من اين انت؟",
                    sourceLanguage = LanguageItem("en", "English"),
                    targetLanguage = LanguageItem("ar", "Arabic"),
                    isSender = false
                ), DialogMessage(
                    sourceText = "How old are you?",
                    translatedText = "كم عمرك؟",
                    sourceLanguage = LanguageItem("en", "English"),
                    targetLanguage = LanguageItem("ar", "Arabic"),
                    isSender = true
                )
            )
        )
    }

    val languageState by langSelectorViewModel.state.collectAsState()
    val speechState by sttViewModel.speechState.collectAsState()
    val transcription by sttViewModel.transcription.collectAsState()
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LaunchedEffect(transcription) {
        if (transcription.isNotBlank()) {
            translateViewModel.onUserInputChanged(transcription)
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                ChatMessages(messages = messages, listState = listState, onPlayClick = { message ->
                    scope.launch {
                        ttsViewModel.speak(message.translatedText, message.targetLanguage.code)
                    }
                }, onClearHistory = { messages = emptyList() })
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                VoiceInputControls(onSourceLanguageClick = {
                    langSelectorViewModel.setSelectingSourceLanguage(
                        true
                    )
                },
                    onTargetLanguageClick = { langSelectorViewModel.setSelectingSourceLanguage(false) },
                    sourceLanguage = languageState.sourceLang.name,
                    targetLanguage = languageState.targetLang.name,
                    isListening = speechState is SpeechState.Listening,
                    onMicClick = {
                        if (speechState is SpeechState.Listening) {
                            sttViewModel.stopRecognition()
                        } else {
                            sttViewModel.startRecognition(SpeechConfig(languageState.sourceLang.code))
                        }
                    })
            }
        }
    }
}
