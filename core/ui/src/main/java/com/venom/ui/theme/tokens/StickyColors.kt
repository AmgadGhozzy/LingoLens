package com.venom.ui.theme.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.venom.ui.theme.BrandColors as B

@Immutable
data class StickyColorScheme(
    val bg: Color,
    val border: Color,
    val text: Color,
    val label: Color,
    val tape: Color
)

@Immutable
data class StickyColors(
    val yellow: StickyColorScheme,
    val amber: StickyColorScheme,
    val rose: StickyColorScheme,
    val blue: StickyColorScheme
)

val LightStickyColors = StickyColors(
    yellow = StickyColorScheme(
        bg = B.Yellow50.copy(alpha = 0.96f),
        border = B.Yellow100,
        text = B.Yellow900,
        label = B.Yellow800.copy(alpha = 0.6f),
        tape = B.Yellow300.copy(alpha = 0.85f)
    ),

    amber = StickyColorScheme(
        bg = B.Amber50.copy(alpha = 0.96f),
        border = B.Amber100,
        text = B.Amber900,
        label = B.Amber800.copy(alpha = 0.6f),
        tape = B.Amber300.copy(alpha = 0.85f)
    ),

    rose = StickyColorScheme(
        bg = B.Rose50.copy(alpha = 0.96f),
        border = B.Rose100,
        text = B.Rose900,
        label = B.Rose800.copy(alpha = 0.6f),
        tape = B.Rose300.copy(alpha = 0.85f)
    ),

    blue = StickyColorScheme(
        bg = B.Blue50.copy(alpha = 0.96f),
        border = B.Blue100,
        text = B.Blue900,
        label = B.Blue800.copy(alpha = 0.6f),
        tape = B.Blue300.copy(alpha = 0.85f)
    )
)

val DarkStickyColors = StickyColors(
    yellow = StickyColorScheme(
        bg = B.Yellow900.copy(0.3f),
        border = B.Yellow700.copy(0.4f),
        text = B.Yellow100,
        label = B.Yellow200.copy(0.7f),
        tape = B.Yellow500.copy(0.6f)
    ),
    amber = StickyColorScheme(
        bg = B.Amber900.copy(0.3f),
        border = B.Amber700.copy(0.4f),
        text = B.Amber100,
        label = B.Amber200.copy(0.7f),
        tape = B.Amber500.copy(0.6f)
    ),
    rose = StickyColorScheme(
        bg = B.Rose900.copy(0.3f),
        border = B.Rose700.copy(0.4f),
        text = B.Rose100,
        label = B.Rose200.copy(0.7f),
        tape = B.Rose500.copy(0.6f)
    ),
    blue = StickyColorScheme(
        bg = B.Blue900.copy(0.3f),
        border = B.Blue700.copy(0.4f),
        text = B.Blue100,
        label = B.Blue200.copy(0.7f),
        tape = B.Blue500.copy(0.6f)
    )
)

val LocalStickyColors = staticCompositionLocalOf { LightStickyColors }