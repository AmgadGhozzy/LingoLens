package com.venom.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.data.model.LANGUAGES_LIST
import com.venom.data.model.LanguageItem
import com.venom.data.repo.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LanguageSelectorState(
    val sourceLang: LanguageItem = LANGUAGES_LIST[0],
    val targetLang: LanguageItem = LANGUAGES_LIST[1],
    val searchQuery: String = "",
    val isSelectingSourceLanguage: Boolean = true,
    val filteredLanguages: List<LanguageItem> = LANGUAGES_LIST,
    val didSwap: Boolean = false
)

@HiltViewModel
class LangSelectorViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LanguageSelectorState())
    val state = _state.asStateFlow()

    private var isInternalUpdate = false

    init {
        loadSavedLanguages()
    }

    private fun loadSavedLanguages() {
        viewModelScope.launch {
            settingsRepository.settings.collectLatest { preferences ->
                if (!isInternalUpdate) {
                    val nativeLang =
                        LANGUAGES_LIST.find { it.code == preferences.nativeLanguage.code }
                            ?: LANGUAGES_LIST[0]
                    val targetLang =
                        LANGUAGES_LIST.find { it.code == preferences.targetLanguage.code }
                            ?: LANGUAGES_LIST[1]

                    _state.update { currentState ->
                        currentState.copy(
                            sourceLang = nativeLang, targetLang = targetLang
                        )
                    }
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(
                    searchQuery = query, filteredLanguages = filterLanguages(query)
                )
            }
        }
    }

    private fun filterLanguages(query: String): List<LanguageItem> {
        return if (query.isEmpty()) {
            LANGUAGES_LIST
        } else {
            val lowerCaseQuery = query.lowercase().trim()
            LANGUAGES_LIST.filter { lang ->
                lang.code.contains(lowerCaseQuery, ignoreCase = true) || lang.englishName.contains(
                    lowerCaseQuery,
                    ignoreCase = true
                )
            }
        }
    }

    fun setSelectingSourceLanguage(isSelecting: Boolean) {
        _state.update { currentState ->
            currentState.copy(isSelectingSourceLanguage = isSelecting)
        }
    }

    fun onLanguageSelected(language: LanguageItem) {
        viewModelScope.launch {
            isInternalUpdate = true
            try {
                _state.update { currentState ->
                    if (currentState.isSelectingSourceLanguage) {
                        if (language == currentState.targetLang) {
                            settingsRepository.setNativeLanguage(currentState.targetLang)
                            settingsRepository.setTargetLanguage(currentState.sourceLang)
                            currentState.copy(
                                sourceLang = currentState.targetLang,
                                targetLang = currentState.sourceLang,
                                isSelectingSourceLanguage = false
                            )
                        } else {
                            settingsRepository.setNativeLanguage(language)
                            currentState.copy(
                                sourceLang = language, isSelectingSourceLanguage = false
                            )
                        }
                    } else {
                        if (language == currentState.sourceLang) {
                            settingsRepository.setNativeLanguage(currentState.targetLang)
                            settingsRepository.setTargetLanguage(currentState.sourceLang)
                            currentState.copy(
                                sourceLang = currentState.targetLang,
                                targetLang = currentState.sourceLang,
                                isSelectingSourceLanguage = true
                            )
                        } else {
                            settingsRepository.setTargetLanguage(language)
                            currentState.copy(
                                targetLang = language, isSelectingSourceLanguage = true
                            )
                        }
                    }
                }
            } finally {
                isInternalUpdate = false
            }
        }
    }

    fun swapLanguages() {
        viewModelScope.launch {
            isInternalUpdate = true
            try {
                _state.update { currentState ->
                    settingsRepository.setNativeLanguage(currentState.targetLang)
                    settingsRepository.setTargetLanguage(currentState.sourceLang)
                    currentState.copy(
                        sourceLang = currentState.targetLang,
                        targetLang = currentState.sourceLang
                    )
                }
            } finally {
                isInternalUpdate = false
            }
        }
    }
}