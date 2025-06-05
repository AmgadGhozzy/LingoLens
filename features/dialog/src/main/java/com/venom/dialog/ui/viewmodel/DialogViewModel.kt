package com.venom.dialog.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.dialog.data.local.model.DialogMessage
import com.venom.dialog.data.repo.DialogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DialogUiState(
    val messages: List<DialogMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DialogViewModel @Inject constructor(
    private val repository: DialogRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DialogUiState())
    val uiState: StateFlow<DialogUiState> = _uiState

    init {
        loadMessages()
        viewModelScope.launch {
            listOf(
                DialogMessage(
                    id = "welcome_1",
                    sourceText = "Welcome to LingoLens!",
                    translatedText = "مرحباً بك في LingoLens!",
                    sourceLanguageCode = "en",
                    sourceLanguageName = "English",
                    targetLanguageCode = "ar",
                    targetLanguageName = "Arabic",
                    isSender = false
                ),
                DialogMessage(
                    id = "welcome_2",
                    sourceText = "Tap the microphone button and start speaking to translate",
                    translatedText = "انقر على زر الميكروفون وابدأ في التحدث للترجمة",
                    sourceLanguageCode = "en",
                    sourceLanguageName = "English",
                    targetLanguageCode = "ar",
                    targetLanguageName = "Arabic",
                    isSender = true
                )
            ).forEach { addMessage(it) }
        }
    }

    private fun loadMessages() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.getAllMessages()
                .catch { _uiState.value = _uiState.value.copy(error = it.message, isLoading = false) }
                .collect { messages ->
                    _uiState.value = _uiState.value.copy(messages = messages, isLoading = false)
                }
        }
    }

    fun addMessage(message: DialogMessage) {
        viewModelScope.launch {
            repository.insertMessage(message)
            loadMessages()
        }
    }

    fun clearAllMessages() {
        viewModelScope.launch {
            repository.deleteAllMessages()
            loadMessages()
        }
    }
}