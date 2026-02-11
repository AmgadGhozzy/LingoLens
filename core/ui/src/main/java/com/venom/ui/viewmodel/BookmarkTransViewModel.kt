package com.venom.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.domain.model.TranslationResult
import com.venom.domain.repo.ITranslationHistory
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
class BookmarkViewModel @Inject constructor(
    private val translationHistory: ITranslationHistory
) : ViewModel() {

    private val _viewType = MutableStateFlow(ViewType.BOOKMARKS)

    val items = _viewType.flatMapLatest {
        when (it) {
            ViewType.BOOKMARKS -> translationHistory.getBookmarkedTranslations()
            ViewType.HISTORY -> translationHistory.getTranslationHistory()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setViewType(viewType: ViewType) { _viewType.value = viewType }

    fun removeItem(entry: TranslationResult) = viewModelScope.launch {
        translationHistory.deleteTranslation(entry)
    }

    fun clearAllItems() = viewModelScope.launch {
        when (_viewType.value) {
            ViewType.BOOKMARKS -> translationHistory.clearBookmarks()
            ViewType.HISTORY -> translationHistory.deleteNonBookmarkedEntries()
        }
    }

    fun toggleBookmark(entry: TranslationResult) = viewModelScope.launch {
        translationHistory.updateTranslation(entry.copy(isBookmarked = !entry.isBookmarked))
    }
}