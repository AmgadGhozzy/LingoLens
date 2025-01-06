package com.venom.wordcard.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WordSwiperState(
    val visibleCards: List<WordCard> = emptyList(),
    val removedCards: List<WordCard> = emptyList(),
    val isFlipped: Boolean = false,
    val currentCardIndex: Int = 0
)

sealed class WordSwiperEvent {
    data class SaveWord(val card: WordCard) : WordSwiperEvent()
    data class SpeakWord(val card: WordCard) : WordSwiperEvent()
    object UndoLastAction : WordSwiperEvent()
    object FlipCard : WordSwiperEvent()
}

class WordSwiperViewModel : ViewModel() {
    private val _state = MutableStateFlow(WordSwiperState())
    val state: StateFlow<WordSwiperState> = _state.asStateFlow()

    fun onEvent(event: WordSwiperEvent) {
        when (event) {
            is WordSwiperEvent.SaveWord -> handleSaveWord(event.card)
            is WordSwiperEvent.SpeakWord -> handleSpeakWord(event.card)
            is WordSwiperEvent.UndoLastAction -> handleUndo()
            WordSwiperEvent.FlipCard -> handleFlip()
        }
    }

    private fun handleSaveWord(card: WordCard) {
        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(removedCards = listOf(card) + currentState.removedCards,
                    visibleCards = currentState.visibleCards.filter { it.id != card.id })
            }
        }
    }

    private fun handleSpeakWord(card: WordCard) {
        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(removedCards = listOf(card) + currentState.removedCards,
                    visibleCards = currentState.visibleCards.filter { it.id != card.id })
            }
        }
    }

    private fun handleUndo() {
        viewModelScope.launch {
            _state.update { currentState ->
                if (currentState.removedCards.isNotEmpty()) {
                    currentState.copy(
                        removedCards = currentState.removedCards.drop(1),
                        visibleCards = (listOf(currentState.removedCards.first()) + currentState.visibleCards).take(
                            3
                        )
                    )
                } else currentState
            }
        }
    }

    private fun handleFlip() {
        _state.update { it.copy(isFlipped = !it.isFlipped) }
    }

    fun initializeCards(words: List<WordCard>) {
        _state.update { it.copy(visibleCards = words.take(5)) }
    }
}
