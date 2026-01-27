package com.venom.stackcard.ui.components.flashcard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.domain.model.WordMaster
import com.venom.resources.R
import com.venom.stackcard.ui.components.flashcard.EnhancedCardAnimations.SWIPE_THRESHOLD_DP
import com.venom.stackcard.ui.components.flashcard.EnhancedCardAnimations.SwipeAnimationSpec
import com.venom.stackcard.ui.components.flashcard.EnhancedCardAnimations.calculateRotation
import com.venom.stackcard.ui.viewmodel.WordMasteryEvent
import com.venom.stackcard.ui.viewmodel.WordMasteryViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.sqrt

private const val MAX_VISIBLE_CARDS = 3

@Composable
fun CardSwiperStack(
    modifier: Modifier = Modifier,
    viewModel: WordMasteryViewModel = hiltViewModel(),
    onSpeak: (text: String) -> Unit,
    onRememberWord: (WordMaster) -> Unit = {},
    onForgotWord: (WordMaster) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val state by viewModel.uiState.collectAsState()
    val contentDescriptionString = stringResource(id = R.string.card_swiper_stack)

    val cardAnimState = rememberCardAnimationState()
    var isRestoringCard by remember { mutableStateOf(false) }
    var targetX by remember { mutableFloatStateOf(0f) }

    var activeSwipeJob by remember { mutableStateOf<Job?>(null) }

    val swipeThresholdPx = remember(density) { with(density) { SWIPE_THRESHOLD_DP.dp.toPx() } }

    val onDragCallback: (Offset) -> Unit = remember(cardAnimState, scope) {
        { dragAmount ->
            activeSwipeJob?.cancel()

            scope.launch {
                val newOffsetX = cardAnimState.offsetX.value + dragAmount.x
                val newOffsetY = cardAnimState.offsetY.value + dragAmount.y
                val dragDistance = sqrt(newOffsetX * newOffsetX + newOffsetY * newOffsetY)
                val scaleValue = 1f - (dragDistance * 0.0005f).coerceIn(0f, 0.2f)

                cardAnimState.offsetX.snapTo(newOffsetX)
                cardAnimState.offsetY.snapTo(newOffsetY)
                cardAnimState.rotation.snapTo(calculateRotation(newOffsetX))
                cardAnimState.scale.snapTo(scaleValue)
            }
        }
    }

    val limitedCards = remember(state.visibleCards) {
        state.visibleCards.take(MAX_VISIBLE_CARDS)
    }

    val stackPositions = remember(limitedCards.size) {
        List(limitedCards.size) { index ->
            val stackPosition = limitedCards.lastIndex - index
            StackPosition(
                offsetY = stackPosition * 4.dp,
                offsetX = if (stackPosition > 0) (sin(stackPosition * 0.5f) * 8f).dp else 0.dp,
                rotation = when (stackPosition) {
                    1 -> -3f
                    2 -> 2f
                    else -> 0f
                },
                scale = when (stackPosition) {
                    1 -> 0.96f
                    2 -> 0.93f
                    else -> 0.90f
                }
            )
        }
    }

    // Cleanup on disposal
    DisposableEffect(Unit) {
        onDispose {
            activeSwipeJob?.cancel()
            cardAnimState.cancelAnimations()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .semantics(mergeDescendants = true) { contentDescription = contentDescriptionString },
        contentAlignment = Alignment.Center
    ) {
        limitedCards.reversed().forEachIndexed { index, word ->
            key(word.wordEn) {
                val isTopCard = index == limitedCards.lastIndex
                val stackPosition = limitedCards.lastIndex - index
                val position = stackPositions.getOrNull(index)

                if (position != null) {
                    val stackRotation = if (stackPosition == 0) {
                        cardAnimState.rotation.value
                    } else {
                        position.rotation + (cardAnimState.rotation.value * 0.1f / stackPosition)
                    }

                    val scale = if (stackPosition == 0) {
                        cardAnimState.scale.value
                    } else {
                        position.scale
                    }

                    MasterySwipeableCard(
                        word = word,
                        offsetX = if (isTopCard) cardAnimState.offsetX.value else 0f,
                        offsetY = if (isTopCard) cardAnimState.offsetY.value else 0f,
                        stackOffsetX = position.offsetX,
                        stackOffsetY = position.offsetY,
                        rotation = stackRotation,
                        scale = scale,
                        isFlipped = isTopCard && state.isFlipped,
                        flipRotation = if (isTopCard) state.flipRotation else 0f,
                        isTopCard = isTopCard,
                        swipeProgress = if (isTopCard) {
                            cardAnimState.offsetX.value / swipeThresholdPx
                        } else 0f,
                        isRestoringCard = isRestoringCard && isTopCard,
                        onFlip = { viewModel.onEvent(WordMasteryEvent.FlipCard) },
                        onDrag = onDragCallback,
                        onDragEnd = {
                            // Cancel previous job before starting new one
                            activeSwipeJob?.cancel()

                            activeSwipeJob = scope.launch {
                                val currentOffset = cardAnimState.offsetX.value
                                when {
                                    abs(currentOffset) > swipeThresholdPx -> {
                                        targetX = if (currentOffset > 0) 1200f else -1200f

                                        // Use parallel animations with proper tracking
                                        launch {
                                            cardAnimState.offsetX.animateTo(
                                                targetValue = targetX,
                                                animationSpec = SwipeAnimationSpec
                                            )
                                        }
                                        launch {
                                            cardAnimState.offsetY.animateTo(
                                                targetValue = -100f,
                                                animationSpec = SwipeAnimationSpec
                                            )
                                        }
                                        launch {
                                            cardAnimState.scale.animateTo(
                                                targetValue = 0.8f,
                                                animationSpec = SwipeAnimationSpec
                                            )
                                        }

                                        viewModel.onEvent(WordMasteryEvent.RemoveCard(word))
                                        if (currentOffset > 0) {
                                            viewModel.onEvent(WordMasteryEvent.SwipeRemember(word))
                                            onRememberWord(word)
                                        } else {
                                            viewModel.onEvent(WordMasteryEvent.SwipeForgot(word))
                                            onForgotWord(word)
                                        }
                                        resetAnimationState(cardAnimState)
                                    }

                                    else -> returnToCenter(cardAnimState)
                                }

                                // Clear job reference after completion
                                activeSwipeJob = null
                            }
                        },
                        isBookmarked = state.isBookmarked,
                        isHintRevealed = state.isHintRevealed,
                        pinnedLanguage = state.pinnedLanguage,
                        onBookmark = { viewModel.onEvent(WordMasteryEvent.ToggleBookmark) },
                        onSpeak = onSpeak,
                        onRevealHint = { viewModel.onEvent(WordMasteryEvent.RevealHint) }
                    )
                }
            }
        }

        if (!isRestoringCard) {
            SwipeIndicators(
                offsetX = cardAnimState.offsetX.value,
                swipeThreshold = swipeThresholdPx,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

private data class StackPosition(
    val offsetY: Dp,
    val offsetX: Dp,
    val rotation: Float,
    val scale: Float
)