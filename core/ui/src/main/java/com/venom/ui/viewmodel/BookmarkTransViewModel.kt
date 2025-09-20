package com.venom.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.domain.model.TranslationResult
import com.venom.domain.repo.ITranslationHistory
import com.venom.ui.screen.ViewType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class BookmarkState(
    val items: List<TranslationResult> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val viewType: ViewType = ViewType.BOOKMARKS
)

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val translationHistory: ITranslationHistory
) : ViewModel() {

    private val _bookmarkState = MutableStateFlow(BookmarkState())
    val bookmarkState: StateFlow<BookmarkState> = _bookmarkState.asStateFlow()

    init {
        fetchItems(ViewType.BOOKMARKS)
    }

    // Fetch items based on view type
    fun fetchItems(viewType: ViewType) {
        viewModelScope.launch {
            try {
                _bookmarkState.update { it.copy(isLoading = true, viewType = viewType) }

                val itemsFlow = when (viewType) {
                    ViewType.BOOKMARKS -> translationHistory.getBookmarkedTranslations()
                    ViewType.HISTORY -> translationHistory.getTranslationHistory()
                }

                itemsFlow.collect { items ->
                    _bookmarkState.update {
                        it.copy(
                            items = items, isLoading = false, error = null
                        )
                    }
                    Log.d("BookmarkViewModel", "${viewType.name} items: $items")
                }
            } catch (e: Exception) {
                _bookmarkState.update {
                    it.copy(
                        error = "Failed to load ${viewType.name.lowercase()}: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun setViewType(viewType: ViewType) {
        _bookmarkState.update { it.copy(viewType = viewType) }
    }

    // Remove a single item
    fun removeItem(entry: TranslationResult) {
        viewModelScope.launch {
            try {
                translationHistory.deleteTranslation(entry)
                // Refresh current view
                fetchItems(_bookmarkState.value.viewType)
            } catch (e: Exception) {
                _bookmarkState.update {
                    it.copy(error = "Failed to remove item: ${e.message}")
                }
            }
        }
    }

    // Clear all items in current view
    fun clearAllItems() {
        viewModelScope.launch {
            try {
                when (_bookmarkState.value.viewType) {
                    ViewType.BOOKMARKS -> translationHistory.clearBookmarks()
                    ViewType.HISTORY -> translationHistory.deleteNonBookmarkedEntries()
                }
                fetchItems(_bookmarkState.value.viewType)
            } catch (e: Exception) {
                _bookmarkState.update {
                    it.copy(error = "Failed to clear items: ${e.message}")
                }
            }
        }
    }

    // Toggle bookmark status
    fun toggleBookmark(entry: TranslationResult) {
        viewModelScope.launch {
            try {
                val updatedEntry = entry.copy(isBookmarked = !entry.isBookmarked)
                translationHistory.updateTranslation(updatedEntry)
                fetchItems(_bookmarkState.value.viewType)
            } catch (e: Exception) {
                _bookmarkState.update {
                    it.copy(error = "Failed to toggle bookmark: ${e.message}")
                }
            }
        }
    }
}