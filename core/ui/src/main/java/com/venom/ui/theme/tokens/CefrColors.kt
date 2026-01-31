package com.venom.ui.theme.tokens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.venom.domain.model.CefrLevel
import com.venom.ui.theme.BrandColors as B

@Immutable
data class CefrColorScheme(
    val background: Color,
    val content: Color,
    val border: Color
)

@Immutable
data class CefrColors(
    val beginner: CefrColorScheme,
    val intermediate: CefrColorScheme,
    val advanced: CefrColorScheme
)

val DarkCefrColors = CefrColors(
    beginner = CefrColorScheme(
        content = B.Emerald300,
        background = B.Emerald300.copy(alpha = 0.15f),
        border = B.Emerald400.copy(alpha = 0.3f)
    ),
    intermediate = CefrColorScheme(
        content = B.Blue300,
        background = B.Blue400.copy(alpha = 0.15f),
        border = B.Blue400.copy(alpha = 0.4f)
    ),
    advanced = CefrColorScheme(
        content = B.Purple300,
        background = B.Purple400.copy(alpha = 0.15f),
        border = B.Purple400.copy(alpha = 0.4f)
    )
)

val LightCefrColors = CefrColors(
    beginner = CefrColorScheme(
        content = B.Emerald700,
        background = B.Emerald50,
        border = B.Emerald300
    ),
    intermediate = CefrColorScheme(
        content = B.Blue800,
        background = B.Blue50,
        border = B.Blue200
    ),
    advanced = CefrColorScheme(
        content = B.Purple800,
        background = B.Purple50,
        border = B.Purple200
    )
)

val LocalCefrColors = staticCompositionLocalOf { LightCefrColors }

@Composable
@ReadOnlyComposable
fun getCefrColorScheme(level: CefrLevel): CefrColorScheme {
    val colors = LocalCefrColors.current
    return when (level) {
        CefrLevel.A1, CefrLevel.A2 -> colors.beginner
        CefrLevel.B1, CefrLevel.B2 -> colors.intermediate
        CefrLevel.C1, CefrLevel.C2 -> colors.advanced
    }
}

@Composable
@ReadOnlyComposable
fun getThemeColorForLevel(cefrLevel: CefrLevel): Color {
    return getCefrColorScheme(cefrLevel).content
}