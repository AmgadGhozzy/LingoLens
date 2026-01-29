package com.venom.ui.theme.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Combined feature color schemes for easy access.
 *
 * Usage:
 * MaterialTheme.lingoLens.feature.<feature>.<color>
 */

@Immutable
data class FeatureColors(
    val quiz: QuizColors,
    val spelling: SpellingColors,
    val quote: QuoteColors
)

val LightFeatureColors = FeatureColors(
    quiz = LightQuizColors,
    spelling = LightSpellingColors,
    quote = LightQuoteColors
)

val DarkFeatureColors = FeatureColors(
    quiz = DarkQuizColors,
    spelling = DarkSpellingColors,
    quote = DarkQuoteColors
)

val LocalFeatureColors = staticCompositionLocalOf { LightFeatureColors }