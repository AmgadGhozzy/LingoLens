package com.venom.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

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

    scrim = BrandColors.Black,
    surfaceTint = BrandColors.Blue600
)

val DarkColorScheme: ColorScheme = darkColorScheme(
    primary = BrandColors.Blue400,
    onPrimary = BrandColors.Blue950,
    primaryContainer = BrandColors.Blue900,
    onPrimaryContainer = BrandColors.Blue200,

    secondary = BrandColors.Indigo300,
    onSecondary = BrandColors.Indigo950,
    secondaryContainer = BrandColors.Indigo950,
    onSecondaryContainer = BrandColors.Indigo200,

    tertiary = BrandColors.Purple300,
    onTertiary = BrandColors.Purple950,
    tertiaryContainer = BrandColors.Purple950,
    onTertiaryContainer = BrandColors.Purple200,

    error = BrandColors.Rose400,
    onError = BrandColors.Rose950,
    errorContainer = BrandColors.Rose950,
    onErrorContainer = BrandColors.Rose200,

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

    scrim = BrandColors.Black,
    surfaceTint = BrandColors.Blue400
)

fun ColorScheme.toAmoledBlack(): ColorScheme = copy(
    background = BrandColors.Black,
    surface = BrandColors.Black,
    surfaceContainer = BrandColors.Slate925,
    surfaceContainerLow = BrandColors.Black,
    surfaceContainerLowest = BrandColors.Black,
    surfaceDim = BrandColors.Black,
    surfaceContainerHigh = BrandColors.Slate900,
    surfaceContainerHighest = BrandColors.Slate800,
    outline = BrandColors.Slate700,
    outlineVariant = BrandColors.Slate800
)
