package com.venom.dialog.ui.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color

@Composable
fun AnimatedPlayButton(
    onClick: () -> Unit, tint: Color, modifier: Modifier = Modifier
) {
    var isPlaying by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPlaying) 0.8f else 1f, animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow
        ), label = ""
    )

    IconButton(
        onClick = {
            isPlaying = !isPlaying
            onClick()
        }, modifier = modifier.scale(scale)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
            contentDescription = "Play translation",
            tint = tint
        )
    }
}
