package com.venom.stackcard.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.resources.R
import com.venom.stackcard.ui.components.EnhancedCardAnimations.FlipAnimationSpec
import com.venom.stackcard.ui.components.EnhancedCardAnimations.ReturnAnimationSpec
import com.venom.stackcard.ui.components.EnhancedCardAnimations.SWIPE_THRESHOLD
import com.venom.stackcard.ui.components.EnhancedCardAnimations.ScaleAnimationSpec
import com.venom.stackcard.ui.components.EnhancedCardAnimations.SwipeAnimationSpec
import com.venom.stackcard.ui.components.EnhancedCardAnimations.calculateRotation
import com.venom.stackcard.ui.components.EnhancedSwipeableCard
import com.venom.stackcard.ui.components.LoadingShimmer
import com.venom.stackcard.ui.components.StackProgressIndicator
import com.venom.stackcard.ui.components.SwipeIndicators
import com.venom.stackcard.ui.components.UndoButton
import com.venom.stackcard.ui.components.rememberCardAnimationState
import com.venom.stackcard.ui.components.resetAnimationState
import com.venom.stackcard.ui.components.returnToCenter
import com.venom.stackcard.ui.viewmodel.CardItem
import com.venom.stackcard.ui.viewmodel.CardSwiperEvent
import com.venom.stackcard.ui.viewmodel.CardSwiperEvent.Remove
import com.venom.stackcard.ui.viewmodel.CardSwiperViewModel
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun CardSwiperStack(
    modifier: Modifier = Modifier,
    viewModel: CardSwiperViewModel = hiltViewModel(),
    onRememberCard: (CardItem) -> Unit,
    onForgotCard: (CardItem) -> Unit,
    onBookmarkCard: (CardItem) -> Unit,
    onSpeak: (String) -> Unit,
    onCopy: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val state by viewModel.state.collectAsState()
    val contentDescriptionString = stringResource(id = R.string.card_swiper_stack)

    // Enhanced animation state with additional properties
    val cardAnimState = rememberCardAnimationState()
    var isRestoringCard by remember { mutableStateOf(false) }
    var targetX by remember { mutableFloatStateOf(0f) }

    // Dynamic background gradient based on swipe direction
    val backgroundGradient by remember {
        derivedStateOf {
            val offset = cardAnimState.offsetX.value
            val intensity = (abs(offset) / SWIPE_THRESHOLD).coerceIn(0f, 0.3f)

            when {
                offset > 50f && !isRestoringCard -> Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF4CAF50).copy(alpha = intensity),
                        Color.Transparent
                    ),
                    radius = 800f
                )

                offset < -50f && !isRestoringCard -> Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFE53E3E).copy(alpha = intensity),
                        Color.Transparent
                    ),
                    radius = 800f
                )

                else -> Brush.radialGradient(
                    colors = listOf(Color.Transparent, Color.Transparent),
                    radius = 800f
                )
            }
        }
    }

    val onDragCallback: (Offset) -> Unit = remember {
        { dragAmount ->
            scope.launch {
                with(cardAnimState) {
                    val newOffsetX = offsetX.value + dragAmount.x
                    val newOffsetY = offsetY.value + dragAmount.y

                    offsetX.snapTo(newOffsetX)
                    offsetY.snapTo(newOffsetY)
                    rotation.snapTo(calculateRotation(newOffsetX))

                    // Dynamic scale based on drag distance
                    val dragDistance = sqrt(newOffsetX * newOffsetX + newOffsetY * newOffsetY)
                    val scaleValue = 1f - (dragDistance / 2000f).coerceIn(0f, 0.2f)
                    scale.snapTo(scaleValue)
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .semantics(mergeDescendants = true) { contentDescription = contentDescriptionString },
        contentAlignment = Alignment.Center
    ) {
        // Enhanced stack rendering with depth and shadows
        state.visibleCards.reversed().forEachIndexed { index, card ->
            key(card.id) {
                val isTopCard = index == state.visibleCards.lastIndex
                val stackPosition = state.visibleCards.lastIndex - index

                // Enhanced stack effect with natural positioning
                val stackOffsetY = stackPosition * 4.dp
                val stackOffsetX = if (stackPosition > 0) {
                    (sin(stackPosition * 0.5f) * 8f).dp
                } else 0.dp

                val stackRotation = when (stackPosition) {
                    0 -> cardAnimState.rotation.value
                    1 -> -3f + (cardAnimState.rotation.value * 0.1f)
                    2 -> 2f + (cardAnimState.rotation.value * 0.05f)
                    3 -> -1f
                    else -> 0f
                }

                val scale = when (stackPosition) {
                    0 -> cardAnimState.scale.value
                    1 -> 0.96f
                    2 -> 0.93f
                    3 -> 0.90f
                    else -> 0.87f
                }

                val alpha = when (stackPosition) {
                    0 -> 1f
                    1 -> 0.9f
                    2 -> 0.7f
                    3 -> 0.5f
                    else -> 0.3f
                }

                // Enhanced shadow and blur effects
                val shadowElevation = when (stackPosition) {
                    0 -> 16.dp
                    1 -> 8.dp
                    2 -> 4.dp
                    else -> 2.dp
                }

                val blurRadius = if (stackPosition > 0) (stackPosition * 0.5f).dp else 0.dp

                EnhancedSwipeableCard(
                    card = card,
                    offsetX = if (isTopCard) cardAnimState.offsetX.value else 0f,
                    offsetY = if (isTopCard) cardAnimState.offsetY.value else 0f,
                    stackOffsetX = stackOffsetX,
                    stackOffsetY = stackOffsetY,
                    rotation = stackRotation,
                    scale = scale,
                    alpha = alpha,
                    shadowElevation = shadowElevation,
                    blurRadius = blurRadius,
                    isFlipped = isTopCard && state.isFlipped,
                    rotationY = if (isTopCard) cardAnimState.rotationY.value else 0f,
                    isTopCard = isTopCard,
                    swipeProgress = if (isTopCard) cardAnimState.offsetX.value / SWIPE_THRESHOLD else 0f,
                    isRestoringCard = isRestoringCard && isTopCard,
                    onFlip = {
                        scope.launch {
                            cardAnimState.rotationY.animateTo(
                                targetValue = if (!state.isFlipped) 360f else 0f,
                                animationSpec = FlipAnimationSpec
                            )
                            viewModel.onEvent(CardSwiperEvent.FlipCard)
                        }
                    },
                    onDrag = onDragCallback,
                    onDragEnd = {
                        scope.launch {
                            val currentOffset = cardAnimState.offsetX.value
                            when {
                                abs(currentOffset) > SWIPE_THRESHOLD -> {
                                    // Enhanced swipe animation
                                    targetX = if (currentOffset > 0) 1200f else -1200f

                                    // Animate with slight upward movement
                                    cardAnimState.offsetX.animateTo(
                                        targetValue = targetX,
                                        animationSpec = SwipeAnimationSpec
                                    )
                                    cardAnimState.offsetY.animateTo(
                                        targetValue = -100f,
                                        animationSpec = SwipeAnimationSpec
                                    )
                                    cardAnimState.scale.animateTo(
                                        targetValue = 0.8f,
                                        animationSpec = SwipeAnimationSpec
                                    )

                                    // Handle action
                                    viewModel.onEvent(Remove(card))
                                    if (currentOffset > 0) onRememberCard(card) else onForgotCard(
                                        card
                                    )
                                    resetAnimationState(cardAnimState)
                                }

                                else -> returnToCenter(cardAnimState)
                            }
                        }
                    },
                    onBookmark = { onBookmarkCard(card) },
                    onSpeak = { onSpeak(if (state.isFlipped) card.arabicAr else card.englishEn) },
                    onCopy = { onCopy(if (state.isFlipped) card.arabicAr else card.englishEn) }
                )
            }
        }

        // Enhanced swipe indicators with modern design
        if (!isRestoringCard) {
            SwipeIndicators(
                offsetX = cardAnimState.offsetX.value,
                swipeThreshold = SWIPE_THRESHOLD,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Stack progress indicator
        if (state.visibleCards.isNotEmpty()) {
            StackProgressIndicator(
                current = state.currentCardIndex,
                total = state.visibleCards.size + state.removedCards.size,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 56.dp)
            )
        }

        // Undo button
        if (state.removedCards.isNotEmpty()) {
            UndoButton(
                removedCardsCount = state.removedCards.size,
                onUndo = {
                    scope.launch {
                        isRestoringCard = true

                        // Animate from opposite direction
                        cardAnimState.offsetX.snapTo(-targetX)
                        cardAnimState.offsetY.snapTo(-50f)
                        cardAnimState.scale.snapTo(0.8f)

                        viewModel.onEvent(CardSwiperEvent.UndoLastAction)

                        // Animate back to center with bounce
                        cardAnimState.offsetX.animateTo(
                            targetValue = 0f,
                            animationSpec = ReturnAnimationSpec
                        )
                        cardAnimState.scale.animateTo(
                            targetValue = 1f,
                            animationSpec = ScaleAnimationSpec
                        )

                        isRestoringCard = false
                    }
                },
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }

        // Loading state with shimmer effect
        if (state.isLoading) {
            LoadingShimmer()
        }
    }
}