package com.venom.ui.components.buttons

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.theme.ThemeColors.GlassPrimary
import com.venom.ui.theme.ThemeColors.GlassSecondary
import com.venom.ui.theme.ThemeColors.GlassTertiary

/**
 * Microphone button with press-and-hold functionality.
 *
 * @param onPressStart Callback when the button is pressed down (start recording)
 * @param onPressEnd Callback when the button is released (stop recording)
 * @param modifier Optional modifier for the button
 * @param size Size of the button (default 56.dp)
 * @param icon Drawable resource for the microphone icon
 * @param enabled Whether the button is enabled
 * @param activeColor Color when the button is pressed
 * @param inactiveColor Color when the button is not pressed
 */
@Composable
fun STTButton(
    onPressStart: () -> Unit,
    onPressEnd: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    icon: Int = R.drawable.icon_mic,
    enabled: Boolean = true,
    isActive: Boolean = false,
    activeColor: Color = if (isActive) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    },
    inactiveColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.15f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed) activeColor else inactiveColor,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    val iconColor by animateColorAsState(
        targetValue = if (isPressed) {
            Color.White
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .background(
                brush = if (isPressed) {
                    Brush.radialGradient(
                        colors = listOf(
                            backgroundColor,
                            backgroundColor.copy(alpha = 0.8f)
                        )
                    )
                } else {
                    Brush.linearGradient(
                        colors = listOf(
                            GlassPrimary.copy(0.1f),
                            GlassSecondary.copy(0.1f),
                            GlassTertiary.copy(0.1f)
                        )
                    )
                },
                shape = CircleShape
            )
            .pointerInput(enabled) {
                if (enabled) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            onPressStart()
                            // Wait for the press to be released
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
            painter = painterResource(id = icon),
            contentDescription = "Microphone",
            tint = iconColor,
            modifier = Modifier.size(size * 0.5f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun STTButtonPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            STTButton(
                onPressStart = { },
                onPressEnd = { }
            )
        }
    }
}
