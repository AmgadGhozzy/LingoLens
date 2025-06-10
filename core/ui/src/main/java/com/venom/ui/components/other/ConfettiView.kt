package com.venom.ui.components.other

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@Composable
fun ConfettiView(modifier: Modifier = Modifier) {
    val colors = listOf(
        0xE91E63, 0x9C27B0, 0x673AB7, 0x3F51B5,
        0x2196F3, 0x03DAC6, 0x4CAF50, 0xFF9800
    )

    KonfettiView(
        modifier = modifier,
        parties = listOf(
            Party(
                speed = 40f,
                maxSpeed = 60f,
                damping = 0.9f,
                spread = 360,
                colors = colors,
                position = Position.Relative(0.5, 0.4),
                emitter = Emitter(duration = 1000, TimeUnit.MILLISECONDS).max(200)
            )
        )
    )
}