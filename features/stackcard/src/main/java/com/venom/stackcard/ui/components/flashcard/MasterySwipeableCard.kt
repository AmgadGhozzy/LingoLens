package com.venom.stackcard.ui.components.flashcard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import com.venom.domain.model.LanguageOption
import com.venom.domain.model.WordMaster
import com.venom.stackcard.ui.components.mastery.HapticStrength
import com.venom.stackcard.ui.components.mastery.rememberHapticFeedback
import com.venom.ui.components.common.adp
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

// Pre-computed shape to avoid recreation


/**
 * Optimized swipeable card that reads animation values inside graphicsLayer
 * to avoid recomposition during animations.
 *
 * Key optimizations:
 * 1. Animation state read inside graphicsLayer (no recomposition)
 * 2. Pre-allocated colors via CardColors object
 * 3. Cached dimension calculations
 * 4. Stable callbacks with remember
 */
@Composable
fun MasterySwipeableCard(
    word: WordMaster,
    animState: CardAnimationState?,
    stackPosition: StackPosition,
    isTopCard: Boolean,
    isFlipped: Boolean,
    flipRotation: Float,
    swipeThresholdPx: Float,
    isBookmarked: Boolean,
    isHintRevealed: Boolean,
    pinnedLanguage: LanguageOption?,
    onFlip: () -> Unit,
    onDrag: (Float, Float) -> Unit,
    onDragEnd: () -> Unit,
    onBookmark: () -> Unit,
    onSpeak: (String) -> Unit,
    onRevealHint: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = rememberHapticFeedback()
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val cardShape = RoundedCornerShape(32.adp)
    // Animate flip rotation - this is the only animated state that triggers recomposition
    // because it's user-initiated and infrequent
    val animatedFlipRotation by animateFloatAsState(
        targetValue = flipRotation,
        animationSpec = OptimizedCardAnimations.FlipAnimationSpec,
        label = "flipRotation"
    )

    // Cache card dimensions - only recalculate on configuration change
    val screenWidthDp = configuration.screenWidthDp.adp
    val paddingDp = 24.adp
    val limitDp = 400.adp

    val (cardWidth, cardHeight) = remember(screenWidthDp, paddingDp, limitDp, density) {
        val maxWidthPx = with(density) { screenWidthDp.toPx() }
        val paddingPx = with(density) { paddingDp.toPx() }
        val limitPx = with(density) { limitDp.toPx() }
        val cardWidthPx = min(maxWidthPx - paddingPx, limitPx)
        val cardWidthDp = with(density) { cardWidthPx.toDp() }
        Pair(cardWidthDp, cardWidthDp * 1.8f)
    }

    // Pre-compute stack offsets in pixels for graphicsLayer
    val stackOffsetXDp = stackPosition.offsetXDp.adp
    val stackOffsetYDp = stackPosition.offsetYDp.adp

    val stackOffsetXPx = remember(stackOffsetXDp, density) {
        with(density) { stackOffsetXDp.toPx() }
    }
    val stackOffsetYPx = remember(stackOffsetYDp, density) {
        with(density) { stackOffsetYDp.toPx() }
    }

    val cameraDistance = remember(density) { 12f * density.density }

    // Derive border color without allocation in composition
    // This only recomputes when animState values change significantly
    val borderState: State<Pair<Color, Float>> = remember(animState, swipeThresholdPx) {
        derivedStateOf {
            if (animState == null || !isTopCard) {
                Pair(CardColors.NeutralBorder, 1f)
            } else {
                val progress = (animState.offsetX.value / swipeThresholdPx).coerceIn(-1f, 1f)
                Pair(
                    CardColors.getBorderColor(progress),
                    CardColors.getBorderWidth(progress)
                )
            }
        }
    }

    val (borderColor, borderWidth) = borderState.value

    Card(
        modifier = modifier
            .size(cardWidth, cardHeight)
            .graphicsLayer {
                // ALL DYNAMIC ANIMATION VALUES READ HERE
                // This lambda runs on every frame but doesn't trigger recomposition

                if (isTopCard && animState != null) {
                    val currentOffsetX = animState.offsetX.value
                    val currentOffsetY = animState.offsetY.value
                    val currentRotation = animState.rotation.value

                    // Translation combines drag offset and stack position
                    translationX = currentOffsetX + stackOffsetXPx
                    translationY = currentOffsetY + stackOffsetYPx

                    // Rotation flips direction when card is flipped
                    rotationZ = if (isFlipped) -currentRotation else currentRotation
                    rotationY = animatedFlipRotation

                    // Dynamic scale based on drag distance
                    val dragDistance = sqrt(
                        currentOffsetX.pow(2) + currentOffsetY.pow(2)
                    )
                    val dynamicScale = (1f - dragDistance * 0.0003f).coerceIn(0.8f, 1f)
                    scaleX = dynamicScale
                    scaleY = dynamicScale
                } else {
                    // Static positioning for non-top cards
                    translationX = stackOffsetXPx
                    translationY = stackOffsetYPx
                    rotationZ = stackPosition.rotation
                    scaleX = stackPosition.scale
                    scaleY = stackPosition.scale
                    rotationY = 0f
                }

                this.cameraDistance = cameraDistance
                clip = false
            }
            .clip(cardShape)
            .border(borderWidth.adp, borderColor, cardShape)
            .then(
                if (isTopCard) {
                    Modifier
                        .pointerInput(isFlipped) {
                            detectDragGestures(
                                onDragEnd = { onDragEnd() },
                                onDragCancel = { onDragEnd() }
                            ) { change, dragAmount ->
                                change.consume()
                                // Correct drag direction when card is flipped
                                val correctedX = if (isFlipped) -dragAmount.x else dragAmount.x
                                onDrag(correctedX, dragAmount.y)
                            }
                        }
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    haptic(HapticStrength.MEDIUM)
                                    onFlip()
                                }
                            )
                        }
                } else Modifier
            ),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.adp)
    ) {
        WordCard(
            word = word,
            animatedRotationY = if (isTopCard) animatedFlipRotation else 0f,
            isBookmarked = isBookmarked,
            isHintRevealed = isHintRevealed,
            pinnedLanguage = pinnedLanguage,
            onSpeak = onSpeak,
            onBookmarkToggle = onBookmark,
            onRevealHint = onRevealHint
        )
    }
}
