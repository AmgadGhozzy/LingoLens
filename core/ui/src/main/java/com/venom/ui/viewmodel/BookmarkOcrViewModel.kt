package com.venom.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.data.local.entity.OcrEntity
import com.venom.data.repo.OcrRepository
import com.venom.ui.screen.ViewType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BookmarkOcrViewModel @Inject constructor(
    private val ocrRepository: OcrRepository
) : ViewModel() {

    private val _viewType = MutableStateFlow(ViewType.BOOKMARKS)

    val items = _viewType.flatMapLatest {
        when (it) {
            ViewType.BOOKMARKS -> ocrRepository.getBookmarkedOcrEntries()
            ViewType.HISTORY -> ocrRepository.getOcrHistory()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setViewType(viewType: ViewType) { _viewType.value = viewType }

    fun removeItem(entry: OcrEntity) = viewModelScope.launch {
        ocrRepository.deleteOcrEntry(entry)
    }

    fun clearAllItems() = viewModelScope.launch {
        when (_viewType.value) {
            ViewType.BOOKMARKS -> ocrRepository.clearBookmarks()
            ViewType.HISTORY -> ocrRepository.deleteNonBookmarkedEntries()
        }
    }

    fun toggleBookmark(entry: OcrEntity) = viewModelScope.launch {
        ocrRepository.updateOcrEntry(entry.copy(isBookmarked = !entry.isBookmarked))
    }
}