package com.venom.ui.viewmodel

import android.util.Log
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
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
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
    val error: String? = null,
    val isNetworkAvailable: Boolean = true,
    val isBookmarked: Boolean = false,
    val clearTextFlag: Boolean = false,
    val selectedProvider: TranslationProvider = TranslationProvider.GOOGLE,
    val availableProviders: List<TranslationProvider> = TranslationProvider.ALL
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
        if (input.isBlank() || input == _uiState.value.sourceText) {
            return
        }
        _uiState.update { it.copy(sourceText = input, clearTextFlag = false) }
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

    fun updateProvider(provider: TranslationProvider) {
        _uiState.update { it.copy(selectedProvider = provider) }
        if (_uiState.value.sourceText.isNotEmpty()) {
            translate(
                input = _uiState.value.sourceText
            )
        }
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
                _uiState.value.sourceLanguage.code,
                _uiState.value.targetLanguage.code,
                input,
                _uiState.value.selectedProvider
            ).fold(onSuccess = { response ->
                if (currentCoroutineContext().isActive) {
                    _uiState.update {
                        it.copy(
                            translationResult = response,
                            translatedText = response.sentences?.firstOrNull()?.trans?.postprocessText()
                                .orEmpty(),
                            synonyms = extractSynonyms(response),
                            isLoading = false,
                            error = null,
                            isBookmarked = existingEntry?.isBookmarked == true,
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

    fun clearTranslation() {
        // Cancel ongoing translation and text collection
        translationJob?.cancel()
        textCollectionJob?.cancel()

        _uiState.update {
            it.copy(
                sourceText = "",
                translatedText = "",
                clearTextFlag = true,
                isBookmarked = false,
                synonyms = emptyList(),
                isLoading = false,
                error = null
            )
        }

        startTextCollection()
    }
}