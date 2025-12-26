package com.venom.textsnap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.venom.ui.components.common.PulseAnimation

@Composable
fun OcrLoadingOverlay(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(0.3f)),
        contentAlignment = Alignment.Center
    ) {
        PulseAnimation(
            color = MaterialTheme.colorScheme.background,
            size = 156f
        )
    }
}
