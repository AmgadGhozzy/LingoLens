package com.venom.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.data.model.OcrEntry
import com.venom.data.repo.OcrRepository
import com.venom.ui.screen.ViewType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OcrBookmarkState(
    val items: List<OcrEntry> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val viewType: ViewType = ViewType.BOOKMARKS
)

@HiltViewModel
class BookmarkOcrViewModel @Inject constructor(
    private val ocrRepository: OcrRepository
) : ViewModel() {

    private val _ocrBookmarkState = MutableStateFlow(OcrBookmarkState())
    val ocrBookmarkState: StateFlow<OcrBookmarkState> = _ocrBookmarkState.asStateFlow()

    init {
        fetchItems(ViewType.BOOKMARKS)
    }

    // Fetch items based on view type
    fun fetchItems(viewType: ViewType) {
        viewModelScope.launch {
            try {
                _ocrBookmarkState.update { it.copy(isLoading = true, viewType = viewType) }

                val itemsFlow = when (viewType) {
                    ViewType.BOOKMARKS -> ocrRepository.getBookmarkedOcrEntries()
                    ViewType.HISTORY -> ocrRepository.getOcrHistory()
                }

                itemsFlow.collect { items ->
                    _ocrBookmarkState.update {
                        it.copy(
                            items = items, isLoading = false, error = null
                        )
                    }
                    Log.d("BookmarkOcrViewModel", "${viewType.name} items: $items")
                }
            } catch (e: Exception) {
                _ocrBookmarkState.update {
                    it.copy(
                        error = "Failed to load ${viewType.name.lowercase()}: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun setViewType(viewType: ViewType) {
        _ocrBookmarkState.update { it.copy(viewType = viewType) }
    }

    // Remove a single item
    fun removeItem(entry: OcrEntry) {
        viewModelScope.launch {
            try {
                ocrRepository.deleteOcrEntry(entry)
                // Refresh current view
                fetchItems(_ocrBookmarkState.value.viewType)
            } catch (e: Exception) {
                _ocrBookmarkState.update {
                    it.copy(error = "Failed to remove item: ${e.message}")
                }
            }
        }
    }

    // Clear all items in current view
    fun clearAllItems() {
        viewModelScope.launch {
            try {
                when (_ocrBookmarkState.value.viewType) {
                    ViewType.BOOKMARKS -> ocrRepository.clearBookmarks()
                    ViewType.HISTORY -> ocrRepository.deleteNonBookmarkedEntries()
                }
                fetchItems(_ocrBookmarkState.value.viewType)
            } catch (e: Exception) {
                _ocrBookmarkState.update {
                    it.copy(error = "Failed to clear items: ${e.message}")
                }
            }
        }
    }

    // Toggle bookmark status
    fun toggleBookmark(entry: OcrEntry) {
        viewModelScope.launch {
            try {
                val updatedEntry = entry.copy(isBookmarked = !entry.isBookmarked)
                ocrRepository.updateOcrEntry(updatedEntry)
                fetchItems(_ocrBookmarkState.value.viewType)
            } catch (e: Exception) {
                _ocrBookmarkState.update {
                    it.copy(error = "Failed to toggle bookmark: ${e.message}")
                }
            }
        }
    }
}