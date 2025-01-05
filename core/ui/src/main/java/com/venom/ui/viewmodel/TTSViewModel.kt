package com.venom.ui.viewmodel

import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.domain.repo.tts.MediaPlayerFactory
import com.venom.domain.repo.tts.TTSUrlGenerator
import com.venom.domain.repo.tts.TextToSpeechFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

data class TTSUiState(
    val isInitializing: Boolean = false,
    val isSpeaking: Boolean = false,
    val currentText: String = "",
    val error: String? = null
)

@HiltViewModel
class TTSViewModel @Inject constructor(
    private val textToSpeechFactory: TextToSpeechFactory,
    private val mediaPlayerFactory: MediaPlayerFactory,
    private val urlGenerator: TTSUrlGenerator
) : ViewModel() {
    private var tts: TextToSpeech? = null
    private var mediaPlayer: MediaPlayer? = null

    private val _uiState = MutableStateFlow(TTSUiState())
    val uiState: StateFlow<TTSUiState> = _uiState.asStateFlow()

    init {
        initTTS() // Initialize TextToSpeech
    }

    private fun initTTS() {
        _uiState.update { it.copy(isInitializing = true) }
        tts = textToSpeechFactory.create { status ->
            _uiState.update { it.copy(isInitializing = false) }
            if (status == TextToSpeech.SUCCESS) {
                setupTTSListener()
            } else {
                _uiState.update {
                    it.copy(error = "Failed to initialize TTS")
                }
            }
        }
    }

    private fun setupTTSListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String) {
                updateSpeakingState(true)
            }

            override fun onDone(utteranceId: String) {
                updateSpeakingState(false)
            }

            override fun onError(utteranceId: String) {
                handleError("TTS failed")
            }
        })
    }

    fun speak(text: String, language: String? = null) {
        if (text.isEmpty()) return

        // If currently speaking the same text, stop it
        if (uiState.value.isSpeaking && uiState.value.currentText == text) {
            stopSpeaking()
            return
        }

        viewModelScope.launch {
            try {
                stopSpeaking() // Stop any previous speech
                _uiState.update { it.copy(currentText = text) }

                val locale = language?.let { Locale(it) } ?: determineLocale(text)
                when (tts?.isLanguageAvailable(locale)) {
                    TextToSpeech.LANG_AVAILABLE -> speakLocally(text, locale)
                    else -> speakOnline(text, locale)
                }
            } catch (e: Exception) {
                handleError(e.message ?: "Unknown error")
            }
        }
    }

    private fun determineLocale(text: String): Locale =
        if (text.matches(Regex("[a-zA-Z]+"))) Locale.ENGLISH else Locale("ar")

    // Speaks text using local TTS
    private fun speakLocally(text: String, locale: Locale) {
        try {
            tts?.apply {
                language = locale
                speak(text, TextToSpeech.QUEUE_FLUSH, null, text.hashCode().toString())
            } ?: throw Exception("TTS is null")
        } catch (e: Exception) {
            speakOnline(text, locale)
        }
    }

    private fun speakOnline(text: String, locale: Locale) {
        try {
            releaseMediaPlayer()
            mediaPlayer = mediaPlayerFactory.create().apply {
                val url = urlGenerator.generateTTSUrl(text, locale)
                setDataSource(url)
                setOnPreparedListener {
                    updateSpeakingState(true)
                    start()
                }
                setOnCompletionListener {
                    updateSpeakingState(false)
                    releaseMediaPlayer()
                }
                setOnErrorListener { _, _, _ ->
                    handleError("Audio playback failed")
                    releaseMediaPlayer()
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            handleError("Media player error: ${e.message}")
        }
    }

    private fun updateSpeakingState(isSpeaking: Boolean) {
        _uiState.update { it.copy(isSpeaking = isSpeaking) }
    }

    private fun handleError(message: String) {
        _uiState.update {
            it.copy(
                error = message, isSpeaking = false
            )
        }
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
    }

    fun stopSpeaking() {
        tts?.stop()
        releaseMediaPlayer()
        updateSpeakingState(false)
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        stopSpeaking()
        tts?.shutdown()
    }
}
