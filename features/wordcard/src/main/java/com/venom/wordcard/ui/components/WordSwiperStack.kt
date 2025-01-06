package com.venom.wordcard.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.wordcard.data.model.WordCardModel
import com.venom.wordcard.ui.components.WordCardAnimations.rememberCardAnimationState
import com.venom.wordcard.ui.viewmodel.WordSwiperEvent
import com.venom.wordcard.ui.viewmodel.WordSwiperViewModel
import kotlinx.coroutines.launch
import kotlin.collections.asReversed
import kotlin.math.abs

/**
 * A composable that displays a stack of word cards that can be swiped.
 * Implements card flip and swipe animations with proper recomposition optimization.
 *
 * @param words List of words to display in the stack
 * @param onSaveWord Callback when a word is saved (swiped left)
 * @param onSpeakWord Callback when a word should be spoken (swiped right)
 * @param onUndoLastAction Callback to undo the last swipe action
 * @param modifier Optional modifier for the component
 */
@Composable
fun WordSwiperStack(
    words: List<WordCardModel>,
    onSaveWord: (WordCardModel) -> Unit,
    onSpeakWord: (WordCardModel) -> Unit,
    onUndoLastAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = remember { WordSwiperViewModel() }
    val scope = rememberCoroutineScope()
    val state by viewModel.state.collectAsState()

    // Initialize cards if needed
    LaunchedEffect(words) {
        viewModel.initializeCards(words)
    }

    // Animation state
    val cardAnimState = rememberCardAnimationState()

    Box(modifier = modifier
        .fillMaxSize()
        .padding(16.dp)
        .semantics(mergeDescendants = true) {
            contentDescription = "Word swiper stack. Swipe left to bookmark, right for speech."
        }) {
        // Render stack of cards
        state.visibleCards.asReversed().forEachIndexed { index, card ->
            key(card.id) {
                val currentRotation = when (index) {
                    state.visibleCards.lastIndex -> cardAnimState.rotation.value
                    state.visibleCards.lastIndex - 1 -> -8f
                    else -> 8f
                }

                WordCard(card = card,
                    offsetX = if (index == state.visibleCards.lastIndex) cardAnimState.offsetX.value else 0f,
                    offsetY = if (index == state.visibleCards.lastIndex) cardAnimState.offsetY.value else 0f,
                    rotation = currentRotation,
                    scale = when (index) {
                        state.visibleCards.lastIndex -> cardAnimState.scale.value
                        state.visibleCards.lastIndex - 1 -> 0.95f
                        else -> 0.9f
                    },
                    isFlipped = if (index == state.visibleCards.lastIndex) state.isFlipped else false,
                    rotationY = if (index == state.visibleCards.lastIndex) cardAnimState.rotationY.value else 0f,
                    onFlip = {
                        scope.launch {
                            viewModel.onEvent(WordSwiperEvent.FlipCard)
                            cardAnimState.rotationY.animateTo(
                                targetValue = if (!state.isFlipped) 180f else 0f,
                                animationSpec = WordCardAnimations.FlipAnimationSpec
                            )
                        }
                    },
                    onDrag = { dragAmount ->
                        scope.launch {
                            val adjustedDragX = if (state.isFlipped) -dragAmount.x else dragAmount.x
                            cardAnimState.offsetX.snapTo(cardAnimState.offsetX.value + adjustedDragX)
                            cardAnimState.offsetY.snapTo(cardAnimState.offsetY.value + dragAmount.y)
                            cardAnimState.rotation.snapTo(cardAnimState.offsetX.value * 0.1f)
                            cardAnimState.scale.snapTo(1.05f)
                        }
                    },
                    onDragEnd = {
                        scope.launch {
                            val adjustedOffsetX =
                                if (state.isFlipped) -cardAnimState.offsetX.value else cardAnimState.offsetX.value

                            if (abs(cardAnimState.offsetX.value) > WordCardAnimations.SWIPE_THRESHOLD) {
                                if (adjustedOffsetX > 0) {
                                    cardAnimState.offsetX.animateTo(
                                        1000f * (if (state.isFlipped) -1f else 1f),
                                        WordCardAnimations.SwipeAnimationSpec
                                    )
                                    onSpeakWord(card)
                                    viewModel.onEvent(WordSwiperEvent.SpeakWord(card))
                                } else {
                                    cardAnimState.offsetX.animateTo(
                                        -1000f * (if (state.isFlipped) -1f else 1f),
                                        WordCardAnimations.SwipeAnimationSpec
                                    )
                                    onSaveWord(card)
                                    viewModel.onEvent(WordSwiperEvent.SaveWord(card))
                                }
                                resetAnimationState(cardAnimState)
                            } else {
                                returnToCenter(cardAnimState)
                            }
                        }
                    })
            }
        }

        // Undo button with counter
        if (state.removedCards.isNotEmpty()) {
            UndoButton(
                removedCardsCount = state.removedCards.size, onUndo = {
                    scope.launch {
                        viewModel.onEvent(WordSwiperEvent.UndoLastAction)
                        onUndoLastAction()
                    }
                }, modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }
    }
}

private suspend fun resetAnimationState(cardAnimState: CardAnimationState) {
    cardAnimState.apply {
        offsetX.snapTo(0f)
        offsetY.snapTo(0f)
        rotation.snapTo(0f)
        scale.snapTo(1f)
        rotationY.snapTo(0f)
    }
}

private suspend fun returnToCenter(cardAnimState: CardAnimationState) {
    cardAnimState.apply {
        offsetX.animateTo(0f, WordCardAnimations.ReturnAnimationSpec)
        offsetY.animateTo(0f, WordCardAnimations.ReturnAnimationSpec)
        rotation.animateTo(0f, WordCardAnimations.ReturnAnimationSpec)
        scale.animateTo(1f, WordCardAnimations.ReturnAnimationSpec)
    }
}

@Preview
@Composable
fun WordSwiperStackPreview() {
    val sampleWords = listOf(
        WordCardModel(
            id = "1",
            word = "مرحبا",
            translation = "Hello",
            examples = listOf("مرحبا، كيف حالك؟", "مرحبا بالجميع!")
        ), WordCardModel(
            id = "2",
            word = "شكرا",
            translation = "Thank you",
            examples = listOf("شكرا جزيلا!", "على الرحب والسعة")
        ), WordCardModel(
            id = "3",
            word = "مع السلامة",
            translation = "Goodbye",
            examples = listOf("مع السلامة و يوم سعيد!", "إلى اللقاء!")
        ), WordCardModel(
            id = "4",
            word = "مرحبا",
            translation = "Hello",
            examples = listOf("مرحبا، كيف حالك؟", "مرحبا بالجميع!")
        ), WordCardModel(
            id = "5",
            word = "شكرا",
            translation = "Thank you",
            examples = listOf("شكرا جزيلا!", "على الرحب والسعة")
        ), WordCardModel(
            id = "6",
            word = "مع السلامة",
            translation = "Goodbye",
            examples = listOf("مع السلامة و يوم سعيد!", "إلى اللقاء!")
        )
    )
    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        WordSwiperStack(words = sampleWords,
            onSaveWord = { },
            onSpeakWord = { },
            onUndoLastAction = { },
            modifier = Modifier.padding(16.dp)
        )
    }
}