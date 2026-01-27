package com.venom.stackcard.ui.components.flashcard

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.venom.data.mock.MockWordData
import com.venom.domain.model.LanguageOption
import com.venom.domain.model.WordMaster
import com.venom.ui.theme.BrandColors
import com.venom.ui.theme.LingoLensTheme
import kotlin.math.abs
import kotlin.math.min

/**
 * Swipeable card wrapper for Word Mastery feature.
 *
 * @param word The word data to display
 * @param offsetX Horizontal drag offset from swipe engine
 * @param offsetY Vertical drag offset from swipe engine
 * @param stackOffsetX Stack position horizontal offset
 * @param stackOffsetY Stack position vertical offset
 * @param rotation Z-axis rotation from drag
 * @param scale Scale factor for stack depth
 * @param isFlipped Whether card shows back face
 * @param flipRotation Cumulative Y-axis rotation for flip
 * @param isTopCard Whether this is the interactive top card
 * @param swipeProgress Normalized swipe progress (-1 to 1)
 * @param isBookmarked Bookmark state
 * @param isHintRevealed Whether blur hint is revealed
 * @param pinnedLanguage Optional pinned language for back
 * @param isRestoringCard Whether card is animating back from undo
 * @param onFlip Callback for card flip
 * @param onDrag Callback for drag events
 * @param onDragEnd Callback when drag ends
 * @param onBookmark Callback for bookmark toggle
 * @param onSpeak Callback for TTS (text, rate)
 * @param onRevealHint Callback to reveal blur hint
 * @param modifier Modifier for styling
 */
@Composable
fun MasterySwipeableCard(
    word: WordMaster,
    offsetX: Float,
    offsetY: Float,
    stackOffsetX: Dp,
    stackOffsetY: Dp,
    rotation: Float,
    scale: Float,
    isFlipped: Boolean,
    flipRotation: Float = 0f,
    isTopCard: Boolean,
    swipeProgress: Float,
    isBookmarked: Boolean,
    isHintRevealed: Boolean,
    pinnedLanguage: LanguageOption?,
    isRestoringCard: Boolean = false,
    onFlip: () -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onBookmark: () -> Unit,
    onSpeak: (text: String) -> Unit,
    onRevealHint: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val cardShape = RoundedCornerShape(32.dp)

    // Use derivedStateOf to reduce recompositions
    val absProgress = remember(swipeProgress) { abs(swipeProgress) }
    val borderColor by animateColorAsState(
        targetValue = when {
            isRestoringCard -> MaterialTheme.colorScheme.outline.copy(0.2f)
            absProgress < 0.3f -> MaterialTheme.colorScheme.outline.copy(0.2f)
            swipeProgress > 0 -> BrandColors.Green500.copy(0.5f)
            else -> BrandColors.Red500.copy(0.5f)
        },
        animationSpec = tween(200)
    )

    val borderWidth by animateFloatAsState(
        targetValue = if (absProgress > 0.3f) 2f else 1f,
        animationSpec = tween(200)
    )

    val animatedRotationY by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = spring(
            dampingRatio = 0.75f,
            stiffness = Spring.StiffnessLow
        ),
        label = "flipRotation"
    )


    // Cache card dimensions to prevent recalculation
    val cardDimensions = remember(density) {
        val maxWidth = with(density) { configuration.screenWidthDp.dp.toPx() }
        val cardWidth = with(density) { min(maxWidth - 24.dp.toPx(), 400.dp.toPx()).toDp() }
        val cardHeight = cardWidth * 1.8f
        Pair(cardWidth, cardHeight)
    }

    val (cardWidth, cardHeight) = cardDimensions

    // Cache camera distance to prevent recalculation
    val cameraDistance = remember(density) { 12f * density.density }

    Card(
        modifier = modifier
            .size(cardWidth, cardHeight)
            .offset(
                x = ((offsetX / density.density).dp) + stackOffsetX,
                y = ((offsetY / density.density).dp) + stackOffsetY
            )
            // Consolidate graphicsLayer properties to reduce overhead
            .graphicsLayer {
                rotationZ = if (isFlipped) -rotation else rotation
                rotationY = animatedRotationY
                scaleX = scale
                scaleY = scale
                this.cameraDistance = cameraDistance
                // Enable hardware acceleration for smoother animations
                clip = false
                renderEffect = null
            }
            .clip(cardShape)
            .border(borderWidth.dp, borderColor, cardShape)
            // Optimize pointer input by only enabling when needed
            .then(
                if (isTopCard) {
                    Modifier
                        .pointerInput(isFlipped) {
                            detectDragGestures(
                                onDragEnd = { onDragEnd() }
                            ) { change, dragAmount ->
                                change.consume()
                                val correctedDrag = if (isFlipped) {
                                    Offset(-dragAmount.x, dragAmount.y)
                                } else {
                                    dragAmount
                                }
                                onDrag(correctedDrag)
                            }
                        }
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { onFlip() }
                            )
                        }
                } else {
                    Modifier
                }
            ),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        WordCard(
            word = word,
            isFlipped = isFlipped,
            animatedRotationY = animatedRotationY,
            isBookmarked = isBookmarked,
            isHintRevealed = isHintRevealed,
            pinnedLanguage = pinnedLanguage,
            onFlip = onFlip,
            onSpeak = onSpeak,
            onBookmarkToggle = onBookmark,
            onRevealHint = onRevealHint
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF020617)
@Composable
private fun MasterySwipeableCardPreview() {
    LingoLensTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            MasterySwipeableCard(
                word = MockWordData.venomWord,
                offsetX = 0f,
                offsetY = 0f,
                stackOffsetX = 0.dp,
                stackOffsetY = 0.dp,
                rotation = 0f,
                scale = 1f,
                isFlipped = false,
                flipRotation = 0f,
                isTopCard = true,
                swipeProgress = 0f,
                isBookmarked = false,
                isHintRevealed = false,
                pinnedLanguage = null,
                onFlip = {},
                onDrag = {},
                onDragEnd = {},
                onBookmark = {},
                onSpeak = { _ -> },
                onRevealHint = {}
            )
        }
    }
}