package com.venom.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val LightColorScheme: ColorScheme = lightColorScheme(
    primary = BrandColors.Blue600,
    onPrimary = BrandColors.White,
    primaryContainer = BrandColors.Blue100,
    onPrimaryContainer = BrandColors.Blue900,

    secondary = BrandColors.Indigo600,
    onSecondary = BrandColors.White,
    secondaryContainer = BrandColors.Indigo100,
    onSecondaryContainer = BrandColors.Indigo900,

    tertiary = BrandColors.Purple600,
    onTertiary = BrandColors.White,
    tertiaryContainer = BrandColors.Purple100,
    onTertiaryContainer = BrandColors.Purple900,

    error = BrandColors.Rose600,
    onError = BrandColors.White,
    errorContainer = BrandColors.Rose100,
    onErrorContainer = BrandColors.Rose900,

    background = BrandColors.Slate50,
    onBackground = BrandColors.Slate900,
    surface = BrandColors.White,
    onSurface = BrandColors.Slate900,
    surfaceVariant = BrandColors.Slate100,
    onSurfaceVariant = BrandColors.Slate700,

    surfaceContainerLowest = BrandColors.White,
    surfaceContainerLow = BrandColors.White,
    surfaceContainer = BrandColors.Slate25,
    surfaceContainerHigh = BrandColors.Slate50,
    surfaceContainerHighest = BrandColors.Slate200,

    surfaceBright = BrandColors.White,
    surfaceDim = BrandColors.Slate100,

    outline = BrandColors.Slate400,
    outlineVariant = BrandColors.Slate200,

    inverseSurface = BrandColors.Slate800,
    inverseOnSurface = BrandColors.Slate50,
    inversePrimary = BrandColors.Blue300,

    scrim = BrandColors.Black.copy(alpha = 0.32f),
    surfaceTint = BrandColors.Blue600
)

val DarkColorScheme: ColorScheme = darkColorScheme(
    primary = BrandColors.Blue500,
    onPrimary = BrandColors.Blue900,
    primaryContainer = BrandColors.Blue800,
    onPrimaryContainer = BrandColors.Blue100,

    secondary = BrandColors.Indigo400,
    onSecondary = BrandColors.Indigo900,
    secondaryContainer = BrandColors.Indigo800,
    onSecondaryContainer = BrandColors.Indigo100,

    tertiary = BrandColors.Purple400,
    onTertiary = BrandColors.Purple900,
    tertiaryContainer = BrandColors.Purple800,
    onTertiaryContainer = BrandColors.Purple100,

    error = BrandColors.Rose400,
    onError = BrandColors.Rose900,
    errorContainer = BrandColors.Rose800,
    onErrorContainer = BrandColors.Rose100,

    background = BrandColors.Slate950,
    onBackground = BrandColors.Slate100,
    surface = BrandColors.Slate900,
    onSurface = BrandColors.Slate50,
    surfaceVariant = BrandColors.Slate800,
    onSurfaceVariant = BrandColors.Slate300,

    surfaceContainerLowest = BrandColors.Black,
    surfaceContainerLow = BrandColors.Slate950,
    surfaceContainer = BrandColors.Slate900,
    surfaceContainerHigh = BrandColors.Slate850,
    surfaceContainerHighest = BrandColors.Slate800,

    surfaceBright = BrandColors.Slate700,
    surfaceDim = BrandColors.Slate950,

    outline = BrandColors.Slate600,
    outlineVariant = BrandColors.Slate700,

    inverseSurface = BrandColors.Slate100,
    inverseOnSurface = BrandColors.Slate900,
    inversePrimary = BrandColors.Blue600,

    scrim = BrandColors.Black.copy(alpha = 0.6f),
    surfaceTint = BrandColors.Blue400
)

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