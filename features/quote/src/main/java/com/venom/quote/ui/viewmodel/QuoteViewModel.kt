package com.venom.quote.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.domain.model.Author
import com.venom.domain.model.Quote
import com.venom.domain.model.Tag
import com.venom.domain.repo.IQuoteRepository
import com.venom.quote.ui.components.FilterOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuoteViewModel @Inject constructor(
    private val repository: IQuoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuoteUiState())
    val uiState: StateFlow<QuoteUiState> = _uiState.asStateFlow()

    // Cached data
    private val _allQuotes = MutableStateFlow<List<Quote>>(emptyList())
    private val _allAuthors = MutableStateFlow<List<Author>>(emptyList())
    private val _allTags = MutableStateFlow<List<Tag>>(emptyList())

    private val searchCache = mutableMapOf<String, List<Quote>>()
    private var searchJob: Job? = null

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // Load quotes
                launch {
                    repository.getAllQuotes().collect { quotes ->
                        _allQuotes.value = quotes
                        _uiState.update { state ->
                            val filtered = applyFilters(quotes, state.filterOptions)
                            state.copy(
                                quotes = quotes,
                                filteredQuotes = filtered,
                                dailyQuote = state.dailyQuote ?: quotes.randomOrNull(),
                                totalQuotesCount = quotes.size,
                                isLoading = false
                            )
                        }
                    }
                }

                // Load authors
                launch {
                    repository.getAllAuthors().collect { authors ->
                        _allAuthors.value = authors
                        _uiState.update { it.copy(availableAuthors = authors) }
                        Log.e("QuoteViewModel", "Authors loaded: ${authors.size}")
                    }
                }

                // Load tags
                launch {
                    repository.getAllTags().collect { tags ->
                        _allTags.value = tags
                        _uiState.update { it.copy(availableTags = tags) }
                    }
                }

            } catch (e: Exception) {
                Log.e("QuoteViewModel", "Error loading data: ${e.message}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load data: ${e.message}"
                    )
                }
            }
        }
    }

    // SEARCH with debouncing
    fun updateSearchQuery(query: String) {
        val trimmedQuery = query.trim()
        _uiState.update { it.copy(searchQuery = trimmedQuery) }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            performSearch(trimmedQuery)
        }
    }

    private fun performSearch(query: String) {
        when {
            query.isEmpty() -> {
                clearSearch()
            }

            query.length < 2 -> {
                _uiState.update { it.copy(searchSuggestions = emptyList()) }
                updateFilteredQuotes()
            }

            else -> {
                generateSuggestions(query)
                searchQuotes(query)
            }
        }
    }

    private fun searchQuotes(query: String) {
        // Check cache first
        val cachedResults = searchCache[query]
        if (cachedResults != null) {
            updateWithResults(cachedResults, query)
            return
        }

        // Perform search
        val searchResults = _allQuotes.value.filter { quote ->
            quote.quoteContent.contains(query, ignoreCase = true) ||
                    quote.authorName.contains(query, ignoreCase = true) ||
                    quote.tags.any { it.contains(query, ignoreCase = true) }
        }

        searchCache[query] = searchResults
        updateWithResults(searchResults, query)
    }

    private fun updateWithResults(results: List<Quote>, query: String) {
        val currentState = _uiState.value
        val filtered = applyFilters(results, currentState.filterOptions)

        _uiState.update { state ->
            state.copy(
                filteredQuotes = filtered,
                recentSearches = updateRecentSearches(state.recentSearches, query)
            )
        }
    }

    private fun clearSearch() {
        _uiState.update { state ->
            val filtered = applyFilters(_allQuotes.value, state.filterOptions)
            state.copy(
                filteredQuotes = filtered,
                searchSuggestions = emptyList()
            )
        }
    }

    private fun updateFilteredQuotes() {
        val currentState = _uiState.value
        val baseQuotes = if (currentState.searchQuery.isNotEmpty()) {
            searchCache[currentState.searchQuery] ?: _allQuotes.value
        } else {
            _allQuotes.value
        }

        val filtered = applyFilters(baseQuotes, currentState.filterOptions)
        _uiState.update { it.copy(filteredQuotes = filtered) }
    }

    private fun generateSuggestions(query: String) {
        val suggestions = buildList {
            _allAuthors.value
                .filter { it.authorName.contains(query, ignoreCase = true) }
                .sortedByDescending { it.quotesCount }
                .take(3)
                .forEach { add(it.authorName) }

            _allTags.value
                .filter { it.tagName.contains(query, ignoreCase = true) }
                .sortedByDescending { it.count }
                .take(3)
                .forEach { add(it.tagName) }
        }

        _uiState.update { it.copy(searchSuggestions = suggestions.distinct().take(6)) }
    }

    // FILTERING
    fun applyFilters(filters: FilterOptions) {
        _uiState.update { it.copy(filterOptions = filters) }
        updateFilteredQuotes()
    }

    private fun applyFilters(quotes: List<Quote>, filters: FilterOptions): List<Quote> {
        if (!filters.hasActiveFilters()) return quotes

        return quotes.filter { quote ->
            (!filters.favoritesOnly || quote.isFavorite) &&
                    (filters.authors.isEmpty() || quote.authorName in filters.authors) &&
                    (filters.tags.isEmpty() || quote.tags.toSet().intersect(filters.tags)
                        .isNotEmpty())
        }
    }

    // FILTER TOGGLES
    fun toggleAuthorFilter(authorName: String) {
        val currentFilters = _uiState.value.filterOptions
        applyFilters(currentFilters.toggleAuthor(authorName))
    }

    fun toggleTagFilter(tagName: String) {
        val currentFilters = _uiState.value.filterOptions
        applyFilters(currentFilters.toggleTag(tagName))
    }

    fun removeFilter(filterType: String, value: String) {
        val currentFilters = _uiState.value.filterOptions
        val newFilters = when (filterType) {
            "favorites" -> currentFilters.copy(favoritesOnly = false)
            "author" -> currentFilters.copy(authors = currentFilters.authors - value)
            "tag" -> currentFilters.copy(tags = currentFilters.tags - value)
            else -> currentFilters
        }
        applyFilters(newFilters)
    }

    fun clearAllFilters() = applyFilters(FilterOptions())

    // QUICK ACTIONS
    fun loadQuotesByAuthor(authorName: String) =
        applyFilters(FilterOptions(authors = setOf(authorName)))

    fun loadQuotesByTag(tagName: String) =
        applyFilters(FilterOptions(tags = setOf(tagName)))

    fun setSelectedAuthor(author: Author?) {
        _uiState.update { it.copy(selectedAuthor = author) }
    }

    fun clearRecentSearches() {
        _uiState.update { it.copy(recentSearches = emptyList()) }
    }

    private fun updateRecentSearches(current: List<String>, newQuery: String): List<String> {
        return (listOf(newQuery) + current.filterNot { it == newQuery }).take(5)
    }

    // FAVORITES
    fun toggleFavorite(quoteId: Long) {
        viewModelScope.launch {
            try {
                val quote = _uiState.value.quotes.find { it.quoteId == quoteId } ?: return@launch

                val newFavorite = !quote.isFavorite
                repository.updateFavoriteStatus(quoteId, newFavorite)

                _uiState.update { state ->
                    if (state.dailyQuote?.quoteId == quoteId) {
                        state.copy(dailyQuote = state.dailyQuote.copy(isFavorite = newFavorite))
                    } else state
                }

            } catch (e: Exception) {
                Log.e("QuoteViewModel", "Error toggling favorite: ${e.message}")
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
        searchCache.clear()
    }
}

data class QuoteUiState(
    val quotes: List<Quote> = emptyList(),
    val filteredQuotes: List<Quote> = emptyList(),
    val dailyQuote: Quote? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val recentSearches: List<String> = emptyList(),
    val searchSuggestions: List<String> = emptyList(),
    val filterOptions: FilterOptions = FilterOptions(),
    val availableAuthors: List<Author> = emptyList(),
    val availableTags: List<Tag> = emptyList(),
    val totalQuotesCount: Int = 0,
    val selectedAuthor: Author? = null
)