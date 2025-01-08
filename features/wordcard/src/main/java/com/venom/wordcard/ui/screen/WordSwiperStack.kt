package com.venom.wordcard.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.resources.R
import com.venom.wordcard.data.model.WordEntity
import com.venom.wordcard.ui.components.UndoButton
import com.venom.wordcard.ui.components.WordCard
import com.venom.wordcard.ui.components.WordCardAnimations
import com.venom.wordcard.ui.components.WordCardAnimations.rememberCardAnimationState
import com.venom.wordcard.ui.components.resetAnimationState
import com.venom.wordcard.ui.components.returnToCenter
import com.venom.wordcard.ui.viewmodel.WordSwiperEvent
import com.venom.wordcard.ui.viewmodel.WordSwiperViewModel
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * A composable that displays a stack of word cards that can be swiped.
 * Implements card flip and swipe animations with proper recomposition optimization.
 * @param viewModel ViewModel for the word card stack
 * @param onForgotWord Callback when a word should be forgotten (swiped left)
 * @param onRememberWord Callback when a word should be remembered (swiped right)
 * @param onBookmarkWord Callback when a word should be bookmarked (swiped left)
 * @param onSpeakWord Callback when a word should be spoken (swiped right)
 * @param onCopyWord Callback when a word should be copied
 * @param onUndoLastAction Callback to undo the last swipe action
 * @param modifier Optional modifier for the component
 */
@Composable
fun WordSwiperStack(
    viewModel: WordSwiperViewModel = hiltViewModel(),
    onRememberWord: (WordEntity) -> Unit,
    onForgotWord: (WordEntity) -> Unit,
    onBookmarkWord: (WordEntity) -> Unit,
    onSpeakWord: (WordEntity) -> Unit,
    onCopyWord: (WordEntity) -> Unit,
    onUndoLastAction: () -> Unit,
    modifier: Modifier = Modifier
) {

    val scope = rememberCoroutineScope()
    val state by viewModel.state.collectAsState()
    val contentDescriptionString = stringResource(id = R.string.word_swiper_stack)

    // Animation state
    val cardAnimState = rememberCardAnimationState()
    Box(modifier = modifier
        .fillMaxSize()
        .semantics(mergeDescendants = true) { contentDescription = contentDescriptionString }) {
        // Render stack of cards
        state.visibleCards.forEachIndexed { index, card ->
            key(card.id) {
                // Add rotation for stack effct
                val currentRotation = when (index) {
                    state.visibleCards.lastIndex -> cardAnimState.rotation.value
                    state.visibleCards.lastIndex - 1 -> -10f
                    state.visibleCards.lastIndex - 2 -> 8f
                    state.visibleCards.lastIndex - 3 -> -6f
                    state.visibleCards.lastIndex - 4 -> 4f
                    else -> 0f
                }

                WordCard(card = card,
                    offsetX = if (index == state.visibleCards.lastIndex) cardAnimState.offsetX.value else 0f,
                    offsetY = if (index == state.visibleCards.lastIndex) cardAnimState.offsetY.value else 0f,
                    rotation = currentRotation,
                    scale = when (index) {
                        // Scale last cards
                        state.visibleCards.lastIndex -> cardAnimState.scale.value
                        state.visibleCards.lastIndex - 1 -> 0.95f
                        state.visibleCards.lastIndex - 2 -> 0.9f
                        state.visibleCards.lastIndex - 3 -> 0.85f
                        state.visibleCards.lastIndex - 4 -> 0.8f
                        else -> 0.9f
                    },
                    isFlipped = if (index == state.visibleCards.lastIndex) state.isFlipped else false,
                    rotationY = if (index == state.visibleCards.lastIndex) cardAnimState.rotationY.value else 0f,
                    onFlip = {
                        scope.launch {
                            cardAnimState.rotationY.animateTo(
                                targetValue = if (!state.isFlipped) 180f else 0f,
                                animationSpec = WordCardAnimations.FlipAnimationSpec
                            )
                            viewModel.onEvent(WordSwiperEvent.FlipCard)
                        }
                    },
                    onDrag = { dragAmount ->
                        scope.launch {
                            val adjustedDragX = if (state.isFlipped) -dragAmount.x else dragAmount.x
                            cardAnimState.offsetX.snapTo(cardAnimState.offsetX.value + adjustedDragX)
                            cardAnimState.offsetY.snapTo(cardAnimState.offsetY.value + dragAmount.y)
                            cardAnimState.rotation.snapTo(WordCardAnimations.calculateRotation(cardAnimState.offsetX.value))
                        }
                    },
                    onDragEnd = {
                        scope.launch {
                            val adjustedOffsetX =
                                if (state.isFlipped) -cardAnimState.offsetX.value else cardAnimState.offsetX.value

                            if (abs(cardAnimState.offsetX.value) > WordCardAnimations.SWIPE_THRESHOLD) {
                                if (adjustedOffsetX > 0) {
                                    viewModel.onEvent(WordSwiperEvent.Remove(card))
                                    onRememberWord(card)
                                } else {
                                    viewModel.onEvent(WordSwiperEvent.Remove(card))
                                    onForgotWord(card)
                                }

                                // Animate the card off screen
                                val targetX =
                                    if (adjustedOffsetX > 0) 1000f * (if (state.isFlipped) -1f else 1f)
                                    else -1000f * (if (state.isFlipped) -1f else 1f)

                                cardAnimState.offsetX.animateTo(
                                    targetValue = targetX,
                                    animationSpec = WordCardAnimations.SwipeAnimationSpec
                                )

                                // Reset animation state immediately after card is off screen
                                resetAnimationState(cardAnimState)
                            } else {
                                returnToCenter(cardAnimState)
                            }
                        }
                        if (state.isFlipped) viewModel.onEvent(WordSwiperEvent.FlipCard)
                    },
                    onBookmark = { onBookmarkWord(card) },
                    onSpeak = { onSpeakWord(card) },
                    onCopy = { onCopyWord(card) })
            }
        }

        if (state.removedCards.isNotEmpty()) {
            UndoButton(
                removedCardsCount = state.removedCards.size, onUndo = {
                    scope.launch { viewModel.onEvent(WordSwiperEvent.UndoLastAction) }
                }, modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}
