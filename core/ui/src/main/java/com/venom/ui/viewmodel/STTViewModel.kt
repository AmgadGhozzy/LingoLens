package com.venom.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.data.stt.SpeechToTextRepository
import com.venom.domain.model.SpeechConfig
import com.venom.domain.model.SpeechState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class STTViewModel @Inject constructor(
    private val repository: SpeechToTextRepository
) : ViewModel() {

    private val _transcription = MutableStateFlow("")
    val transcription = _transcription.asStateFlow()

    private var isRecognitionActive = false

    val speechState = repository.state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SpeechState.Idle
    )

    init {
        viewModelScope.launch {
            speechState.collect { state ->
                when (state) {
                    is SpeechState.Result -> appendTranscription(state.text)

                    is SpeechState.Paused -> if (state.lastResult.isNotBlank()) appendTranscription(
                        state.lastResult
                    )

                    else -> Unit
                }
            }
        }
    }

    private fun appendTranscription(text: String) {
        if (text.isNotBlank()) {
            _transcription.value = buildString {
                append(_transcription.value)
                if (isNotEmpty() && !endsWith(" ")) append(" ")
                append(text)
            }.trim()
        }
    }

    fun startRecognition(config: SpeechConfig = SpeechConfig()) {
        isRecognitionActive = true
        viewModelScope.launch {
            repository.startListening(config)
        }
    }

    fun pauseRecognition() {
        isRecognitionActive = false
        viewModelScope.launch {
            repository.pauseListening()
        }
    }

    fun stopRecognition() {
        isRecognitionActive = false
        viewModelScope.launch {
            repository.stopListening()
            _transcription.value = ""
            repository.destroy()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            repository.destroy()
        }
    }
}