package com.venom.ui.components.buttons

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.venom.resources.R
import com.venom.ui.components.common.adp
import com.venom.ui.theme.lingoLens

/**
 * Microphone button with press-and-hold functionality.
 *
 * @param onPressStart Callback when the button is pressed down (start recording)
 * @param onPressEnd Callback when the button is released (stop recording)
 * @param modifier Optional modifier for the button
 * @param size Size of the button (default 56.dp)
 * @param enabled Whether the button is enabled
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun STTButton(
    onPressStart: () -> Unit,
    onPressEnd: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 56.adp,
    enabled: Boolean = true,
    isActive: Boolean = false,
) {
    // Permission handling
    val micPermission = rememberPermissionState(android.Manifest.permission.RECORD_AUDIO)
    var isPressed by remember { mutableStateOf(false) }

    // MD3 Color system - container colors
    val containerColor = when {
        !enabled -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f)
        isPressed || isActive -> MaterialTheme.colorScheme.primaryContainer
        else -> Color.Transparent
    }

    // Gradient for inactive state
    val inactiveGradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.lingoLens.glass.tintPrimary.copy(0.05f),
            MaterialTheme.lingoLens.glass.tintSecondary.copy(0.05f),
            MaterialTheme.lingoLens.glass.tintAccent.copy(0.05f)
        )
    )

    val contentColor = when {
        !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        isPressed || isActive -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    // Animations
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Pulse effect when active
        if (isPressed || isActive) {
            Box(
                modifier = Modifier
                    .size(size * 1.3f)
                    .scale(scale)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = pulseAlpha * 0.2f),
                        shape = CircleShape
                    )
            )
        }

        // Main button
        Box(
            modifier = Modifier
                .size(size)
                .scale(scale)
                .background(
                    brush = if (isPressed || isActive || !enabled) {
                        Brush.linearGradient(colors = listOf(containerColor, containerColor))
                    } else {
                        inactiveGradient
                    },
                    shape = CircleShape
                )
                .pointerInput(enabled) {
                    if (enabled) {
                        detectTapGestures(
                            onPress = {
                                // Check and request permission
                                if (!micPermission.status.isGranted) {
                                    micPermission.launchPermissionRequest()
                                    return@detectTapGestures
                                }

                                isPressed = true
                                onPressStart()
                                tryAwaitRelease()
                                isPressed = false
                                onPressEnd()
                            }
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(
                    id = if (isPressed || isActive) R.drawable.ic_audio_lines
                    else R.drawable.ic_mic
                ),
                contentDescription = "Microphone",
                tint = contentColor,
                modifier = Modifier.size(size * 0.45f)
            )
        }
    }
}