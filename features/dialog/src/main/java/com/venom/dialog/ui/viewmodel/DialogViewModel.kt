package com.venom.dialog.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.dialog.data.local.model.DialogMessage
import com.venom.dialog.data.repo.DialogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DialogUiState(
    val messages: List<DialogMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSourceListening: Boolean = false,
    val isTargetListening: Boolean = false
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
            val initialMessages = listOf(
                DialogMessage(
                    id = "welcome_1",
                    sourceText = "Welcome to LingoLens!",
                    translatedText = "مرحباً بك في LingoLens!",
                    sourceLanguageCode = "en",
                    sourceLanguageName = "English",
                    targetLanguageCode = "ar",
                    targetLanguageName = "Arabic",
                    isSender = false
                ), DialogMessage(
                    id = "welcome_2",
                    sourceText = "Tap the microphone button and start speaking to translate",
                    translatedText = "انقر على زر الميكروفون وابدأ في التحدث للترجمة",
                    sourceLanguageCode = "en",
                    sourceLanguageName = "English",
                    targetLanguageCode = "ar",
                    targetLanguageName = "Arabic",
                    isSender = true
                ), DialogMessage(
                    id = "welcome_1",
                    sourceText = "Welcome to LingoLens!",
                    translatedText = "مرحباً بك في LingoLens!",
                    sourceLanguageCode = "en",
                    sourceLanguageName = "English",
                    targetLanguageCode = "ar",
                    targetLanguageName = "Arabic",
                    timestamp = System.currentTimeMillis(),
                    isSender = true
                ), DialogMessage(
                    id = "welcome_2",
                    sourceText = "Tap the microphone button and start speaking to translate",
                    translatedText = "انقر على زر الميكروفون وابدأ في التحدث للترجمة",
                    sourceLanguageCode = "en",
                    sourceLanguageName = "English",
                    targetLanguageCode = "ar",
                    targetLanguageName = "Arabic",
                    isSender = false
                )
            )
            initialMessages.forEach { addMessage(it) }
        }
    }

    private fun loadMessages() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.getAllMessages().catch { e ->
                _uiState.value = _uiState.value.copy(
                    error = e.message, isLoading = false
                )
            }.collect { messagesList ->
                _uiState.value = _uiState.value.copy(messages = messagesList)
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun addMessage(message: DialogMessage) {
        viewModelScope.launch {
            try {
                repository.insertMessage(message)
                // Refresh messages after insert
                loadMessages()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearAllMessages() {
        viewModelScope.launch {
            try {
                repository.deleteAllMessages()
                loadMessages()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}