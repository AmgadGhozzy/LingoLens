package com.venom.ui.theme.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.venom.ui.theme.BrandColors as B

@Immutable
data class SpellingColors(
    val mastery: Color,
    val masteryContainer: Color,

    val slotActive: Color,
    val slotFilled: Color,
    val slotEmpty: Color,

    val surfaceTile: Color,
    val streakEmpty: Color,

    val shadowPrimary: Color
)

val LightSpellingColors = SpellingColors(
    mastery = B.Amber500,
    masteryContainer = B.Amber100,
    slotActive = B.Blue50,
    slotFilled = B.White,
    slotEmpty = B.Slate100,
    surfaceTile = B.White,
    streakEmpty = B.Slate200,
    shadowPrimary = B.Blue500.copy(0.25f)
)

val DarkSpellingColors = SpellingColors(
    mastery = B.Amber400,
    masteryContainer = B.Amber900.copy(0.32f),
    slotActive = B.Blue900.copy(0.85f),
    slotFilled = B.Slate700,
    slotEmpty = B.Slate600,
    surfaceTile = B.Slate800,
    streakEmpty = B.Slate600,
    shadowPrimary = B.Black.copy(0.6f)
)
