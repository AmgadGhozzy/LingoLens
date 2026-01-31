package com.venom.ui.theme.tokens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.venom.ui.theme.BrandColors as B

@Immutable
data class RelationColorScheme(
    val text: Color,
    val background: Color,
    val border: Color
)

@Immutable
data class PosColorScheme(
    val noun: Color,
    val verb: Color,
    val adjective: Color,
    val adverb: Color
)

@Immutable
data class RelationColors(
    val synonym: RelationColorScheme,
    val antonym: RelationColorScheme,
    val related: RelationColorScheme,
    val pos: PosColorScheme
)

val DarkRelationColors = RelationColors(
    synonym = RelationColorScheme(
        text = B.Cyan300,
        background = B.Cyan400.copy(alpha = 0.15f),
        border = B.Cyan400.copy(alpha = 0.35f)
    ),
    antonym = RelationColorScheme(
        text = B.Rose300,
        background = B.Rose400.copy(alpha = 0.15f),
        border = B.Rose400.copy(alpha = 0.3f)
    ),
    related = RelationColorScheme(
        text = B.Slate200,
        background = B.Slate700.copy(alpha = 0.2f),
        border = B.Slate600.copy(alpha = 0.3f)
    ),
    pos = PosColorScheme(
        noun = B.Blue400,
        verb = B.Emerald400,
        adjective = B.Amber400,
        adverb = B.Purple400
    )
)

val LightRelationColors = RelationColors(
    synonym = RelationColorScheme(
        text = B.Cyan700,
        background = B.Cyan50,
        border = B.Cyan300
    ),
    antonym = RelationColorScheme(
        text = B.Rose700,
        background = B.Rose50,
        border = B.Rose300
    ),
    related = RelationColorScheme(
        text = B.Slate700,
        background = B.Slate50,
        border = B.Slate300
    ),
    pos = PosColorScheme(
        noun = B.Blue600,
        verb = B.Emerald600,
        adjective = B.Amber600,
        adverb = B.Purple600
    )
)

@Immutable
data class TipCardColors(
    val background: Color,
    val border: Color,
    val accent: Color,
    val text: Color,
    val iconBackground: Color
)

val LightTipCardColors = TipCardColors(
    background = B.Amber50,
    border = B.Amber300,
    accent = B.Amber600,
    text = B.Amber700,
    iconBackground = B.Amber400.copy(alpha = 0.2f)
)

val DarkTipCardColors = TipCardColors(
    background = B.Amber400.copy(alpha = 0.12f),
    border = B.Amber500.copy(alpha = 0.25f),
    accent = B.Amber500,
    text = B.Amber600,
    iconBackground = B.Amber400.copy(alpha = 0.2f)
)

@Immutable
data class CollocationColors(
    val text: Color,
    val border: Color,
    val background: Color
)

val LightCollocationColors = CollocationColors(
    text = B.Indigo700,
    border = B.Indigo300,
    background = Color.Transparent
)

val DarkCollocationColors = CollocationColors(
    text = B.Indigo300,
    border = B.Indigo600.copy(alpha = 0.4f),
    background = Color.Transparent
)

@Immutable
data class FeatureColors(
    val relation: RelationColors,
    val tip: TipCardColors,
    val collocation: CollocationColors,
    val quiz: QuizColors,
    val spelling: SpellingColors,
    val quote: QuoteColors
)

val LightFeatureColors = FeatureColors(
    relation = LightRelationColors,
    tip = LightTipCardColors,
    collocation = LightCollocationColors,
    quiz = LightQuizColors,
    spelling = LightSpellingColors,
    quote = LightQuoteColors
)

val DarkFeatureColors = FeatureColors(
    relation = DarkRelationColors,
    tip = DarkTipCardColors,
    collocation = DarkCollocationColors,
    quiz = DarkQuizColors,
    spelling = DarkSpellingColors,
    quote = DarkQuoteColors
)

val LocalFeatureColors = staticCompositionLocalOf { LightFeatureColors }

@Composable
@ReadOnlyComposable
fun getPosColor(partOfSpeech: String): Color {
    val colors = LocalFeatureColors.current.relation.pos
    return when (partOfSpeech.lowercase()) {
        "noun", "n" -> colors.noun
        "verb", "v" -> colors.verb
        "adj", "adjective", "a" -> colors.adjective
        "adv", "adverb" -> colors.adverb
        else -> colors.noun // Default to noun color
    }
}