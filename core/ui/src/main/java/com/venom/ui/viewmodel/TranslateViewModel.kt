package com.venom.ui.viewmodel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.data.mapper.extractSynonyms
import com.venom.data.model.LANGUAGES_LIST
import com.venom.data.model.LanguageItem
import com.venom.data.model.TranslationEntry
import com.venom.data.model.TranslationProvider
import com.venom.data.model.TranslationResponse
import com.venom.data.repo.SettingsRepository
import com.venom.data.repo.TranslationRepository
import com.venom.utils.Extensions.postprocessText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class TranslationUiState(
    val sourceLanguage: LanguageItem = LANGUAGES_LIST[0],
    val targetLanguage: LanguageItem = LANGUAGES_LIST[1],
    val sourceText: String = "",
    val translatedText: String = "",
    val translationResult: TranslationResponse = TranslationResponse(),
    val synonyms: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val isBookmarked: Boolean = false,
    val selectedProvider: TranslationProvider = TranslationProvider.GOOGLE,
    val isInitialized: Boolean = false
)

@OptIn(FlowPreview::class)
@HiltViewModel
class TranslateViewModel @Inject constructor(
    private val repository: TranslationRepository,
    private val settingsRepository: SettingsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Keys for SavedStateHandle
    companion object {
        private const val KEY_SOURCE_TEXT = "source_text"
        private const val KEY_TRANSLATED_TEXT = "translated_text"
        private const val KEY_SOURCE_LANG_CODE = "source_lang_code"
        private const val KEY_TARGET_LANG_CODE = "target_lang_code"
        private const val KEY_IS_BOOKMARKED = "is_bookmarked"
        private const val KEY_IS_INITIALIZED = "is_initialized"
    }

    private val _uiState = MutableStateFlow(
        TranslationUiState(
            sourceText = savedStateHandle.get<String>(KEY_SOURCE_TEXT) ?: "",
            translatedText = savedStateHandle.get<String>(KEY_TRANSLATED_TEXT) ?: "",
            isBookmarked = savedStateHandle.get<Boolean>(KEY_IS_BOOKMARKED) ?: false,
            isInitialized = savedStateHandle.get<Boolean>(KEY_IS_INITIALIZED) ?: false,
            sourceLanguage = findLanguageByCode(savedStateHandle.get<String>(KEY_SOURCE_LANG_CODE)) ?: LANGUAGES_LIST[0],
            targetLanguage = findLanguageByCode(savedStateHandle.get<String>(KEY_TARGET_LANG_CODE)) ?: LANGUAGES_LIST[1]
        )
    )
    val uiState = _uiState.asStateFlow()

    private val textChangeFlow = MutableSharedFlow<String>()
    private var translationJob: Job? = null

    init {
        // Save state whenever it changes
        viewModelScope.launch {
            uiState.collect { state ->
                savedStateHandle[KEY_SOURCE_TEXT] = state.sourceText
                savedStateHandle[KEY_TRANSLATED_TEXT] = state.translatedText
                savedStateHandle[KEY_SOURCE_LANG_CODE] = state.sourceLanguage.code
                savedStateHandle[KEY_TARGET_LANG_CODE] = state.targetLanguage.code
                savedStateHandle[KEY_IS_BOOKMARKED] = state.isBookmarked
                savedStateHandle[KEY_IS_INITIALIZED] = state.isInitialized
            }
        }

        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                _uiState.update { it.copy(selectedProvider = settings.selectedProvider) }
            }
        }

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

    private fun findLanguageByCode(code: String?): LanguageItem? {
        return code?.let { langCode ->
            LANGUAGES_LIST.find { it.code == langCode }
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

    fun updateLanguages(source: LanguageItem, target: LanguageItem, forceUpdate: Boolean = false) {
        val currentState = _uiState.value

        // Check if languages actually changed
        val languagesChanged = currentState.sourceLanguage.code != source.code ||
                currentState.targetLanguage.code != target.code

        if (!languagesChanged && !forceUpdate) return

        _uiState.update {
            it.copy(
                sourceLanguage = source,
                targetLanguage = target,
                isInitialized = true
            )
        }

        // Only translate if there's text and languages actually changed
        if (currentState.sourceText.isNotBlank() && (languagesChanged || forceUpdate)) {
            translate(currentState.sourceText)
        }
    }

    fun markAsInitialized() {
        _uiState.update { it.copy(isInitialized = true) }
    }

    fun toggleBookmark() {
        _uiState.update { it.copy(isBookmarked = !it.isBookmarked) }
        saveTranslation()
    }

    fun moveUp() {
        val currentState = _uiState.value
        if (currentState.translatedText.isBlank()) return

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

        viewModelScope.launch {
            textChangeFlow.emit(currentState.translatedText)
        }
    }

    fun updateProvider(provider: TranslationProvider) {
        val currentProvider = _uiState.value.selectedProvider
        if (currentProvider == provider) return

        viewModelScope.launch {
            settingsRepository.setSelectedProvider(provider)
        }
        if (_uiState.value.sourceText.isNotBlank()) {
            translate(_uiState.value.sourceText)
        }
    }

    private fun translate(input: String) {
        val currentState = _uiState.value
        if (input.isBlank()) return

        // Cancel previous translation if still loading
        translationJob?.cancel()

        // Don't start new translation if one is already in progress with same input
        if (currentState.isLoading) return

        translationJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val existingEntry = repository.getTranslationEntry(
                    input, currentState.sourceLanguage.code, currentState.targetLanguage.code
                )

                repository.getTranslation(
                    currentState.sourceLanguage.code,
                    currentState.targetLanguage.code,
                    input,
                    currentState.selectedProvider
                ).onSuccess { response ->
                    // Ensure the text hasn't changed during translation
                    if (input == _uiState.value.sourceText) {
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
                }.onFailure {
                    if (input == _uiState.value.sourceText) {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
            } catch (e: Exception) {
                if (input == _uiState.value.sourceText) {
                    _uiState.update { it.copy(isLoading = false) }
                }
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
                selectedProvider = it.selectedProvider,
                isInitialized = it.isInitialized
            )
        }
        // Clear saved state
        savedStateHandle[KEY_SOURCE_TEXT] = ""
        savedStateHandle[KEY_TRANSLATED_TEXT] = ""
        savedStateHandle[KEY_IS_BOOKMARKED] = false
    }

    override fun onCleared() {
        super.onCleared()
        translationJob?.cancel()
    }
}