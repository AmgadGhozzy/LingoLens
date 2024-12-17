package com.venom.lingopro.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.lingopro.data.model.TranslationEntry
import com.venom.lingopro.data.repository.TranslationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ViewType {
    BOOKMARKS, HISTORY
}

data class BookmarkState(
    val items: List<TranslationEntry> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val viewType: ViewType = ViewType.BOOKMARKS
)

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val translationRepository: TranslationRepository
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
                    ViewType.BOOKMARKS -> translationRepository.getBookmarkedTranslations()
                    ViewType.HISTORY -> translationRepository.getTranslationHistory()
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

    fun toggleViewType() {
        val currentViewType = _bookmarkState.value.viewType
        val newViewType = when (currentViewType) {
            ViewType.BOOKMARKS -> ViewType.HISTORY
            ViewType.HISTORY -> ViewType.BOOKMARKS
        }
        fetchItems(newViewType)
    }

    // Remove a single item
    fun removeItem(entry: TranslationEntry) {
        viewModelScope.launch {
            try {
                translationRepository.deleteTranslationEntry(entry)
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
                    ViewType.BOOKMARKS -> translationRepository.clearBookmarks()
                    ViewType.HISTORY -> translationRepository.deleteNonBookmarkedEntries()
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
    fun toggleBookmark(entry: TranslationEntry) {
        viewModelScope.launch {
            try {
                val updatedEntry = entry.copy(isBookmarked = !entry.isBookmarked)
                translationRepository.updateTranslationEntry(updatedEntry)
                fetchItems(_bookmarkState.value.viewType)
            } catch (e: Exception) {
                _bookmarkState.update {
                    it.copy(error = "Failed to toggle bookmark: ${e.message}")
                }
            }
        }
    }
}