package com.venom.ui.viewmodel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.data.model.LANGUAGES_LIST
import com.venom.data.model.LanguageItem
import com.venom.data.model.TranslationProvider
import com.venom.data.repo.SettingsRepository
import com.venom.data.repo.TranslationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class SentenceCardState(
    val isExpanded: Boolean = false,
    val translatedText: String = "",
    val isLoading: Boolean = false,
    val sourceLanguage: LanguageItem = LANGUAGES_LIST[0],
    val targetLanguage: LanguageItem = LANGUAGES_LIST[1],
    val selectedProvider: TranslationProvider = TranslationProvider.GOOGLE
)

@HiltViewModel
class SentenceCardViewModel @Inject constructor(
    private val repository: TranslationRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                _uiState.update { it.copy(selectedProvider = settings.selectedProvider) }
                _uiState.update { it.copy(targetLanguage = settings.targetLanguage) }
            }
        }
    }

    private val _uiState = MutableStateFlow(SentenceCardState())
    val uiState = _uiState.asStateFlow()

    private var translationJob: Job? = null
    private var lastTranslatedSentence: String? = null

    fun updateExpanded(expanded: Boolean) {
        _uiState.update { it.copy(isExpanded = expanded) }
    }

    fun translate(sentence: String) {
        // Skip if already translated this sentence or currently loading it
        if (lastTranslatedSentence == sentence ||
            (_uiState.value.isLoading && lastTranslatedSentence == sentence)
        ) {
            return
        }

        _uiState.update { it.copy(isLoading = true, translatedText = "") }
        lastTranslatedSentence = sentence

        translationJob?.cancel()
        translationJob = viewModelScope.launch {
            repository.translate(
                sourceText = sentence,
                sourceLang = _uiState.value.sourceLanguage.code,
                targetLang = _uiState.value.targetLanguage.code,
                providerId = _uiState.value.selectedProvider.id,
                forceRefresh = false
            )
                .onSuccess { response ->
                    _uiState.update {
                        it.copy(
                            translatedText = response.translatedText,
                            isLoading = false
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false, translatedText = "") }
                    lastTranslatedSentence = null
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        translationJob?.cancel()
    }
}