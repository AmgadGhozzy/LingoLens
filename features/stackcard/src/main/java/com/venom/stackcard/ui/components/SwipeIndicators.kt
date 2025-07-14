package com.venom.stackcard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.venom.resources.R

@Composable
fun SwipeIndicators(
    offsetX: Float, swipeThreshold: Float
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // Left indicator ("I Remember")
        SwipeIndicator(
            text = stringResource(R.string.i_remember),
            alpha = (-offsetX / swipeThreshold).coerceIn(0f, 1f),
            backgroundColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterStart)
        )

        // Right indicator ("Forgot")
        SwipeIndicator(
            text = stringResource(R.string.forgot),
            alpha = (offsetX / swipeThreshold).coerceIn(0f, 1f),
            backgroundColor = MaterialTheme.colorScheme.error,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Composable
fun SwipeIndicator(
    text: String, alpha: Float, backgroundColor: Color, modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .alpha(alpha)
            .background(color = backgroundColor, shape = CircleShape)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
