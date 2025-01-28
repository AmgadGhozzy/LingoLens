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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
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

@OptIn(FlowPreview::class)
@HiltViewModel
class TranslateViewModel @Inject constructor(
    private val repository: TranslationRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TranslationUiState())
    val uiState = _uiState.asStateFlow()

    private val textChangeFlow = MutableSharedFlow<String>()
    private var translationJob: Job? = null
    private var textCollectionJob: Job? = null

    init {
        startTextCollection()
    }

    private fun startTextCollection() {
        textCollectionJob = viewModelScope.launch {
            textChangeFlow.debounce(500)
                .filterNot { it.isBlank() && it != _uiState.value.sourceText }
                .collect { translate(it) }
        }
    }

    fun onSourceTextChanged(input: String) {
        if (input.isBlank()) {
            clearTranslation()
            return
        }
        _uiState.update { it.copy(sourceText = input) }
        viewModelScope.launch {
            textChangeFlow.emit(input)
        }
    }

    fun updateLanguages(source: LanguageItem, target: LanguageItem) {
        val currentState = _uiState.value
        if (source == currentState.sourceLanguage && target == currentState.targetLanguage) return

        _uiState.update { it.copy(sourceLanguage = source, targetLanguage = target) }
        if (currentState.sourceText.isNotBlank()) {
            translate(currentState.sourceText)
        }
    }

    fun toggleBookmark() {
        _uiState.update { it.copy(isBookmarked = !it.isBookmarked) }
        saveTranslation()
    }

    private fun translate(input: String) {
        Log.d("TranslateViewModel", "Input: $input")
        // Cancel any ongoing translation
        translationJob?.cancel()

        translationJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val existingEntry = repository.getTranslationEntry(
                input, _uiState.value.sourceLanguage.code, _uiState.value.targetLanguage.code
            )

            repository.getTranslation(
                _uiState.value.sourceLanguage.code, _uiState.value.targetLanguage.code, input
            ).fold(onSuccess = { response ->
                if (currentCoroutineContext().isActive) {
                    _uiState.update {
                        it.copy(
                            translationResult = response,
                            translatedText = response.sentences?.firstOrNull()?.trans?.toString()
                                .orEmpty(),
                            synonyms = extractSynonyms(response),
                            isLoading = false,
                            error = null,
                            isBookmarked = existingEntry?.isBookmarked == true
                        )
                    }
                    saveTranslation()
                }
            }, onFailure = { error ->
                if (currentCoroutineContext().isActive) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Unknown error occurred"
                        )
                    }
                }
            })
        }
    }

    private fun saveTranslation() {
        viewModelScope.launch {
            with(_uiState.value) {
                repository.saveTranslationEntry(
                    TranslationEntry(
                        sourceText = sourceText,
                        translatedText = translatedText,
                        sourceLangName = sourceLanguage.name,
                        targetLangName = targetLanguage.name,
                        sourceLangCode = sourceLanguage.code,
                        targetLangCode = targetLanguage.code,
                        isBookmarked = isBookmarked,
                        synonyms = synonyms
                    )
                )
            }
        }
    }

    fun clearTranslation() {
        // Cancel ongoing translation and text collection
        translationJob?.cancel()
        textCollectionJob?.cancel()

        _uiState.update {
            it.copy(
                sourceText = "",
                translatedText = "",
                isBookmarked = false,
                synonyms = emptyList(),
                isLoading = false,
                error = null
            )
        }

        startTextCollection()
    }
}