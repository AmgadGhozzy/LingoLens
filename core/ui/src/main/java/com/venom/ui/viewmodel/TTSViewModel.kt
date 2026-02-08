package com.venom.ui.viewmodel

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.venom.data.repo.GroqTtsRepository
import com.venom.data.repo.SettingsRepository
import com.venom.utils.Extensions.cleanForTTS
import com.venom.utils.Extensions.sanitize
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.util.Locale
import javax.inject.Inject

@Immutable
data class TTSUiState(
    val isSpeaking: Boolean = false,
    val speakingText: String = "",
    val isSlowSpeed: Boolean = false,
    val error: String? = null
) {
    fun isSpeakingText(text: String) = isSpeaking && speakingText == text
}

@HiltViewModel
class TTSViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository,
    private val groqTtsRepository: GroqTtsRepository
) : ViewModel() {

    private var tts: TextToSpeech? = null
    private var mediaPlayer: MediaPlayer? = null
    private var exoPlayer: ExoPlayer? = null
    private var baseSpeechRate = 0.8f
    private var isTTSReady = false

    private val _uiState = MutableStateFlow(TTSUiState())
    val uiState: StateFlow<TTSUiState> = _uiState.asStateFlow()

    private companion object {
        const val NORMAL_RATE = 0.7f
        const val SLOW_RATE = 0.4f
        val ARABIC_LOCALE = Locale("ar", "SA")
        const val GROQ_VOICE_EN = "troy"
        const val GROQ_VOICE_AR = "noura"
        val ARABIC_VOICES = setOf("fahad", "sultan", "noura", "lulwa", "aisha")
        val ENGLISH_VOICES = setOf("autumn", "diana", "hannah", "austin", "daniel", "troy")
    }

    init {
        tts = TextToSpeech(context) { status ->
            isTTSReady = status == TextToSpeech.SUCCESS
            if (isTTSReady) setupListener()
        }
        viewModelScope.launch {
            settingsRepository.settings.collect { baseSpeechRate = it.speechRate }
        }
    }

    private fun setupListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(id: String) = setSpeaking(true)
            override fun onDone(id: String) = setSpeaking(false)
            override fun onError(id: String) = setSpeaking(false)
        })
    }

    // Click: start speaking or stop if same text already playing
    fun toggle(text: String, languageTag: String? = null) {
        if (text.isBlank()) return
        if (_uiState.value.isSpeakingText(text)) stop()
        else speak(text, languageTag, slow = false)
    }

    // Long click: speak at slow speed
    fun speakSlow(text: String, languageTag: String? = null) {
        if (text.isBlank()) return
        speak(text, languageTag, slow = true)
    }

    private fun speak(text: String, languageTag: String?, slow: Boolean) {
        viewModelScope.launch {
            stop()
            val clean = text.cleanForTTS().sanitize().lowercase()
            val locale = resolveLocale(languageTag, clean)
            val rate = baseSpeechRate * if (slow) SLOW_RATE else NORMAL_RATE

            _uiState.update { it.copy(speakingText = text, isSlowSpeed = slow) }

            val engine = tts
            if (engine != null && isTTSReady) {
                engine.language = locale
                engine.setSpeechRate(rate)
                if (engine.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE) {
                    engine.speak(clean, TextToSpeech.QUEUE_FLUSH, null, text.hashCode().toString())
                    return@launch
                }
            }
            // Device doesn't support this language — fall back to online
            speakOnline(clean, locale, slow)
        }
    }

    private fun speakOnline(text: String, locale: Locale, slow: Boolean) {
        viewModelScope.launch {
            try {
                val url = "https://st.tokhmi.xyz/api/tts/?engine=google&lang=$locale&text=${
                    URLEncoder.encode(text, "UTF-8")
                }"
                releaseMediaPlayer()
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(url)
                    setOnPreparedListener { mp ->
                        setSpeaking(true)
                        if (slow && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            mp.playbackParams = mp.playbackParams.setSpeed(SLOW_RATE)
                        }
                        mp.start()
                    }
                    setOnCompletionListener { setSpeaking(false); releaseMediaPlayer() }
                    setOnErrorListener { _, _, _ -> setSpeaking(false); releaseMediaPlayer(); true }
                    prepareAsync()
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isSpeaking = false, speakingText = "") }
            }
        }
    }

    fun speakWithGroq(text: String, locale: String, voice: String = GROQ_VOICE_EN) {
        viewModelScope.launch {
            try {
                stop()
                val resolvedLocale = resolveLocale(locale, text)
                val isArabic = resolvedLocale.language == "ar"
                val effectiveVoice = if (isArabic) {
                    if (voice in ARABIC_VOICES) voice else GROQ_VOICE_AR
                } else {
                    if (voice in ENGLISH_VOICES) voice else GROQ_VOICE_EN
                }

                val audioFile = groqTtsRepository.generateSpeech(text, resolvedLocale, effectiveVoice)
                if (!audioFile.exists()) return@launch

                withContext(Dispatchers.Main) {
                    releaseExoPlayer()
                    exoPlayer = ExoPlayer.Builder(context).build().apply {
                        addListener(object : Player.Listener {
                            override fun onPlaybackStateChanged(state: Int) {
                                if (state == Player.STATE_ENDED) {
                                    setSpeaking(false)
                                    releaseExoPlayer()
                                    viewModelScope.launch(Dispatchers.IO) { audioFile.delete() }
                                }
                            }
                            override fun onPlayerError(error: PlaybackException) {
                                setSpeaking(false)
                                releaseExoPlayer()
                                viewModelScope.launch(Dispatchers.IO) { audioFile.delete() }
                            }
                        })
                        setMediaItem(MediaItem.fromUri(Uri.fromFile(audioFile)))
                        prepare()
                        setSpeaking(true)
                        play()
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isSpeaking = false, speakingText = "") }
            }
        }
    }

    private fun resolveLocale(tag: String?, text: String) = when (tag) {
        "ar-SA" -> ARABIC_LOCALE
        "en-US" -> Locale.US
        else -> if (text.contains(Regex("[a-zA-Z]"))) Locale.US else ARABIC_LOCALE
    }

    private fun setSpeaking(speaking: Boolean) {
        _uiState.update {
            if (speaking) it.copy(isSpeaking = true)
            else it.copy(isSpeaking = false, speakingText = "", isSlowSpeed = false)
        }
    }

    fun stop() {
        tts?.stop()
        releaseMediaPlayer()
        releaseExoPlayer()
        setSpeaking(false)
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.apply {
            try { if (isPlaying) stop() } catch (_: Exception) {}
            release()
        }
        mediaPlayer = null
    }

    private fun releaseExoPlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }

    override fun onCleared() {
        stop()
        tts?.shutdown()
    }
}