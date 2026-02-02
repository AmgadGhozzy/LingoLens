package com.venom.stackcard.ui.components.flashcard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.domain.model.WordMaster
import com.venom.resources.R
import com.venom.stackcard.ui.components.flashcard.OptimizedCardAnimations.SWIPE_THRESHOLD_DP
import com.venom.stackcard.ui.components.flashcard.OptimizedCardAnimations.SwipeAnimationSpec
import com.venom.stackcard.ui.components.flashcard.OptimizedCardAnimations.calculateRotation
import com.venom.stackcard.ui.components.flashcard.OptimizedCardAnimations.calculateThrowTarget
import com.venom.stackcard.ui.components.mastery.HapticStrength
import com.venom.stackcard.ui.components.mastery.rememberHapticFeedback
import com.venom.stackcard.ui.viewmodel.WordMasteryEvent
import com.venom.stackcard.ui.viewmodel.WordMasteryViewModel
import com.venom.ui.components.common.adp
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val state by viewModel.uiState.collectAsState()
    val contentDescriptionString = stringResource(id = R.string.card_swiper_stack)

    val topCardId = state.visibleCards.firstOrNull()?.wordEn ?: ""
    val cardAnimState = rememberCardAnimationState()

    // Reset animation when top card changes
    DisposableEffect(topCardId) {
        scope.launch { resetAnimationState(cardAnimState) }
        onDispose { }
    }

    var activeSwipeJob by remember { mutableStateOf<Job?>(null) }

    val swipeThresholdAdp = SWIPE_THRESHOLD_DP.adp
    val swipeThresholdPx = remember(density, swipeThresholdAdp) {
        with(density) { swipeThresholdAdp.toPx() }
    }

    val onDragCallback: (Offset) -> Unit = remember(cardAnimState, scope) {
        { dragAmount ->
            activeSwipeJob?.cancel()
            cardAnimState.cancelAnimations()
            scope.launch {
                val newOffsetX = cardAnimState.offsetX.value + dragAmount.x
                val newOffsetY = cardAnimState.offsetY.value + dragAmount.y
                cardAnimState.offsetX.snapTo(newOffsetX)
                cardAnimState.offsetY.snapTo(newOffsetY)
                cardAnimState.rotation.snapTo(calculateRotation(newOffsetX))
            }
        }
    }

    val limitedCards = remember(state.visibleCards) {
        state.visibleCards.take(MAX_VISIBLE_CARDS)
    }

    val fourAdp = 4.adp
    val eightAdp = 8.adp
    val stackPositions = remember(limitedCards.size, fourAdp, eightAdp) {
        List(limitedCards.size) { index ->
            val stackPosition = limitedCards.lastIndex - index
            StackPosition(
                offsetY = fourAdp * stackPosition,
                offsetX = if (stackPosition > 0) eightAdp * sin(stackPosition * 0.5f) else 0.dp,
                rotation = when (stackPosition) {
                    1 -> -3f; 2 -> 2f; else -> 0f
                },
                scale = when (stackPosition) {
                    1 -> 0.96f; 2 -> 0.93f; else -> 0.90f
                }
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            activeSwipeJob?.cancel()
            cardAnimState.cancelAnimations()
            cardAnimState.cleanup()
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

                    val scale =
                        if (stackPosition == 0) cardAnimState.calculateScale() else position.scale

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
                        swipeProgress = if (isTopCard) cardAnimState.offsetX.value / swipeThresholdPx else 0f,
                        isBookmarked = state.isBookmarked,
                        isHintRevealed = state.isHintRevealed,
                        pinnedLanguage = state.pinnedLanguage,
                        onFlip = { viewModel.onEvent(WordMasteryEvent.FlipCard) },
                        onDrag = onDragCallback,
                        onDragEnd = {
                            activeSwipeJob?.cancel()
                            cardAnimState.cancelAnimations()
                            activeSwipeJob = scope.launch {
                                val currentOffset = cardAnimState.offsetX.value
                                when {
                                    abs(currentOffset) > swipeThresholdPx -> {
                                        val velocity = cardAnimState.offsetX.velocity
                                        val targetX = calculateThrowTarget(currentOffset, velocity)

                                        // Strong haptic for swipe completion
                                        haptic(HapticStrength.STRONG)

                                        launch {
                                            cardAnimState.offsetY.animateTo(
                                                -100f,
                                                SwipeAnimationSpec
                                            )
                                        }
                                        cardAnimState.offsetX.animateTo(targetX, SwipeAnimationSpec)
                                        resetAnimationState(cardAnimState)

                                        if (currentOffset > 0) {
                                            viewModel.onEvent(WordMasteryEvent.SwipeRemember(word))
                                            onRememberWord(word)
                                        } else {
                                            viewModel.onEvent(WordMasteryEvent.SwipeForgot(word))
                                            onForgotWord(word)
                                        }
                                        viewModel.onEvent(WordMasteryEvent.RemoveCard(word))
                                    }

                                    else -> returnToCenter(cardAnimState)
                                }
                                activeSwipeJob = null
                            }
                        },
                        onBookmark = { viewModel.onEvent(WordMasteryEvent.ToggleBookmark) },
                        onSpeak = onSpeak,
                        onRevealHint = { viewModel.onEvent(WordMasteryEvent.RevealHint) }
                    )
                }
            }
        }

        SwipeIndicators(
            offsetX = cardAnimState.offsetX.value,
            swipeThreshold = swipeThresholdPx,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

private data class StackPosition(
    val offsetY: Dp,
    val offsetX: Dp,
    val rotation: Float,
    val scale: Float
)