package com.venom.ui.theme.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.venom.ui.theme.BrandColors as B

@Immutable
data class GlassColors(
    val ultraThin: Color,
    val thin: Color,
    val regular: Color,
    val thick: Color,
    val ultraThick: Color,
    val border: Color,
    val borderStrong: Color,
    val shadow: Color,
    val shadowStrong: Color,
    val tintPrimary: Color,
    val tintSecondary: Color,
    val tintAccent: Color
)

val LightGlassColors = GlassColors(
    ultraThin = B.White.copy(alpha = 0.50f),
    thin = B.White.copy(alpha = 0.70f),
    regular = B.White.copy(alpha = 0.85f),
    thick = B.White.copy(alpha = 0.92f),
    ultraThick = B.White,

    border = B.Slate400.copy(alpha = 0.25f),
    borderStrong = B.Slate500.copy(alpha = 0.35f),

    shadow = B.Slate900.copy(alpha = 0.06f),
    shadowStrong = B.Slate900.copy(alpha = 0.12f),

    tintPrimary = B.Blue500.copy(alpha = 0.10f),
    tintSecondary = B.Purple500.copy(alpha = 0.08f),
    tintAccent = B.Cyan500.copy(alpha = 0.10f),
)

val DarkGlassColors = GlassColors(
    ultraThin = B.Slate800.copy(alpha = 0.60f),
    thin = B.Slate800.copy(alpha = 0.80f),
    regular = B.Slate800.copy(alpha = 0.90f),
    thick = B.Slate900.copy(alpha = 0.95f),
    ultraThick = B.Slate900,

    border = B.Slate400.copy(alpha = 0.15f),
    borderStrong = B.Slate300.copy(alpha = 0.25f),

    shadow = B.Black.copy(alpha = 0.25f),
    shadowStrong = B.Black.copy(alpha = 0.45f),

    tintPrimary = B.Blue400.copy(alpha = 0.15f),
    tintSecondary = B.Purple400.copy(alpha = 0.12f),
    tintAccent = B.Cyan400.copy(alpha = 0.15f),
)

val LocalGlassColors = staticCompositionLocalOf { LightGlassColors }