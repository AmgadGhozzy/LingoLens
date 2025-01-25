package com.venom.stackcard.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.stackcard.data.model.WordEntity
import com.venom.stackcard.data.repo.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val wordRepository: WordRepository
) : ViewModel() {
    val bookmarkedWords: StateFlow<List<WordEntity>> =
        wordRepository.getBookmarkedWords().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    fun removeBookmark(word: WordEntity) {
        viewModelScope.launch {
            wordRepository.toggleBookmark(word)
        }
    }
}