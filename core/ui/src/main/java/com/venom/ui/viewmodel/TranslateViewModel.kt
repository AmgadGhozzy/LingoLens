package com.venom.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.data.model.TranslationUiState
import com.venom.data.repo.TranslationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TranslateViewModel @Inject constructor(
    private val repository: TranslationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TranslationUiState())
    val uiState: StateFlow<TranslationUiState> = _uiState.asStateFlow()

    fun onUserInputChanged(input: String) {
        _uiState.update { it.copy(userInput = input) }
        if (input.isNotBlank()) {
            translate(input)
        } else {
            _uiState.update { it.copy(translationResult = null) }
        }
    }

    fun updateSourceLanguage(languageCode: String) {
        _uiState.update { it.copy(sourceLanguage = languageCode) }
        _uiState.value.userInput.takeIf { it.isNotBlank() }?.let { translate(it) }
    }

    fun updateTargetLanguage(languageCode: String) {
        _uiState.update { it.copy(targetLanguage = languageCode) }
        _uiState.value.userInput.takeIf { it.isNotBlank() }?.let { translate(it) }
    }

    private fun translate(input: String) {
        Log.d("TranslateViewModel", "Input: $input")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            repository.getTranslation(
                _uiState.value.sourceLanguage, _uiState.value.targetLanguage, input
            ).onSuccess { response ->
                _uiState.update {
                    it.copy(
                        translationResult = response, isLoading = false, error = null
                    )
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false, error = exception.message ?: "Unknown error occurred"
                    )
                }
            }
        }
    }
}
