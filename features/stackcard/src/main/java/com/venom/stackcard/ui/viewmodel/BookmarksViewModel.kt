package com.venom.stackcard.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.data.repo.WordRepositoryImpl
import com.venom.domain.model.Word
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val wordRepository: WordRepositoryImpl
) : ViewModel() {
    val bookmarkedWords: StateFlow<List<Word>> =
        wordRepository.getBookmarkedWords().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    fun removeBookmark(word: Word) {
        viewModelScope.launch {
            wordRepository.toggleBookmark(word)
        }
    }
}