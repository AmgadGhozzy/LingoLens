package com.venom.stackcard.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.venom.stackcard.ui.viewmodel.CardItem
import com.venom.ui.theme.ThemeColors.GlassPrimary
import com.venom.ui.theme.ThemeColors.GlassSecondary
import com.venom.ui.theme.ThemeColors.GlassTertiary
import kotlin.math.abs

@Composable
fun SwipeableCard(
    card: CardItem,
    offsetX: Float,
    offsetY: Float,
    stackOffsetX: Dp,
    stackOffsetY: Dp,
    rotation: Float,
    scale: Float,
    alpha: Float,
    shadowElevation: Dp,
    blurRadius: Dp,
    isFlipped: Boolean,
    isTopCard: Boolean,
    swipeProgress: Float,
    isRestoringCard: Boolean = false,
    onFlip: () -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onBookmark: () -> Unit,
    onSpeak: () -> Unit,
    onCopy: () -> Unit,
    onClearTranslation: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    // Dynamic card colors based on swipe direction and restoration state
    val cardBackgroundColor by animateColorAsState(
        targetValue = when {
            isRestoringCard -> MaterialTheme.colorScheme.surfaceContainerLowest
            abs(swipeProgress) < 0.3f -> MaterialTheme.colorScheme.surfaceContainerLowest
            swipeProgress > 0 -> MaterialTheme.colorScheme.primary.copy(0.1f)
            else -> MaterialTheme.colorScheme.error.copy(0.1f)
        },
        animationSpec = tween(200)
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isRestoringCard -> MaterialTheme.colorScheme.outline.copy(0.1f)
            abs(swipeProgress) < 0.3f -> MaterialTheme.colorScheme.outline.copy(0.2f)
            swipeProgress > 0 -> MaterialTheme.colorScheme.primary.copy(0.6f)
            else -> MaterialTheme.colorScheme.error.copy(0.6f)
        },
        animationSpec = tween(200)
    )

    val borderWidth by animateFloatAsState(
        targetValue = if (abs(swipeProgress) > 0.3f) 2f else 1f,
        animationSpec = tween(200)
    )

    // Glassmorphism effect for modern look
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val glassMorphismBrush = remember(onSurfaceColor) {
        Brush.verticalGradient(
            colors = listOf(
                onSurfaceColor.copy(0.1f),
                onSurfaceColor.copy(0.06f),
                Color.Transparent
            ),
            startY = 0f,
            endY = 200f
        )
    }

    // Flip transition for smooth flip
    val transition = updateTransition(isFlipped)
    val animatedRotationY by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 800) }
    ) { state -> if (state) 180f else 0f }

    Card(
        modifier = modifier
            .size(280.dp, 400.dp)
            .offset(
                x = with(density) { (offsetX / density.density).dp } + stackOffsetX,
                y = with(density) { (offsetY / density.density).dp } + stackOffsetY
            )
            .graphicsLayer(
                rotationZ = rotation,
                rotationY = animatedRotationY,
                scaleX = scale,
                scaleY = scale,
                alpha = alpha,
                cameraDistance = 12f
            )
            .shadow(
                elevation = shadowElevation,
                shape = RoundedCornerShape(24.dp),
                ambientColor = MaterialTheme.colorScheme.primary.copy(0.1f),
                spotColor = MaterialTheme.colorScheme.primary.copy(0.2f)
            )
            .then(
                if (blurRadius > 0.dp) Modifier.blur(blurRadius) else Modifier
            )
            .clip(RoundedCornerShape(24.dp))
            .background(cardBackgroundColor)
            .background(glassMorphismBrush)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        GlassPrimary.copy(0.1f),
                        GlassSecondary.copy(0.1f),
                        GlassTertiary.copy(0.1f)
                    )
                )
            )
            .border(
                width = borderWidth.dp,
                color = borderColor,
                shape = RoundedCornerShape(24.dp)
            )
            .pointerInput(isTopCard) {
                if (isTopCard) {
                    detectDragGestures(
                        onDragEnd = {
                            onDragEnd()
                            if (isTopCard) onClearTranslation()
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount)
                    }
                }
            },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().graphicsLayer(rotationY = animatedRotationY)
        ) {
            CardContent(
                card = card,
                isFlipped = isFlipped,
                onDragEnd = onDragEnd,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            )

            if (isTopCard) {
                ActionButtons(
                    card = card,
                    onBookmark = onBookmark,
                    onSpeak = onSpeak,
                    onCopy = onCopy,
                    onFlip = onFlip
                )
            }
        }
    }
}
