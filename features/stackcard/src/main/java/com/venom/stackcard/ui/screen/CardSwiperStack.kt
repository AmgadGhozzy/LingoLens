package com.venom.stackcard.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.resources.R
import com.venom.stackcard.ui.components.SwipeableCard
import com.venom.stackcard.ui.components.UndoButton
import com.venom.stackcard.ui.components.WordCardAnimations.FlipAnimationSpec
import com.venom.stackcard.ui.components.WordCardAnimations.ReturnAnimationSpec
import com.venom.stackcard.ui.components.WordCardAnimations.SWIPE_THRESHOLD
import com.venom.stackcard.ui.components.WordCardAnimations.SwipeAnimationSpec
import com.venom.stackcard.ui.components.WordCardAnimations.calculateRotation
import com.venom.stackcard.ui.components.WordCardAnimations.rememberCardAnimationState
import com.venom.stackcard.ui.components.resetAnimationState
import com.venom.stackcard.ui.components.returnToCenter
import com.venom.stackcard.ui.viewmodel.CardItem
import com.venom.stackcard.ui.viewmodel.CardSwiperEvent.FlipCard
import com.venom.stackcard.ui.viewmodel.CardSwiperEvent.Remove
import com.venom.stackcard.ui.viewmodel.CardSwiperEvent.UndoLastAction
import com.venom.stackcard.ui.viewmodel.CardSwiperViewModel
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * A composable that displays a stack of word cards that can be swiped.
 * Implements card flip and swipe animations with proper recomposition optimization.
 * Also handles displaying the next card in the stack after a swipe.
 *
 * @param viewModel ViewModel for the word card stack
 * @param onForgotCard Callback when a word should be forgotten (swiped left)
 * @param onRememberCard Callback when a word should be remembered (swiped right)
 * @param onBookmarkCard Callback when a word should be bookmarked (swiped left)
 * @param onSpeak Callback when a word should be spoken (swiped right)
 * @param onCopy Callback when a word should be copied
 * @param onUndoLastAction Callback to undo the last swipe action
 * @param modifier Optional modifier for the component
 */

@Composable
fun CardSwiperStack(
    viewModel: CardSwiperViewModel = hiltViewModel(),
    onRememberCard: (CardItem) -> Unit,
    onForgotCard: (CardItem) -> Unit,
    onBookmarkCard: (CardItem) -> Unit,
    onSpeak: (String) -> Unit,
    onCopy: (String) -> Unit,
    onUndoLastAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val state by viewModel.state.collectAsState()
    val contentDescriptionString = stringResource(id = R.string.card_swiper_stack)

    // Animation state
    val cardAnimState = rememberCardAnimationState()
    var isRestoringCard by remember { mutableStateOf(false) }
    var targetX by remember { mutableFloatStateOf(0f) }

    val onDragCallback = remember {
        { dragAmount: Offset ->
            scope.launch {
                with(cardAnimState) {
                    offsetX.snapTo(offsetX.value + dragAmount.x)
                    offsetY.snapTo(offsetY.value + dragAmount.y)
                    rotation.snapTo(calculateRotation(offsetX.value))
                }
            }
            Unit
        }
    }

    Box(modifier = modifier
        .fillMaxSize()
        .semantics(mergeDescendants = true) { contentDescription = contentDescriptionString }) {
        // Render stack of cards
        state.visibleCards.reversed().forEachIndexed { index, card ->
            key(card.id) {
                val isTopCard = index == state.visibleCards.lastIndex
                // Add rotation for stack effct
                val stackRotation = with(state.visibleCards) {
                    when (index) {
                        lastIndex -> cardAnimState.rotation.value
                        lastIndex - 1 -> -10f
                        lastIndex - 2 -> 8f
                        lastIndex - 3 -> 6f
                        else -> 0f
                    }
                }
                // Add scale for stack effect
                val scale = with(state.visibleCards) {
                    when (index) {
                        lastIndex -> cardAnimState.scale.value
                        lastIndex - 1 -> 0.94f
                        lastIndex - 2 -> 0.92f
                        lastIndex - 4 -> 0.9f
                        else -> 0.8f
                    }
                }

                SwipeableCard(card = card,
                    offsetX = if (isTopCard) cardAnimState.offsetX.value else 0f,
                    offsetY = if (isTopCard) cardAnimState.offsetY.value else 0f,
                    rotation = stackRotation,
                    scale = scale,
                    isFlipped = isTopCard && state.isFlipped,
                    rotationY = if (isTopCard) cardAnimState.rotationY.value else 0f,
                    onFlip = {
                        scope.launch {
                            cardAnimState.rotationY.animateTo(
                                targetValue = if (!state.isFlipped) 360f else 0f,
                                animationSpec = FlipAnimationSpec
                            )
                            viewModel.onEvent(FlipCard)
                        }
                    },
                    onDrag = onDragCallback,
                    onDragEnd = {
                        scope.launch {
                            val currentOffset = cardAnimState.offsetX.value
                            when {
                                abs(currentOffset) > SWIPE_THRESHOLD -> {
                                    // Swipe action
                                    targetX = if (currentOffset > 0) 1000f else -1000f
                                    cardAnimState.offsetX.animateTo(
                                        targetValue = targetX, animationSpec = SwipeAnimationSpec
                                    )

                                    // Handle word action
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
                    onCopy = { onCopy(if (state.isFlipped) card.arabicAr else card.englishEn) })
            }
        }

        // Undo button
        if (state.removedCards.isNotEmpty()) {
            UndoButton(
                removedCardsCount = state.removedCards.size, onUndo = {
                    scope.launch {
                        // Set the initial position for the restored card
                        isRestoringCard = true
                        cardAnimState.offsetX.animateTo(
                            targetValue = -targetX, animationSpec = SwipeAnimationSpec
                        )
                        // Trigger undo action
                        viewModel.onEvent(UndoLastAction)

                        // Animate the card back to center
                        cardAnimState.offsetX.animateTo(
                            targetValue = 0f, animationSpec = ReturnAnimationSpec
                        )

                        // Reset states
                        isRestoringCard = false
                    }
                }, modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}
