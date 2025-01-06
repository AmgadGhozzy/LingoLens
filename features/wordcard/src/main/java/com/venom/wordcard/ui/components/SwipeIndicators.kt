package com.venom.wordcard.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun SwipeIndicators(
    offsetX: Float, swipeThreshold: Float, isFlipped: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(rotationY = if (isFlipped) 180f else 0f)
    ) {
        // Left indicator ("I Remember")
        Box(
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            SwipeIndicator(
                text = "I Remember",
                alpha = (-offsetX / swipeThreshold).coerceIn(0f, 1f),
                backgroundColor = MaterialTheme.colorScheme.primary
            )
        }

        // Right indicator ("Forgot")
        Box(
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            SwipeIndicator(
                text = "Forgot",
                alpha = (offsetX / swipeThreshold).coerceIn(0f, 1f),
                backgroundColor = MaterialTheme.colorScheme.error
            )
        }
    }
}