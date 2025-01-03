package com.venom.phrase.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.domain.model.LANGUAGES_LIST
import com.venom.domain.model.LanguageItem
import com.venom.phrase.data.mapper.getTranslation
import com.venom.phrase.data.model.Category
import com.venom.phrase.data.model.SectionWithPhrases
import com.venom.phrase.data.repo.PhraseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PhraseUiState(
    val categories: List<Category> = emptyList(),
    val sections: List<SectionWithPhrases> = emptyList(),
    val filteredSections: List<SectionWithPhrases> = emptyList(),
    val selectedCategory: Category? = null,
    val sourceLang: LanguageItem = LANGUAGES_LIST[2],
    val targetLang: LanguageItem = LANGUAGES_LIST[3],
    val searchQuery: String = ""
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

    fun onSearchQueryChange(query: String) {
        _state.update { currentState ->
            currentState.copy(
                searchQuery = query,
                filteredSections = filterSections(query, currentState.sections)
            )
        }
    }

    private fun filterSections(
        query: String,
        sections: List<SectionWithPhrases>
    ): List<SectionWithPhrases> {
        if (query.isBlank()) return sections

        val searchQuery = query.lowercase().trim()
        val sourceLangCode = _state.value.sourceLang.code
        val targetLangCode = _state.value.targetLang.code

        return sections.mapNotNull { sectionWithPhrases ->
            // Check if section name matches in both languages
            val sectionMatchesSource = sectionWithPhrases.section.getTranslation(sourceLangCode)
                .lowercase()
                .contains(searchQuery)

            val sectionMatchesTarget = sectionWithPhrases.section.getTranslation(targetLangCode)
                .lowercase()
                .contains(searchQuery)

            // Filter matching phrases in both languages
            val matchingPhrases = sectionWithPhrases.phrases.filter { phrase ->
                phrase.getTranslation(sourceLangCode).lowercase().contains(searchQuery) ||
                        phrase.getTranslation(targetLangCode).lowercase().contains(searchQuery)
            }

            // Return section with filtered phrases if either condition is met
            when {
                sectionMatchesSource || sectionMatchesTarget -> sectionWithPhrases
                matchingPhrases.isNotEmpty() -> sectionWithPhrases.copy(phrases = matchingPhrases)
                else -> null
            }
        }
    }

    fun updateLanguages(source: LanguageItem, target: LanguageItem) {
        _state.update {
            it.copy(
                sourceLang = source, targetLang = target
            )
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    categories = repository.getAllCategories()
                )
            }
        }
    }

    fun loadSectionsWithPhrases(categoryId: Int) {
        viewModelScope.launch {
            val sections = repository.getSectionsWithPhrases(categoryId)
            _state.update {
                it.copy(sections = sections,
                    filteredSections = sections,
                    selectedCategory = _state.value.categories.find { it.categoryId == categoryId })
            }
        }
    }
}