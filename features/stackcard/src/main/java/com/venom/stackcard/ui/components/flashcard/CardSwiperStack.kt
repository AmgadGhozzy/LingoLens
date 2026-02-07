package com.venom.stackcard.ui.components.flashcard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.domain.model.WordMaster
import com.venom.resources.R
import com.venom.stackcard.ui.components.mastery.HapticStrength
import com.venom.stackcard.ui.components.mastery.rememberHapticFeedback
import com.venom.stackcard.ui.viewmodel.WordMasteryEvent
import com.venom.stackcard.ui.viewmodel.WordMasteryViewModel
import kotlin.math.abs
import kotlin.math.sin

private const val MAX_VISIBLE_CARDS = 3

@Composable
fun CardSwiperStack(
    modifier: Modifier = Modifier,
    viewModel: WordMasteryViewModel = hiltViewModel(),
    onSpeak: (text: String) -> Unit,
    onRememberWord: (WordMaster) -> Unit = {},
    onForgotWord: (WordMaster) -> Unit = {},
) {
    val haptic = rememberHapticFeedback()
    val density = LocalDensity.current
    val state by viewModel.uiState.collectAsState()
    val contentDescriptionString = stringResource(id = R.string.card_swiper_stack)

    // Single animation state for top card
    val cardAnimState = rememberCardAnimationState()

    // Compute threshold once
    val swipeThresholdPx = remember(density) {
        with(density) { OptimizedCardAnimations.SWIPE_THRESHOLD_DP.dp.toPx() }
    }

    // Reset animation when top card changes — single source of truth
    val topCardId = state.visibleCards.firstOrNull()?.wordEn
    LaunchedEffect(topCardId) {
        cardAnimState.resetImmediate()
    }

    // Limit visible cards for performance
    val limitedCards = remember(state.visibleCards) {
        state.visibleCards.take(MAX_VISIBLE_CARDS)
    }

    // Pre-compute stack positions
    val stackPositions = remember(limitedCards.size) {
        List(limitedCards.size) { index ->
            val stackIndex = limitedCards.lastIndex - index
            StackPosition(
                offsetYDp = 4f * stackIndex,
                offsetXDp = if (stackIndex > 0) 8f * sin(stackIndex * 0.5f) else 0f,
                rotation = when (stackIndex) {
                    1 -> -3f
                    2 -> 2f
                    else -> 0f
                },
                scale = when (stackIndex) {
                    0 -> 1f
                    1 -> 0.96f
                    2 -> 0.93f
                    else -> 0.90f
                }
            )
        }
    }

    // Stable drag callback
    val onDragCallback = remember(cardAnimState) {
        { deltaX: Float, deltaY: Float ->
            cardAnimState.updateDragPosition(deltaX, deltaY)
        }
    }

    // Stabilize unstable lambda parameters
    val currentOnRememberWord by rememberUpdatedState(onRememberWord)
    val currentOnForgotWord by rememberUpdatedState(onForgotWord)
    val currentHaptic by rememberUpdatedState(haptic)

    // Stable drag end handler
    val onDragEndCallback = remember(cardAnimState, swipeThresholdPx, viewModel) {
        { word: WordMaster ->
            val currentOffset = cardAnimState.offsetX.value
            val absOffset = abs(currentOffset)

            when {
                absOffset > swipeThresholdPx -> {
                    currentHaptic(HapticStrength.STRONG)

                    val targetX = OptimizedCardAnimations.calculateThrowTarget(
                        currentOffset,
                        cardAnimState.offsetX.velocity
                    )

                    cardAnimState.animateSwipeOff(targetX) {
                        if (currentOffset > 0) {
                            viewModel.onEvent(WordMasteryEvent.SwipeRemember(word))
                            currentOnRememberWord(word)
                        } else {
                            viewModel.onEvent(WordMasteryEvent.SwipeForgot(word))
                            currentOnForgotWord(word)
                        }
                        viewModel.onEvent(WordMasteryEvent.RemoveCard(word))
                    }
                }

                else -> {
                    cardAnimState.animateReturnToCenter()
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .semantics(mergeDescendants = true) {
                contentDescription = contentDescriptionString
            },
        contentAlignment = Alignment.Center
    ) {
        limitedCards.reversed().forEachIndexed { renderIndex, word ->
            key(word.wordEn) {
                val isTopCard = renderIndex == limitedCards.lastIndex
                val position = stackPositions.getOrNull(renderIndex)

                if (position != null) {
                    MasterySwipeableCard(
                        word = word,
                        animState = if (isTopCard) cardAnimState else null,
                        stackPosition = position,
                        isTopCard = isTopCard,
                        isFlipped = if (isTopCard) state.isFlipped else false,
                        flipRotation = if (isTopCard) state.flipRotation else 0f,
                        swipeThresholdPx = swipeThresholdPx,
                        isBookmarked = if (isTopCard) state.isBookmarked else false,
                        isHintRevealed = if (isTopCard) state.isHintRevealed else false,
                        pinnedLanguage = if (isTopCard) state.pinnedLanguage else null,
                        onFlip = { viewModel.onEvent(WordMasteryEvent.FlipCard) },
                        onDrag = onDragCallback,
                        onDragEnd = { onDragEndCallback(word) },
                        onBookmark = { viewModel.onEvent(WordMasteryEvent.ToggleBookmark) },
                        onSpeak = onSpeak,
                        onRevealHint = { viewModel.onEvent(WordMasteryEvent.RevealHint) }
                    )
                }
            }
        }

        SwipeIndicators(
            animState = cardAnimState,
            swipeThresholdPx = swipeThresholdPx,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}