package com.venom.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.data.mapper.extractSynonyms
import com.venom.data.model.LANGUAGES_LIST
import com.venom.data.model.LanguageItem
import com.venom.data.model.TranslationEntry
import com.venom.data.model.TranslationProvider
import com.venom.data.model.TranslationResponse
import com.venom.data.repo.TranslationRepository
import com.venom.utils.Extensions.postprocessText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TranslationUiState(
    val sourceLanguage: LanguageItem = LANGUAGES_LIST[0],
    val targetLanguage: LanguageItem = LANGUAGES_LIST[1],
    val sourceText: String = "",
    val translatedText: String = "",
    val translationResult: TranslationResponse = TranslationResponse(),
    val synonyms: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val isBookmarked: Boolean = false,
    val selectedProvider: TranslationProvider = TranslationProvider.GOOGLE
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

    init {
        viewModelScope.launch {
            textChangeFlow
                .debounce(300)
                .distinctUntilChanged()
                .filter { it.isNotBlank() }
                .collect { text ->
                    if (text == _uiState.value.sourceText) {
                        translate(text)
                    }
                }
        }
    }

    fun onSourceTextChanged(input: String) {
        if (input == _uiState.value.sourceText) return

        _uiState.update {
            it.copy(
                sourceText = input,
                translatedText = if (input.isBlank()) "" else it.translatedText
            )
        }

        if (input.isNotBlank()) {
            viewModelScope.launch { textChangeFlow.emit(input) }
        } else {
            translationJob?.cancel()
            _uiState.update {
                it.copy(
                    translatedText = "",
                    synonyms = emptyList(),
                    isLoading = false,
                    isBookmarked = false
                )
            }
        }
    }

    fun updateLanguages(source: LanguageItem, target: LanguageItem) {
        _uiState.update { it.copy(sourceLanguage = source, targetLanguage = target) }
        if (_uiState.value.sourceText.isNotBlank()) {
            translate(_uiState.value.sourceText)
        }
    }

    fun toggleBookmark() {
        _uiState.update { it.copy(isBookmarked = !it.isBookmarked) }
        saveTranslation()
    }

    fun moveUp() {
        val currentState = _uiState.value
        if (currentState.translatedText.isBlank()) return

        // Cancel ongoing operations
        translationJob?.cancel()

        _uiState.update {
            it.copy(
                sourceText = currentState.translatedText,
                translatedText = "",
                synonyms = emptyList(),
                isBookmarked = false,
                isLoading = false
            )
        }

        // Trigger translation for the moved text
        viewModelScope.launch {
            textChangeFlow.emit(currentState.translatedText)
        }
    }

    fun updateProvider(provider: TranslationProvider) {
        _uiState.update { it.copy(selectedProvider = provider) }
        if (_uiState.value.sourceText.isNotBlank()) {
            translate(_uiState.value.sourceText)
        }
    }

    private fun translate(input: String) {
        val currentState = _uiState.value
        if (input.isBlank() || currentState.isLoading) return

        translationJob?.cancel()
        translationJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val existingEntry = repository.getTranslationEntry(
                input, currentState.sourceLanguage.code, currentState.targetLanguage.code
            )

            repository.getTranslation(
                currentState.sourceLanguage.code,
                currentState.targetLanguage.code,
                input,
                currentState.selectedProvider
            ).onSuccess { response ->
                _uiState.update {
                    it.copy(
                        translationResult = response,
                        translatedText = response.sentences?.firstOrNull()?.trans?.postprocessText()
                            .orEmpty(),
                        synonyms = extractSynonyms(response),
                        isLoading = false,
                        isBookmarked = existingEntry?.isBookmarked == true
                    )
                }
                saveTranslation()
            }
        }
    }

    private fun saveTranslation() {
        viewModelScope.launch {
            with(_uiState.value) {
                if (sourceText.isNotBlank() && translatedText.isNotBlank()) {
                    repository.saveTranslationEntry(
                        TranslationEntry(
                            sourceText = sourceText,
                            translatedText = translatedText,
                            sourceLangName = sourceLanguage.englishName,
                            targetLangName = targetLanguage.englishName,
                            sourceLangCode = sourceLanguage.code,
                            targetLangCode = targetLanguage.code,
                            isBookmarked = isBookmarked,
                            synonyms = synonyms
                        )
                    )
                }
            }
        }
    }

    fun clearAll() {
        translationJob?.cancel()
        _uiState.update {
            TranslationUiState(
                sourceLanguage = it.sourceLanguage,
                targetLanguage = it.targetLanguage,
                selectedProvider = it.selectedProvider
            )
        }
    }
}