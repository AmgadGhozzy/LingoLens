package com.venom.stackcard.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.venom.resources.R

@Composable
fun SwipeIndicators(
    offsetX: Float, swipeThreshold: Float
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(12.dp)
    ) {
        // Left indicator ("I Remember")
        Box(
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            SwipeIndicator(
                text = stringResource(R.string.i_remember),
                alpha = (-offsetX / swipeThreshold).coerceIn(0f, 1f),
                backgroundColor = MaterialTheme.colorScheme.primary
            )
        }

        // Right indicator ("Forgot")
        Box(
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            SwipeIndicator(
                text = stringResource(R.string.forgot),
                alpha = (offsetX / swipeThreshold).coerceIn(0f, 1f),
                backgroundColor = MaterialTheme.colorScheme.error
            )
        }
    }
}