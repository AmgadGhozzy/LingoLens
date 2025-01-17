package com.venom.lingopro.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.data.mapper.extractSynonyms
import com.venom.data.model.TranslationEntry
import com.venom.data.model.TranslationResponse
import com.venom.data.repo.TranslationRepository
import com.venom.domain.model.LANGUAGES_LIST
import com.venom.domain.model.LanguageItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TranslationUiState(
    val sourceLanguage: LanguageItem = LanguageItem("en", "English"),
    val targetLanguage: LanguageItem = LanguageItem("ar", "Arabic"),
    val sourceText: String = "",
    val translatedText: String = "",
    val translationResult: TranslationResponse = TranslationResponse(),
    val synonyms: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isNetworkAvailable: Boolean = true,
    val isBookmarked: Boolean = false,
    val translationHistory: List<TranslationEntry> = emptyList(),
    val availableLanguages: List<LanguageItem> = LANGUAGES_LIST
)

@HiltViewModel
class TranslateViewModel @Inject constructor(
    private val repository: TranslationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TranslationUiState())
    val uiState: StateFlow<TranslationUiState> = _uiState.asStateFlow()

    fun onSourceTextChanged(input: String) {
        if (input.isBlank()) {
            _uiState.update {
                it.copy(
                    sourceText = input,
                    translationResult = TranslationResponse(),
                    translatedText = "",
                    synonyms = emptyList()
                )
            }
            return
        }

        if (input != _uiState.value.sourceText) {
            _uiState.update { it.copy(sourceText = input) }
            translate(input)
        }
    }

    fun updateLanguages(
        sourceLanguage: LanguageItem, targetLanguage: LanguageItem
    ) {
        val currentState = _uiState.value
        if (sourceLanguage == currentState.sourceLanguage && targetLanguage == currentState.targetLanguage) {
            return
        }

        _uiState.update {
            it.copy(
                sourceLanguage = sourceLanguage, targetLanguage = targetLanguage
            )
        }

        currentState.sourceText.takeIf { it.isNotBlank() }?.let { translate(it) }
    }

    fun toggleBookmark() {
        _uiState.update { it.copy(isBookmarked = !it.isBookmarked) }
        saveTranslation()
    }

    private fun translate(input: String) {
        Log.d("TranslateViewModel", "Input: $input")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            repository.getTranslation(
                _uiState.value.sourceLanguage.code, _uiState.value.targetLanguage.code, input
            ).onSuccess { response ->
                Log.d("TranslateViewModel", "Response: ${response.sentences?.getOrNull(0)?.trans}")
                _uiState.update {
                    it.copy(
                        translationResult = response,
                        synonyms = extractSynonyms(response),
                        translatedText = response.sentences?.getOrNull(0)?.trans?.toString() ?: "",
                        isLoading = false,
                        error = null
                    )
                }
                // Create and save translation entry to history
                saveTranslation()
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false, error = exception.message ?: "Unknown error occurred"
                    )
                }
            }
        }
    }

    fun saveTranslation() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val translationEntry = TranslationEntry(
                sourceText = currentState.sourceText,
                translatedText = currentState.translatedText,
                sourceLangName = currentState.sourceLanguage.name,
                targetLangName = currentState.targetLanguage.name,
                sourceLangCode = currentState.sourceLanguage.code,
                targetLangCode = currentState.targetLanguage.code,
                isBookmarked = currentState.isBookmarked,
                synonyms = currentState.synonyms
            )
            repository.saveTranslationEntry(translationEntry)
        }
    }

    fun clearText() {
        _uiState.update {
            it.copy(
                sourceText = "",
                translatedText = "",
                translationResult = TranslationResponse(),
                synonyms = emptyList()
            )
        }
    }
}