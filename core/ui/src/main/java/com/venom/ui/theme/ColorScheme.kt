package com.venom.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/** Material3 color schemes for light and dark themes with improved secondary colors. */
val LightColorScheme: ColorScheme = lightColorScheme(
    // Primary - Blue (main brand color)
    primary = BrandColors.Blue600,
    onPrimary = BrandColors.White,
    primaryContainer = BrandColors.Blue100,
    onPrimaryContainer = BrandColors.Blue900,

    // Secondary - Indigo (refined, professional accent)
    secondary = BrandColors.Indigo600,
    onSecondary = BrandColors.White,
    secondaryContainer = BrandColors.Indigo100,
    onSecondaryContainer = BrandColors.Indigo900,

    // Tertiary - Emerald (success, completion states)
    tertiary = BrandColors.Emerald600,
    onTertiary = BrandColors.White,
    tertiaryContainer = BrandColors.Emerald100,
    onTertiaryContainer = BrandColors.Emerald900,

    // Error - Rose (soft, modern error color)
    error = BrandColors.Rose600,
    onError = BrandColors.White,
    errorContainer = BrandColors.Rose100,
    onErrorContainer = BrandColors.Rose900,

    // Background & Surface
    background = BrandColors.Slate50,
    onBackground = BrandColors.Slate900,
    surface = BrandColors.White,
    onSurface = BrandColors.Slate900,
    surfaceVariant = BrandColors.Slate100,
    onSurfaceVariant = BrandColors.Slate700,

    // Surface containers (elevation levels)
    surfaceContainerLowest = BrandColors.White,
    surfaceContainerLow = BrandColors.Slate50,
    surfaceContainer = BrandColors.Slate100,
    surfaceContainerHigh = BrandColors.Slate200,
    surfaceContainerHighest = BrandColors.Slate300,

    surfaceBright = BrandColors.White,
    surfaceDim = BrandColors.Slate100,

    // Outlines - FIXED for better contrast
    outline = BrandColors.Slate400,
    outlineVariant = BrandColors.Slate200,

    // Inverse colors
    inverseSurface = BrandColors.Slate800,
    inverseOnSurface = BrandColors.Slate50,
    inversePrimary = BrandColors.Blue300,

    scrim = BrandColors.Black.copy(alpha = 0.32f),
    surfaceTint = BrandColors.Blue600
)

val DarkColorScheme: ColorScheme = darkColorScheme(
    // Primary - Blue (vibrant on dark)
    primary = BrandColors.Blue400,
    onPrimary = BrandColors.Blue900,
    primaryContainer = BrandColors.Blue800,
    onPrimaryContainer = BrandColors.Blue100,

    // Secondary - Indigo (refined purple-blue)
    secondary = BrandColors.Indigo400,
    onSecondary = BrandColors.Indigo900,
    secondaryContainer = BrandColors.Indigo800,
    onSecondaryContainer = BrandColors.Indigo100,

    // Tertiary - Emerald (success states)
    tertiary = BrandColors.Emerald400,
    onTertiary = BrandColors.Emerald900,
    tertiaryContainer = BrandColors.Emerald800,
    onTertiaryContainer = BrandColors.Emerald100,

    // Error - Rose (soft red)
    error = BrandColors.Rose400,
    onError = BrandColors.Rose900,
    errorContainer = BrandColors.Rose800,
    onErrorContainer = BrandColors.Rose100,

    // Background & Surface
    background = BrandColors.Slate950,
    onBackground = BrandColors.Slate100,
    surface = BrandColors.Slate900,
    onSurface = BrandColors.Slate50,
    surfaceVariant = BrandColors.Slate800,
    onSurfaceVariant = BrandColors.Slate300,

    // Surface containers (elevation levels)
    surfaceContainerLowest = BrandColors.Black,
    surfaceContainerLow = BrandColors.Slate950,
    surfaceContainer = BrandColors.Slate900,
    surfaceContainerHigh = BrandColors.Slate800,
    surfaceContainerHighest = BrandColors.Slate700,

    surfaceBright = BrandColors.Slate700,
    surfaceDim = BrandColors.Slate950,

    // Outlines - FIXED: better visibility on dark backgrounds
    outline = BrandColors.Slate600,
    outlineVariant = BrandColors.Slate700,

    // Inverse colors
    inverseSurface = BrandColors.Slate100,
    inverseOnSurface = BrandColors.Slate900,
    inversePrimary = BrandColors.Blue600,

    scrim = BrandColors.Black.copy(alpha = 0.6f),
    surfaceTint = BrandColors.Blue400
)

/** Convert scheme to AMOLED black variant. */
fun ColorScheme.toAmoledBlack(): ColorScheme = copy(
    background = BrandColors.Black,
    surface = BrandColors.Black,
    surfaceContainer = Color(0xFF0A0D11),
    surfaceContainerLow = BrandColors.Black,
    surfaceContainerLowest = BrandColors.Black,
    surfaceDim = BrandColors.Black,
    surfaceContainerHigh = BrandColors.Slate900,
    surfaceContainerHighest = BrandColors.Slate800,
    outline = BrandColors.Slate700,
    outlineVariant = BrandColors.Slate800
)
