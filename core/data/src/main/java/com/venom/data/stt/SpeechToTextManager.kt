package com.venom.data.stt

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.core.content.ContextCompat
import com.venom.domain.model.SpeechError
import com.venom.domain.model.SpeechState
import com.venom.domain.repo.stt.SpeechConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class SpeechToTextManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var speechRecognizer: SpeechRecognizer? = null
    private var recognitionJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _state = MutableStateFlow<SpeechState>(SpeechState.Idle)
    val state = _state.asStateFlow()

    private var currentConfig = SpeechConfig()
    private var isPaused = false
    private var lastPartialResult: String? = null


    private fun createRecognizerIntent() = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, currentConfig.language)
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, currentConfig.partialResults)
        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, currentConfig.maxResults)
    }

    suspend fun startListening(config: SpeechConfig = SpeechConfig()) =
        withContext(Dispatchers.Main) {
            if (speechRecognizer != null && !isPaused) return@withContext

            currentConfig = config
            if (!checkPreconditions()) return@withContext

            if (isPaused) {
                resumeListening()
            } else {
                initializeRecognizer()
            }
            startRecognitionTimeout()
        }


    suspend fun pauseListening() = withContext(Dispatchers.Main) {
        isPaused = true
        speechRecognizer?.stopListening()
        cancelTimeout()
        _state.value = SpeechState.Paused(lastPartialResult.toString())
    }

    private suspend fun resumeListening() = withContext(Dispatchers.Main) {
        isPaused = false
        speechRecognizer?.startListening(createRecognizerIntent())
        _state.value = SpeechState.Listening
    }

    private fun checkPreconditions(): Boolean {
        return when {
            !isPermissionGranted() -> {
                _state.value = SpeechState.Error(SpeechError.NO_PERMISSION)
                false
            }

            !SpeechRecognizer.isRecognitionAvailable(context) -> {
                _state.value = SpeechState.Error(SpeechError.NOT_AVAILABLE)
                false
            }

            else -> true
        }
    }

    private fun initializeRecognizer() {
        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(createListener())
                startListening(createRecognizerIntent())
            }
            _state.value = SpeechState.Listening
        } catch (e: Exception) {
            handleError(SpeechError.UNKNOWN)
        }
    }

    fun stopListening() = changeStateAndDestroyRecognizer(SpeechRecognizer::stopListening)
    fun cancelListening() = changeStateAndDestroyRecognizer(SpeechRecognizer::cancel)

    fun destroy() {
        cancelTimeout()
        changeStateAndDestroyRecognizer(SpeechRecognizer::destroy)
        recognitionJob?.cancel()
        coroutineScope.cancel()
    }

    private fun changeStateAndDestroyRecognizer(action: SpeechRecognizer.() -> Unit) {
        try {
            speechRecognizer?.action()
        } catch (e: Exception) {
            // Ignore exceptions during cleanup
        } finally {
            _state.value = SpeechState.Idle
            destroyRecognizer()
        }
    }

    private fun destroyRecognizer() {
        speechRecognizer?.destroy()
        speechRecognizer = null
        isPaused = false
        lastPartialResult = null
        cancelTimeout()
    }

    private fun startRecognitionTimeout() {
        cancelTimeout()
        recognitionJob = coroutineScope.launch {
            delay(currentConfig.recognitionTimeoutMs)
            if (speechRecognizer != null && !isPaused) {
                handleError(SpeechError.TIMEOUT)
            }
        }
    }

    private fun cancelTimeout() {
        recognitionJob?.cancel()
        recognitionJob = null
    }

    private fun handleError(error: SpeechError) {
        _state.value = SpeechState.Error(error)
        destroyRecognizer()

        if (error in listOf(SpeechError.NETWORK, SpeechError.BUSY)) {
            coroutineScope.launch {
                delay(currentConfig.retryDelayMs)
                startListening(currentConfig)
            }
        }
    }

    private fun isPermissionGranted() = ContextCompat.checkSelfPermission(
        context, Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED

    private fun createListener() = object : RecognitionListener {
        override fun onPartialResults(partialResults: Bundle?) {
            if (isPaused) return

            partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                ?.let { text ->
                    lastPartialResult = text
                    _state.value = SpeechState.Partial(text)
                }
        }

        override fun onReadyForSpeech(params: Bundle?) {
            if (!isPaused) _state.value = SpeechState.Listening
        }

        override fun onEndOfSpeech() {
            if (!isPaused) {
                cancelTimeout()
            }
        }

        override fun onResults(results: Bundle?) {
            if (isPaused) return

            results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                ?.let { text ->
                    _state.value = SpeechState.Result(text)
                } ?: run {
                _state.value = SpeechState.Error(SpeechError.NO_MATCH)
            }
            destroyRecognizer()
        }

        override fun onError(error: Int) {
            if (!isPaused) handleError(mapError(error))
        }

        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    private fun mapError(errorCode: Int): SpeechError = when (errorCode) {
        SpeechRecognizer.ERROR_AUDIO -> SpeechError.AUDIO
        SpeechRecognizer.ERROR_NETWORK -> SpeechError.NETWORK
        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> SpeechError.TIMEOUT
        SpeechRecognizer.ERROR_NO_MATCH -> SpeechError.NO_MATCH
        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> SpeechError.BUSY
        SpeechRecognizer.ERROR_SERVER -> SpeechError.SERVER
        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> SpeechError.NO_INPUT
        else -> SpeechError.UNKNOWN
    }
}