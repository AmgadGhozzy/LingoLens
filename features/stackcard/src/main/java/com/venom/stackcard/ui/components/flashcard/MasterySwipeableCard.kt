package com.venom.stackcard.ui.components.flashcard

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.venom.domain.model.LanguageOption
import com.venom.domain.model.WordMaster
import com.venom.stackcard.ui.components.mastery.HapticStrength
import com.venom.stackcard.ui.components.mastery.rememberHapticFeedback
import com.venom.ui.components.common.adp
import com.venom.ui.theme.BrandColors
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
    val haptic = rememberHapticFeedback()
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val cardShape = RoundedCornerShape(32.adp)

    val absProgress = remember(swipeProgress) { abs(swipeProgress) }

    val borderColor = when {
        isRestoringCard -> MaterialTheme.colorScheme.outline.copy(0.2f)
        absProgress < 0.3f -> MaterialTheme.colorScheme.outline.copy(0.2f)
        swipeProgress > 0 -> BrandColors.Green500.copy(
            alpha = (0.3f + absProgress * 0.4f).coerceAtMost(
                0.7f
            )
        )

        else -> BrandColors.Red500.copy(alpha = (0.3f + absProgress * 0.4f).coerceAtMost(0.7f))
    }

    val borderWidth = if (absProgress > 0.3f) 2f else 1f

    val animatedRotationY = if (isTopCard) {
        animateFloatAsState(
            targetValue = flipRotation,
            animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMediumLow)
        ).value
    } else 0f

    val adapterMaxWidth = configuration.screenWidthDp.adp
    val adapterPadding = 24.adp
    val adapterLimit = 400.adp
    
    val cardDimensions = remember(density, adapterMaxWidth, adapterPadding, adapterLimit) {
        val maxWidth = with(density) { adapterMaxWidth.toPx() }
        val cardWidth = with(density) { min(maxWidth - adapterPadding.toPx(), adapterLimit.toPx()).toDp() }
        Pair(cardWidth, cardWidth * 1.8f)
    }

    val (cardWidth, cardHeight) = cardDimensions
    val cameraDistance = remember(density) { 12f * density.density }

    Card(
        modifier = modifier
            .size(cardWidth, cardHeight)
            .offset(
                x = ((offsetX / density.density).adp) + stackOffsetX,
                y = ((offsetY / density.density).adp) + stackOffsetY
            )
            .graphicsLayer {
                rotationZ = if (isFlipped) -rotation else rotation
                rotationY = animatedRotationY
                scaleX = scale
                scaleY = scale
                this.cameraDistance = cameraDistance
                clip = false
                renderEffect = null
            }
            .clip(cardShape)
            .border(borderWidth.adp, borderColor, cardShape)
            .then(
                if (isTopCard) {
                    Modifier
                        .pointerInput(isFlipped) {
                            detectDragGestures(onDragEnd = { onDragEnd() }) { change, dragAmount ->
                                change.consume()
                                val correctedDrag = if (isFlipped) Offset(
                                    -dragAmount.x,
                                    dragAmount.y
                                ) else dragAmount
                                onDrag(correctedDrag)
                            }
                        }
                        .pointerInput(Unit) { detectTapGestures(onTap = { 
                            haptic(HapticStrength.MEDIUM)
                            onFlip() 
                        }) }
                } else Modifier
            ),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.adp)
    ) {
        WordCard(
            word = word,
            animatedRotationY = animatedRotationY,
            isBookmarked = isBookmarked,
            isHintRevealed = isHintRevealed,
            pinnedLanguage = pinnedLanguage,
            onSpeak = onSpeak,
            onBookmarkToggle = onBookmark,
            onRevealHint = onRevealHint
        )
    }
}
