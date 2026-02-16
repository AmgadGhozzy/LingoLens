package com.venom.ui.theme.tokens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.venom.ui.theme.BrandColors as B

@Immutable
data class DifficultyTheme(
    val text: Color,
    val background: Color,
    val border: Color
)

@Immutable
data class DifficultyColors(
    val easy: DifficultyTheme,
    val medium: DifficultyTheme,
    val hard: DifficultyTheme
)

val DarkDifficultyColors = DifficultyColors(
    easy = DifficultyTheme(
        text = B.Green300,
        background = B.Green400.copy(alpha = 0.15f),
        border = B.Green400.copy(alpha = 0.3f)
    ),

    medium = DifficultyTheme(
        text = B.Amber300,
        background = B.Amber400.copy(alpha = 0.14f),
        border = B.Amber500.copy(alpha = 0.35f)
    ),

    hard = DifficultyTheme(
        text = B.Red300,
        background = B.Red400.copy(alpha = 0.15f),
        border = B.Red400.copy(alpha = 0.4f)
    )
)

val LightDifficultyColors = DifficultyColors(
    easy = DifficultyTheme(
        text = B.Green700,
        background = B.Green100.copy(alpha = 0.45f),
        border = B.Green300
    ),
    medium = DifficultyTheme(
        text = B.Amber700,
        background = B.Amber100.copy(alpha = 0.45f),
        border = B.Amber300
    ),
    hard = DifficultyTheme(
        text = B.Red700,
        background = B.Red100.copy(alpha = 0.45f),
        border = B.Red300
    )
)

val LocalDifficultyColors = staticCompositionLocalOf { LightDifficultyColors }

@Composable
@ReadOnlyComposable
fun getDifficultyTheme(score: Int): DifficultyTheme {
    val colors = LocalDifficultyColors.current

    return when (score) {
        in 1..3 -> colors.easy
        in 4..7 -> colors.medium
        else -> colors.hard
    }
}