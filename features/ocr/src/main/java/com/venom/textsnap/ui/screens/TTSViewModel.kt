package com.venom.textsnap.ui.screens

import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.textsnap.data.tts.MediaPlayerFactory
import com.venom.textsnap.data.tts.TTSUrlGenerator
import com.venom.textsnap.data.tts.TextToSpeechFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TTSViewModel @Inject constructor(
    private val textToSpeechFactory: TextToSpeechFactory,
    private val mediaPlayerFactory: MediaPlayerFactory,
    private val urlGenerator: TTSUrlGenerator
) : ViewModel() {
    private var tts: TextToSpeech? = null
    private var mediaPlayer: MediaPlayer? = null

    private val _state = MutableStateFlow<TTSState>(TTSState.Idle)
    val state = _state.asStateFlow()

    init {
        initTTS() // Initialize TextToSpeech
    }

    private fun initTTS() {
        tts = textToSpeechFactory.create { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String) {
                        _state.value = TTSState.Speaking(utteranceId)
                    }

                    override fun onDone(utteranceId: String) {
                        _state.value = TTSState.Idle
                    }

                    override fun onError(utteranceId: String) {
                        _state.value = TTSState.Error("TTS failed")
                    }
                })
            }
        }
    }

    fun speak(text: String, source: TextSource = TextSource.SOURCE) {
        if (text.isEmpty()) return
        viewModelScope.launch {
            stopSpeaking()
            val locale = determineLocale(text)
            try {
                when (tts?.isLanguageAvailable(locale)) {
                    TextToSpeech.LANG_AVAILABLE -> speakLocally(text, locale, source.id)
                    else -> speakOnline(text, locale, source.id)
                }
            } catch (e: Exception) {
                _state.value = TTSState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun determineLocale(text: String): Locale {
        return if (text.matches(Regex("[a-zA-Z]+"))) Locale.ENGLISH else Locale("ar")
    }

    // Speaks text using local TTS
    private fun speakLocally(text: String, locale: Locale, sourceId: String) {
        try {
            tts?.language = locale
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, sourceId)
                ?: throw Exception("TTS is null")
        } catch (e: Exception) {
            speakOnline(text, locale, sourceId) // Fallback to online if local fails
        }
    }

    private fun speakOnline(text: String, locale: Locale, sourceId: String) {
        try {
            mediaPlayer?.release()
            mediaPlayer = mediaPlayerFactory.create().apply {
                val url = urlGenerator.generateTTSUrl(text, locale)
                setDataSource(url)
                setOnPreparedListener { _state.value = TTSState.Speaking(sourceId); start() }
                setOnCompletionListener {
                    _state.value = TTSState.Idle; release(); mediaPlayer = null
                }
                setOnErrorListener { _, _, _ ->
                    _state.value = TTSState.Error("Audio playback failed")
                    release(); mediaPlayer = null
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            _state.value = TTSState.Error("Media player error: ${e.message}")
        }
    }

    fun stopSpeaking() {
        tts?.stop()
        mediaPlayer?.apply { if (isPlaying) stop(); release() }
        mediaPlayer = null
        _state.value = TTSState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        stopSpeaking()
        tts?.shutdown()
    }
}

sealed class TTSState {
    object Idle : TTSState()
    data class Speaking(val sourceId: String) : TTSState()
    data class Error(val message: String) : TTSState()
}

enum class TextSource(val id: String) {
    SOURCE("source_text"), TRANSLATED("translated_text")
}