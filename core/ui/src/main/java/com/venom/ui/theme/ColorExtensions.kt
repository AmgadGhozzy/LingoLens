package com.venom.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import com.venom.ui.theme.tokens.CefrColors
import com.venom.ui.theme.tokens.FeatureColors
import com.venom.ui.theme.tokens.GlassColors
import com.venom.ui.theme.tokens.LocalCefrColors
import com.venom.ui.theme.tokens.LocalFeatureColors
import com.venom.ui.theme.tokens.LocalGlassColors
import com.venom.ui.theme.tokens.LocalSemanticColors
import com.venom.ui.theme.tokens.LocalStickyColors
import com.venom.ui.theme.tokens.SemanticColors
import com.venom.ui.theme.tokens.StickyColors

/** Aggregated LingoLens custom colors. */
@Immutable
data class LingoLensColors(
    val semantic: SemanticColors,
    val glass: GlassColors,
    val feature: FeatureColors,
    val cefr: CefrColors,
    val sticky: StickyColors
)

/** Access LingoLens colors via MaterialTheme.lingoLens. */
val MaterialTheme.lingoLens: LingoLensColors
    @Composable
    @ReadOnlyComposable
    get() = LingoLensColors(
        semantic = LocalSemanticColors.current,
        glass = LocalGlassColors.current,
        feature = LocalFeatureColors.current,
        cefr = LocalCefrColors.current,
        sticky = LocalStickyColors.current
    )