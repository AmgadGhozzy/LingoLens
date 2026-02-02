package com.venom.phrase.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.data.model.LANGUAGES_LIST
import com.venom.data.model.LanguageItem
import com.venom.phrase.data.mapper.getTranslation
import com.venom.phrase.data.model.Category
import com.venom.phrase.data.model.PhraseEntity
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
    val sourceLang: LanguageItem = LANGUAGES_LIST[1],
    val targetLang: LanguageItem = LANGUAGES_LIST[2],
    val searchQuery: String = "",
    val isBookmarkedView: Boolean = false
)

@HiltViewModel
class PhraseViewModel @Inject constructor(
    private val repository: PhraseRepository
) : ViewModel() {
    private val _state = MutableStateFlow(PhraseUiState())
    val state = _state.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _state.update { currentState ->
            currentState.copy(
                searchQuery = query, filteredSections = filterSections(query, currentState.sections)
            )
        }
    }

    private fun filterSections(
        query: String, sections: List<SectionWithPhrases>
    ): List<SectionWithPhrases> {
        if (query.isBlank()) return sections

        val searchQuery = query.lowercase().trim()
        val sourceLangCode = _state.value.sourceLang.code
        val targetLangCode = _state.value.targetLang.code

        return sections.mapNotNull { sectionWithPhrases ->
            val sectionMatchesSource =
                sectionWithPhrases.section.getTranslation(sourceLangCode).lowercase()
                    .contains(searchQuery)

            val sectionMatchesTarget =
                sectionWithPhrases.section.getTranslation(targetLangCode).lowercase()
                    .contains(searchQuery)

            val matchingPhrases = sectionWithPhrases.phrases.filter { phrase ->
                phrase.getTranslation(sourceLangCode).lowercase()
                    .contains(searchQuery) || phrase.getTranslation(targetLangCode).lowercase()
                    .contains(searchQuery)
            }

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
                    categories = repository.getAllCategories(), isBookmarkedView = false
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

    fun loadBookmarkedPhrases() {
        viewModelScope.launch {
            val bookmarkedSections = repository.getSectionsWithBookmarkedPhrases()

            _state.update {
                it.copy(
                    sections = bookmarkedSections,
                    filteredSections = bookmarkedSections,
                    selectedCategory = null,
                    isBookmarkedView = true
                )
            }
        }
    }

    fun toggleBookmark(phrase: PhraseEntity) {
        viewModelScope.launch {
            repository.toggleBookmark(phrase)

            if (_state.value.isBookmarkedView) {
                loadBookmarkedPhrases()
            } else {
                _state.update { currentState ->
                    val updatedSections = currentState.sections.map { section ->
                        section.copy(
                            phrases = section.phrases.map {
                                if (it.phraseId == phrase.phraseId) {
                                    it.copy(isBookmarked = !it.isBookmarked)
                                } else it
                            }
                        )
                    }

                    currentState.copy(
                        sections = updatedSections,
                        filteredSections = filterSections(currentState.searchQuery, updatedSections)
                    )
                }
            }
        }
    }
}
