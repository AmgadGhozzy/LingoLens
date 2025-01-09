package com.venom.stackcard.ui.components

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.venom.stackcard.ui.viewmodel.CardItem

@Composable
fun SwipeableCard(
    card: CardItem,
    offsetX: Float,
    offsetY: Float,
    rotation: Float,
    scale: Float,
    isFlipped: Boolean,
    rotationY: Float,
    onBookmark: () -> Unit,
    onSpeak: () -> Unit,
    onCopy: () -> Unit,
    onFlip: () -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val swipeThreshold = 200f
    val density = LocalDensity.current.density
    val cameraDistance = 20f * density

    Card(modifier = modifier
        .offset { IntOffset(offsetX.toInt(), offsetY.toInt()) }
        .scale(scale)
        .rotate(rotation)
        .graphicsLayer(
            rotationY = rotationY,
            cameraDistance = cameraDistance,
            transformOrigin = TransformOrigin.Center
        )
        .shadow(elevation = 12.dp, shape = RoundedCornerShape(24.dp))
        .clip(RoundedCornerShape(24.dp))
        .fillMaxWidth()
        .aspectRatio(0.9f)
        .pointerInput(Unit) {
            detectDragGestures(onDragEnd = { onDragEnd() },
                onDrag = { _, dragAmount -> onDrag(dragAmount) })
        },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp, pressedElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            CardContent(card = card, isFlipped = isFlipped)

            // Action buttons and indicators
            ActionButtons(
                card = card,
                onBookmark = onBookmark,
                onSpeak = onSpeak,
                onCopy = onCopy,
                onFlip = onFlip
            )

            SwipeIndicators(
                offsetX = offsetX, swipeThreshold = swipeThreshold
            )
        }
    }
}
