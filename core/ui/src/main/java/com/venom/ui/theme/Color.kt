package com.venom.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.kyant.m3color.dynamiccolor.MaterialDynamicColors
import com.kyant.m3color.hct.Hct
import com.kyant.m3color.scheme.SchemeContent
import com.kyant.m3color.scheme.SchemeExpressive
import com.kyant.m3color.scheme.SchemeFidelity
import com.kyant.m3color.scheme.SchemeFruitSalad
import com.kyant.m3color.scheme.SchemeMonochrome
import com.kyant.m3color.scheme.SchemeNeutral
import com.kyant.m3color.scheme.SchemeRainbow
import com.kyant.m3color.scheme.SchemeTonalSpot
import com.kyant.m3color.scheme.SchemeVibrant
import com.venom.domain.model.PaletteStyle

/**
 * Creates a dynamic Material 3 ColorScheme based on a key color and style
 *
 * @param keyColor Base color for generating the color scheme
 * @param style Palette style (Neutral, Vibrant, Expressive, etc.)
 * @param isDark Whether to generate a dark or light color scheme
 * @param isAmoledBlack Enable pure black backgrounds for OLED displays
 * @param contrastLevel Contrast adjustment (1.0 = normal, higher = more contrast)
 * @return Material 3 ColorScheme
 */
@Composable
fun createDynamicColorScheme(
    keyColor: Color,
    style: PaletteStyle = PaletteStyle.Neutral,
    isDark: Boolean = isSystemInDarkTheme(),
    isAmoledBlack: Boolean = false,
    contrastLevel: Double = 1.0
): ColorScheme {
    val hct = Hct.fromInt(keyColor.toArgb())
    val colors = MaterialDynamicColors()

    // Select scheme based on style
    val scheme = when (style) {
        PaletteStyle.Neutral -> SchemeNeutral(hct, isDark, contrastLevel)
        PaletteStyle.Vibrant -> SchemeVibrant(hct, isDark, contrastLevel)
        PaletteStyle.Expressive -> SchemeExpressive(hct, isDark, contrastLevel)
        PaletteStyle.Rainbow -> SchemeRainbow(hct, isDark, contrastLevel)
        PaletteStyle.FruitSalad -> SchemeFruitSalad(hct, isDark, contrastLevel)
        PaletteStyle.Monochrome -> SchemeMonochrome(hct, isDark, contrastLevel)
        PaletteStyle.Fidelity -> SchemeFidelity(hct, isDark, contrastLevel)
        PaletteStyle.Content -> SchemeContent(hct, isDark, contrastLevel)
        PaletteStyle.TonalSpot -> SchemeTonalSpot(hct, isDark, contrastLevel)
    }

    return if (isDark.not()) {
        lightColorScheme(
            background = Color(colors.background().getArgb(scheme)),
            error = Color(colors.error().getArgb(scheme)),
            errorContainer = Color(colors.errorContainer().getArgb(scheme)),
            inverseOnSurface = Color(colors.inverseOnSurface().getArgb(scheme)),
            inversePrimary = Color(colors.inversePrimary().getArgb(scheme)),
            inverseSurface = Color(colors.inverseSurface().getArgb(scheme)),
            onBackground = Color(colors.onBackground().getArgb(scheme)),
            onError = Color(colors.onError().getArgb(scheme)),
            onErrorContainer = Color(colors.onErrorContainer().getArgb(scheme)),
            onPrimary = Color(colors.onPrimary().getArgb(scheme)),
            onPrimaryContainer = Color(colors.onPrimaryContainer().getArgb(scheme)),
            onSecondary = Color(colors.onSecondary().getArgb(scheme)),
            onSecondaryContainer = Color(colors.onSecondaryContainer().getArgb(scheme)),
            onSurface = Color(colors.onSurface().getArgb(scheme)),
            onSurfaceVariant = Color(colors.onSurfaceVariant().getArgb(scheme)),
            onTertiary = Color(colors.onTertiary().getArgb(scheme)),
            onTertiaryContainer = Color(colors.onTertiaryContainer().getArgb(scheme)),
            outline = Color(colors.outline().getArgb(scheme)),
            outlineVariant = Color(colors.outlineVariant().getArgb(scheme)),
            primary = Color(colors.primary().getArgb(scheme)),
            primaryContainer = Color(colors.primaryContainer().getArgb(scheme)),
            scrim = Color(colors.scrim().getArgb(scheme)),
            secondary = Color(colors.secondary().getArgb(scheme)),
            secondaryContainer = Color(colors.secondaryContainer().getArgb(scheme)),
            surface = Color(colors.surface().getArgb(scheme)),
            surfaceTint = Color(colors.surfaceTint().getArgb(scheme)),
            surfaceVariant = Color(colors.surfaceVariant().getArgb(scheme)),
            tertiary = Color(colors.tertiary().getArgb(scheme)),
            tertiaryContainer = Color(colors.tertiaryContainer().getArgb(scheme))
        )
    } else {
        darkColorScheme(
            // Apply AMOLED black when enabled
            background = if (isAmoledBlack) Color.Black else Color(colors.background().getArgb(scheme)),
            error = Color(colors.error().getArgb(scheme)),
            errorContainer = Color(colors.errorContainer().getArgb(scheme)),
            inverseOnSurface = Color(colors.inverseOnSurface().getArgb(scheme)),
            inversePrimary = Color(colors.inversePrimary().getArgb(scheme)),
            inverseSurface = Color(colors.inverseSurface().getArgb(scheme)),
            onBackground = Color(colors.onBackground().getArgb(scheme)),
            onError = Color(colors.onError().getArgb(scheme)),
            onErrorContainer = Color(colors.onErrorContainer().getArgb(scheme)),
            onPrimary = Color(colors.onPrimary().getArgb(scheme)),
            onPrimaryContainer = Color(colors.onPrimaryContainer().getArgb(scheme)),
            onSecondary = Color(colors.onSecondary().getArgb(scheme)),
            onSecondaryContainer = Color(colors.onSecondaryContainer().getArgb(scheme)),
            onSurface = Color(colors.onSurface().getArgb(scheme)),
            onSurfaceVariant = Color(colors.onSurfaceVariant().getArgb(scheme)),
            onTertiary = Color(colors.onTertiary().getArgb(scheme)),
            onTertiaryContainer = Color(colors.onTertiaryContainer().getArgb(scheme)),
            outline = Color(colors.outline().getArgb(scheme)),
            outlineVariant = Color(colors.outlineVariant().getArgb(scheme)),
            primary = Color(colors.primary().getArgb(scheme)),
            primaryContainer = Color(colors.primaryContainer().getArgb(scheme)),
            scrim = Color(colors.scrim().getArgb(scheme)),
            secondary = Color(colors.secondary().getArgb(scheme)),
            secondaryContainer = Color(colors.secondaryContainer().getArgb(scheme)),
            // Apply AMOLED black when enabled
            surface = if (isAmoledBlack) Color.Black else Color(colors.surface().getArgb(scheme)),
            surfaceTint = Color(colors.surfaceTint().getArgb(scheme)),
            surfaceVariant = Color(colors.surfaceVariant().getArgb(scheme)),
            tertiary = Color(colors.tertiary().getArgb(scheme)),
            tertiaryContainer = Color(colors.tertiaryContainer().getArgb(scheme))
        )
    }
}