package com.venom.ui.theme.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.venom.ui.theme.BrandColors as B

@Immutable
data class QuoteColors(
    val accent: Color,
    val gold: Color,
    val pink: Color,
    val tagBackground: Color,
    val tagText: Color,
    val gradient: Brush
)

val LightQuoteColors = QuoteColors(
    accent = B.Indigo600,
    gold = B.Amber500,
    pink = B.Pink500,
    tagBackground = B.Blue50.copy(0.6f),
    tagText = B.Blue600,
    gradient = Brush.linearGradient(
        colors = listOf(
            B.Purple700.copy(0.7f),
            B.Blue500.copy(0.7f),
            B.Indigo600.copy(0.7f)
        )
    )
)

val DarkQuoteColors = QuoteColors(
    accent = B.Indigo400,
    gold = B.Amber400,
    pink = B.Pink400,
    tagBackground = B.Slate700.copy(0.7f),
    tagText = B.Blue200,
    gradient = Brush.linearGradient(
        colors = listOf(
            B.Purple500.copy(0.7f),
            B.Blue500.copy(0.7f),
            B.Indigo600.copy(0.7f)
        )
    )
)