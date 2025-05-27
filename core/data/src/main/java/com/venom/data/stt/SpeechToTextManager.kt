package com.venom.data.stt

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.core.content.ContextCompat
import com.venom.domain.model.SpeechConfig
import com.venom.domain.model.SpeechError
import com.venom.domain.model.SpeechState
import com.venom.domain.repo.stt.ISpeechToTextRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeechToTextManager @Inject constructor(
    @ApplicationContext private val context: Context
) : ISpeechToTextRepository {
    private var speechRecognizer: SpeechRecognizer? = null
    private val _state = MutableStateFlow<SpeechState>(SpeechState.Idle)
    override val state: StateFlow<SpeechState> = _state.asStateFlow()

    private var isListening = false
    private var isPaused = false
    private var currentConfig = SpeechConfig()
    private var lastPartialResult = ""

    private var hasPermission: Boolean = false
    private var isRecognitionAvailable: Boolean = false

    init {
        // Check permission and availability once during initialization
        checkPermissionAndAvailability()
    }

    /**
     * Creates and configures an intent for speech recognition.
     */
    private fun createRecognizerIntent() = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, currentConfig.language)
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, currentConfig.partialResults)
        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, currentConfig.maxResults)
        putExtra(
            RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
            currentConfig.completeSilenceLengthMs
        )
        putExtra(
            RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, currentConfig.minimumLengthMs
        )
        putExtra(
            RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
            currentConfig.possiblyCompleteSilenceLengthMs
        )
    }

    /**
     * Starts listening to the user's voice with the given configuration.
     */
    override suspend fun startListening(config: SpeechConfig) {
        if (!checkPermissionAndAvailability()) return

        currentConfig = config
        isListening = true
        isPaused = false

        initializeRecognizer()
    }

    /**
     * Pauses the current listening session.
     */
    override suspend fun pauseListening() {
        if (!isListening) return

        isPaused = true
        isListening = false
        speechRecognizer?.stopListening()
        _state.value = SpeechState.Paused(lastPartialResult)
    }

    /**
     * Stops the current listening session.
     */
    override suspend fun stopListening() {
        isListening = false
        isPaused = false
        lastPartialResult = ""
        destroy()
        _state.value = SpeechState.Idle
    }

    /**
     * Checks for audio recording permissions and availability of speech recognition services.
     */
    private fun checkPermissionAndAvailability(): Boolean {
        hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        isRecognitionAvailable = SpeechRecognizer.isRecognitionAvailable(context)

        return when {
            !hasPermission -> {
                _state.value = SpeechState.Error(SpeechError.NO_PERMISSION)
                false
            }

            !isRecognitionAvailable -> {
                _state.value = SpeechState.Error(SpeechError.NOT_AVAILABLE)
                false
            }

            else -> true
        }
    }

    /**
     * Initializes the SpeechRecognizer and starts listening.
     */
    private fun initializeRecognizer() {
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(createListener())
            }
        }
        try {
            speechRecognizer?.startListening(createRecognizerIntent())
            _state.value = SpeechState.Listening
        } catch (e: Exception) {
            _state.value = SpeechState.Error(SpeechError.UNKNOWN)
            Log.e("SpeechToTextManager", "Error initializing SpeechRecognizer: ${e.message}")
        }
    }

    /**
     * Creates a RecognitionListener to handle speech recognition events.
     */
    private fun createListener() = object : RecognitionListener {
        override fun onPartialResults(partialResults: Bundle?) {
            if (!isListening) return

            partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                ?.let { text ->
                    lastPartialResult = text
                    _state.value = SpeechState.Partial(text)
                }
        }

        override fun onResults(results: Bundle?) {
            if (!isListening) return

            results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                ?.let { text ->
                    _state.value = SpeechState.Result(text)
                    if (isListening) {
                        initializeRecognizer()
                    }
                } ?: run {
                if (isListening) {
                    initializeRecognizer()
                }
            }
        }

        override fun onError(error: Int) {
            handleRecognizerError(error)
            if (isListening) {
                initializeRecognizer()
            }
        }

        override fun onReadyForSpeech(params: Bundle?) {
            if (isListening) _state.value = SpeechState.Listening
        }

        // Required overrides
        override fun onBeginningOfSpeech() {/**/}
        override fun onEndOfSpeech() {/**/}
        override fun onRmsChanged(rmsdB: Float) {/**/}
        override fun onBufferReceived(buffer: ByteArray?) {/**/}
        override fun onEvent(eventType: Int, params: Bundle?) {/**/}
    }

    /**
     * Handles errors from the SpeechRecognizer.
     */
    private fun handleRecognizerError(error: Int) {
        val speechError = when (error) {
            SpeechRecognizer.ERROR_NO_MATCH -> SpeechError.NO_MATCH
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                if (lastPartialResult.isNotEmpty())
                    _state.value = SpeechState.Result(lastPartialResult)

                SpeechError.NO_INPUT
            }

            SpeechRecognizer.ERROR_AUDIO -> SpeechError.AUDIO
            SpeechRecognizer.ERROR_NETWORK -> SpeechError.NETWORK
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> SpeechError.TIMEOUT
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> SpeechError.BUSY
            SpeechRecognizer.ERROR_SERVER -> SpeechError.SERVER
            else -> SpeechError.UNKNOWN
        }
        _state.value = SpeechState.Error(speechError)
    }

    /**
     * Destroys the SpeechRecognizer instance.
     */
    override suspend fun destroy() {
        isListening = false
        isPaused = false
        lastPartialResult = ""
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}