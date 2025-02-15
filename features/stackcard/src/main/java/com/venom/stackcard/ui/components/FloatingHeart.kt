package com.venom.stackcard.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun FloatingHeart(
    modifier: Modifier = Modifier,
    tint: Color = Color.Red.copy(alpha = 0.8f)
) {
    val offsetY = remember { Animatable(0f) }
    val offsetX = remember { Random.nextFloat() * 60f - 30f }
    val scale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        coroutineScope {
            launch {
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(250, easing = FastOutSlowInEasing)
                )
            }

            launch {
                offsetY.animateTo(
                    targetValue = -100f,
                    animationSpec = tween(1000, easing = FastOutSlowInEasing)
                )
            }
        }
    }

    Icon(
        imageVector = Icons.Rounded.Favorite,
        contentDescription = null,
        tint = tint,
        modifier = modifier
            .size(24.dp)
            .offset(x = offsetX.dp, y = offsetY.value.dp)
            .scale(scale.value)
            .graphicsLayer {
                alpha = 1f - (offsetY.value / -100f)
                rotationZ = offsetX * 0.2f
            }
    )
}
