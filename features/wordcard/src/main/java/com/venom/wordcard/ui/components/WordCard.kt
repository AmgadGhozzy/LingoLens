package com.venom.wordcard.ui.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.venom.wordcard.data.model.WordCardModel
import kotlin.math.abs

@Composable
fun WordCard(
    card: WordCardModel,
    offsetX: Float,
    offsetY: Float,
    rotation: Float,
    scale: Float,
    isFlipped: Boolean,
    rotationY: Float,
    onFlip: () -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val swipeAlpha = 1f - (abs(offsetX) / 1000f).coerceIn(0f, 0.5f)
    val swipeThreshold = 300f
    val density = LocalDensity.current.density

    Card(modifier = modifier
        .offset {
            IntOffset(
                offsetX.toInt(), offsetY.toInt()
            )
        }
        .scale(scale)
        .rotate(rotation)
        .alpha(swipeAlpha)
        .graphicsLayer(
            rotationY = rotationY, cameraDistance = 12f * density
        )
        .shadow(
            elevation = 8.dp,
            shape = RoundedCornerShape(16.dp),
        )
        .clip(RoundedCornerShape(16.dp))
        .fillMaxWidth()
        .aspectRatio(1.5f)
        .pointerInput(Unit) {
            detectDragGestures(onDragEnd = { onDragEnd() },
                onDrag = { _, dragAmount -> onDrag(dragAmount) })
        }, elevation = CardDefaults.cardElevation(
        defaultElevation = 4.dp, pressedElevation = 8.dp
    )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Front content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        alpha = if (isFlipped) 0f else 1f,
                    )
            ) {
                FrontContent(card)
            }

            // Back content with mirroring effect
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        alpha = if (isFlipped) 1f else 0f, rotationY = if (isFlipped) 180f else 0f
                    )
            ) {
                BackContent(card)
            }

            // Action buttons and indicators
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        rotationY = if (isFlipped) 180f else 0f
                    )
            ) {
                ActionButtons(
                    card = card, onFlip = onFlip
                )

                SwipeIndicators(
                    offsetX = offsetX, swipeThreshold = swipeThreshold, isFlipped = isFlipped
                )
            }
        }
    }
}
