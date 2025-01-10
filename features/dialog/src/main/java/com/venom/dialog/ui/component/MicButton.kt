package com.venom.dialog.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun MicButton(
    isListening: Boolean,
    onClick: () -> Unit,
    containerColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    // Animation for the pulsing effect
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    // Animation for the icon rotation
    val rotation by animateFloatAsState(
        targetValue = if (isListening) 360f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = ""
    )

    Box(
        modifier = modifier
            .size(72.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(containerColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isListening) Icons.Rounded.Stop else Icons.Rounded.Mic,
            contentDescription = if (isListening) "Stop recording" else "Start recording",
            modifier = Modifier
                .size(32.dp)
                .graphicsLayer(
                    rotationZ = rotation,
                    scaleX = if (isListening) 1.2f else 1f,
                    scaleY = if (isListening) 1.2f else 1f
                ),
            tint = iconColor
        )
    }
}
