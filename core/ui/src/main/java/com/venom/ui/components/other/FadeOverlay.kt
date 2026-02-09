package com.venom.ui.components.other

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.venom.ui.components.common.adp

@Composable
fun FadeOverlay(
    modifier: Modifier = Modifier,
    top: Boolean,
    height: Dp = 40.adp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(
                brush = Brush.verticalGradient(
                    colors = if (top) {
                        listOf(
                            MaterialTheme.colorScheme.background.copy(0.5f),
                            Color.Transparent
                        )
                    } else {
                        listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background.copy(0.5f)
                        )
                    }
                )
            )
    )
}
