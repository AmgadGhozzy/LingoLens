package com.venom.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.domain.model.SpeechState
import com.venom.domain.repo.stt.ISpeechToTextRepository
import com.venom.domain.repo.stt.SpeechConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class STTViewModel @Inject constructor(
    private val repository: ISpeechToTextRepository
) : ViewModel() {

    val speechState: StateFlow<SpeechState> = repository.state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SpeechState.Idle
    )

    private var isActive = true
    private var currentConfig = SpeechConfig()

    fun updateConfig(config: SpeechConfig) {
        currentConfig = config
    }

    fun onResume() {
        isActive = true
    }

    fun onPause() {
        isActive = false
        cancelRecognition()
    }

    suspend fun startRecognition() {
        if (isActive) {
            repository.startListening(currentConfig)
        }
    }

    fun pauseRecognition() {
        if (isActive) {
            viewModelScope.launch {
                repository.pauseListening()
            }
        }
    }

    fun stopRecognition() {
        viewModelScope.launch {
            repository.stopListening()
        }
    }

    fun cancelRecognition() {
        viewModelScope.launch {
            repository.cancelListening()
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            repository.destroy()
        }
    }

}