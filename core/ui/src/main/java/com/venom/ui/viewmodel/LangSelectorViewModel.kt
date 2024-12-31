package com.venom.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.domain.model.LANGUAGES_LIST
import com.venom.domain.model.LanguageItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.text.contains

data class LanguageSelectorState(
    val sourceLang: LanguageItem = LANGUAGES_LIST[0],
    val targetLang: LanguageItem = LANGUAGES_LIST[1],
    val searchQuery: String = "",
    val isSelectingSourceLanguage: Boolean = true,
    val filteredLanguages: List<LanguageItem> = LANGUAGES_LIST
)

@HiltViewModel
class LangSelectorViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(LanguageSelectorState())

    val state = _state.asStateFlow()

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
            LANGUAGES_LIST.filter { lang ->
                lang.code.contains(query, ignoreCase = true) || lang.name.contains(
                    query,
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
        _state.update { currentState ->
            if (currentState.isSelectingSourceLanguage) {
                if (language == currentState.targetLang) {
                    currentState.copy(
                        sourceLang = currentState.targetLang,
                        targetLang = currentState.sourceLang,
                        isSelectingSourceLanguage = false
                    )
                } else {
                    currentState.copy(
                        sourceLang = language,
                        isSelectingSourceLanguage = false
                    )
                }
            } else {
                if (language == currentState.sourceLang) {
                    currentState.copy(
                        sourceLang = currentState.targetLang,
                        targetLang = currentState.sourceLang,
                        isSelectingSourceLanguage = true
                    )
                } else {
                    currentState.copy(
                        targetLang = language,
                        isSelectingSourceLanguage = true
                    )
                }
            }
        }
    }

    fun swapLanguages() {
        _state.update { currentState ->
            currentState.copy(
                sourceLang = currentState.targetLang, targetLang = currentState.sourceLang
            )
        }
    }
}