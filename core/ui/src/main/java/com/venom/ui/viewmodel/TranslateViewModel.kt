package com.venom.ui.viewmodel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.data.model.LANGUAGES_LIST
import com.venom.data.model.LanguageItem
import com.venom.data.model.TranslationProvider
import com.venom.data.repo.SettingsRepository
import com.venom.data.repo.TranslationRepository
import com.venom.domain.model.TranslationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class TranslationUiState(
    val sourceLanguage: LanguageItem = LANGUAGES_LIST[0],
    val targetLanguage: LanguageItem = LANGUAGES_LIST[1],
    val sourceText: String = "",
    val isLoading: Boolean = false,
    val selectedProvider: TranslationProvider = TranslationProvider.GOOGLE,
    val error: String? = null,
    val translationResult: TranslationResult = TranslationResult(),
) {
    val translatedText: String get() = translationResult.translatedText
    val isBookmarked: Boolean get() = translationResult.isBookmarked
}

@OptIn(FlowPreview::class)
@HiltViewModel
class TranslateViewModel @Inject constructor(
    private val repository: TranslationRepository,
    private val settingsRepository: SettingsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_SOURCE_TEXT = "source_text"
        private const val KEY_SOURCE_LANG = "source_lang"
        private const val KEY_TARGET_LANG = "target_lang"
        private const val DEBOUNCE_DELAY = 500L
    }

    private val _uiState = MutableStateFlow(
        TranslationUiState(
            sourceText = savedStateHandle.get<String>(KEY_SOURCE_TEXT) ?: "",
            sourceLanguage = findLanguageByCode(savedStateHandle.get<String>(KEY_SOURCE_LANG))
                ?: LANGUAGES_LIST[0],
            targetLanguage = findLanguageByCode(savedStateHandle.get<String>(KEY_TARGET_LANG))
                ?: LANGUAGES_LIST[1]
        )
    )
    val uiState = _uiState.asStateFlow()

    private val translationTrigger = MutableSharedFlow<TranslationRequest>()
    private var translationJob: Job? = null

    data class TranslationRequest(
        val text: String,
        val forceRefresh: Boolean = false,
        val immediate: Boolean = false
    )

    init {
        observeSettings()
        observeTranslationTriggers()
        observeStateForSaving()

        // Trigger initial translation if we have saved text
        val initialText = _uiState.value.sourceText
        if (initialText.isNotBlank()) {
            triggerTranslation(initialText, immediate = true)
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                val currentProvider = _uiState.value.selectedProvider
                if (settings.selectedProvider != currentProvider) {
                    _uiState.update { it.copy(selectedProvider = settings.selectedProvider) }

                    // Re-translate with new provider if we have text
                    val currentText = _uiState.value.sourceText
                    if (currentText.isNotBlank()) {
                        triggerTranslation(currentText, forceRefresh = true, immediate = true)
                    }
                }
            }
        }
    }

    private fun observeTranslationTriggers() {
        viewModelScope.launch {
            translationTrigger
                .debounce { request ->
                    if (request.immediate) 0L else DEBOUNCE_DELAY
                }
                .distinctUntilChanged { old, new ->
                    old.text == new.text && !new.forceRefresh
                }
                .filter { it.text.isNotBlank() }
                .collect { request ->
                    performTranslation(request.text, request.forceRefresh)
                }
        }
    }

    private fun observeStateForSaving() {
        viewModelScope.launch {
            uiState
                .distinctUntilChanged { old, new ->
                    old.sourceText == new.sourceText &&
                            old.sourceLanguage.code == new.sourceLanguage.code &&
                            old.targetLanguage.code == new.targetLanguage.code
                }
                .collect { state ->
                    savedStateHandle[KEY_SOURCE_TEXT] = state.sourceText
                    savedStateHandle[KEY_SOURCE_LANG] = state.sourceLanguage.code
                    savedStateHandle[KEY_TARGET_LANG] = state.targetLanguage.code
                }
        }
    }

    fun onSourceTextChanged(text: String) {
        val trimmedText = text.trim()

        _uiState.update { state ->
            state.copy(
                sourceText = trimmedText,
                translationResult = if (trimmedText.isBlank()) TranslationResult() else state
                    .translationResult,
                error = null
            )
        }

        cancelCurrentTranslation()

        if (trimmedText.isNotBlank()) {
            triggerTranslation(trimmedText)
        }
    }

    fun updateLanguages(source: LanguageItem, target: LanguageItem) {
        val currentState = _uiState.value

        if (source.code == target.code) return

        val languageChanged = currentState.sourceLanguage.code != source.code ||
                currentState.targetLanguage.code != target.code

        if (!languageChanged) return

        _uiState.update { state ->
            state.copy(
                sourceLanguage = source,
                targetLanguage = target,
                translationResult = TranslationResult(),
                error = null
            )
        }

        if (currentState.sourceText.isNotBlank()) {
            triggerTranslation(currentState.sourceText, forceRefresh = true, immediate = true)
        }
    }

    fun swapLanguages() {
        val currentState = _uiState.value

        _uiState.update { state ->
            state.copy(
                sourceLanguage = currentState.targetLanguage,
                targetLanguage = currentState.sourceLanguage,
                sourceText = currentState.translatedText,
                translationResult = TranslationResult(),
                error = null
            )
        }

        if (currentState.translatedText.isNotBlank()) {
            triggerTranslation(currentState.translatedText, immediate = true)
        }
    }

    fun moveUp() {
        val currentState = _uiState.value
        if (currentState.translatedText.isNotBlank()) {
            // Update source text first
            _uiState.update { state ->
                state.copy(
                    sourceText = currentState.translatedText,
                    translationResult = TranslationResult(),
                    error = null
                )
            }
            triggerTranslation(currentState.translatedText, immediate = true)
        }
    }

    fun toggleBookmark() {
        val result = _uiState.value.translationResult
        if (result.id == 0L) return

        val newBookmarkState = !result.isBookmarked

        _uiState.update { it.copy(translationResult = result.copy(isBookmarked = newBookmarkState)) }

        viewModelScope.launch {
            try {
                repository.bookmarkTranslation(result.id, newBookmarkState)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        translationResult = result.copy(isBookmarked = !newBookmarkState),
                        error = "Failed to update bookmark: ${e.message}"
                    )
                }
            }
        }
    }

    fun updateProvider(provider: TranslationProvider) {
        if (_uiState.value.selectedProvider == provider) return

        viewModelScope.launch { settingsRepository.setSelectedProvider(provider) }
    }

    fun retryTranslation() {
        val currentText = _uiState.value.sourceText
        if (currentText.isNotBlank()) {
            triggerTranslation(currentText, forceRefresh = true, immediate = true)
        }
    }

    fun clearAll() {
        cancelCurrentTranslation()

        _uiState.update { state ->
            state.copy(
                sourceText = "",
                translationResult = TranslationResult(),
                error = null
            )
        }

        savedStateHandle[KEY_SOURCE_TEXT] = ""
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun triggerTranslation(
        text: String,
        forceRefresh: Boolean = false,
        immediate: Boolean = false
    ) {
        viewModelScope.launch {
            translationTrigger.emit(
                TranslationRequest(
                    text = text,
                    forceRefresh = forceRefresh,
                    immediate = immediate
                )
            )
        }
    }

    private fun performTranslation(text: String, forceRefresh: Boolean = false) {
        if (text.isBlank()) return

        val currentState = _uiState.value


        cancelCurrentTranslation()

        translationJob = viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                repository.translate(
                    sourceText = text,
                    sourceLang = currentState.sourceLanguage.code,
                    targetLang = currentState.targetLanguage.code,
                    providerId = currentState.selectedProvider.id,
                    forceRefresh = forceRefresh
                ).fold(
                    onSuccess = { result ->
                        if (text == _uiState.value.sourceText) {
                            _uiState.update { state ->
                                state.copy(
                                    translationResult = result,
                                    isLoading = false,
                                    error = null
                                )
                            }
                        }
                    },
                    onFailure = { exception ->
                        if (text == _uiState.value.sourceText) {
                            _uiState.update { state ->
                                state.copy(
                                    isLoading = false,
                                    error = exception.message ?: "Translation failed"
                                )
                            }
                        }
                    }
                )
            } catch (e: Exception) {
                if (text == _uiState.value.sourceText) {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            error = e.message ?: "Translation failed"
                        )
                    }
                }
            }
        }
    }

    private fun cancelCurrentTranslation() {
        translationJob?.cancel()
        translationJob = null
        _uiState.update { it.copy(isLoading = false) }
    }

    private fun findLanguageByCode(code: String?): LanguageItem? {
        return code?.let { LANGUAGES_LIST.find { it.code == code } }
    }

    override fun onCleared() {
        super.onCleared()
        cancelCurrentTranslation()
    }
}