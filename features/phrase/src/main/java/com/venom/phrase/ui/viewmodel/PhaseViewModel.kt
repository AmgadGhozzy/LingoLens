package com.venom.phrase.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.domain.model.LANGUAGES_LIST
import com.venom.domain.model.LanguageItem
import com.venom.phrase.data.model.Category
import com.venom.phrase.data.model.Phrase
import com.venom.phrase.data.model.Section
import com.venom.phrase.data.repo.PhraseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PhraseUiState(
    val categories: List<Category> = emptyList(),
    val phrases: List<Phrase> = emptyList(),
    val sections: List<Section> = emptyList(),
    val selectedCategory: Category? = null,
    val selectedSection: Section? = null,
    val sourceLanguage: LanguageItem = LANGUAGES_LIST[0],
    val targetLanguage: LanguageItem = LANGUAGES_LIST[1],
    val searchQuery: String = "",
)

@HiltViewModel
class PhraseViewModel @Inject constructor(
    private val repository: PhraseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PhraseUiState())
    val state = _state.asStateFlow()

    init {
        loadCategories()
    }

    fun updateLanguages(
        sourceLanguage: LanguageItem, targetLanguage: LanguageItem
    ) {
        _state.update { currentState ->
            currentState.copy(
                sourceLanguage = sourceLanguage, targetLanguage = targetLanguage
            )
        }
    }

    fun selectCategory(category: Category) {
        _state.update { currentState ->
            currentState.copy(
                selectedCategory = category,
                selectedSection = null
            )
        }
        category.categoryId?.let { categoryId ->
            loadSectionsForCategory(categoryId)
        }
    }

    fun selectSection(section: Section) {
        _state.update { currentState ->
            currentState.copy(selectedSection = section)
        }
        loadPhrasesForSection(section.sectionId)
    }

    fun loadCategories() {
        viewModelScope.launch {
            repository.getAllCategories().collect { categories ->
                _state.update { currentState ->
                    currentState.copy(categories = categories)
                }
                if (_state.value.selectedCategory == null && categories.isNotEmpty()) {
                    selectCategory(categories.first())
                }
            }
        }
    }

    fun loadSectionsForCategory(categoryId: Int) {
        viewModelScope.launch {
            repository.getSectionsForCategory(categoryId).collect { sections ->
                _state.update { currentState ->
                    currentState.copy(sections = sections)
                }
                if (_state.value.selectedSection == null && sections.isNotEmpty()) {
                    selectSection(sections.first())
                }
            }
        }
    }

    fun loadPhrasesForSection(sectionId: Int) {
        viewModelScope.launch {
            repository.getPhrasesForSection(sectionId).collect { phrases ->
                _state.update { currentState ->
                    currentState.copy(phrases = phrases)
                }
            }
        }
    }
}