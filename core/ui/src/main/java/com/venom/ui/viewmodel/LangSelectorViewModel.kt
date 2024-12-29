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
    val sourceLang: LanguageItem,
    val targetLang: LanguageItem,
    val searchQuery: String = "",
    val isSelectingSourceLanguage: Boolean = true,
    val filteredLanguages: List<LanguageItem> = LANGUAGES_LIST
)

@HiltViewModel
class LangSelectorViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(
        LanguageSelectorState(
            sourceLang = LANGUAGES_LIST[0], targetLang = LANGUAGES_LIST[1]
        )
    )
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
                currentState.copy(sourceLang = language)
            } else {
                currentState.copy(targetLang = language)
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