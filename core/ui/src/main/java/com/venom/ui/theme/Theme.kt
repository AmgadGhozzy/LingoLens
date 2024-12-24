package com.venom.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
val DarkColorScheme = ColorScheme(
    // Background Colors (Fundamental Surface Layers)
    background = Color(0xFF121214),       // Primary background color for entire app screens
    surface = Color(0xFF121214),          // Base surface color for components
    surfaceVariant = Color(0xFF252427),   // Variant surface for subtle differentiations


    // Surface Containers (Hierarchical Depth Levels)
    surfaceContainerLowest = Color(0xFF2F3134),    // Lowest level container (near-black)

    surfaceContainerLow = Color(0xFF252427),       // Low-elevation container surfaces
    surfaceContainer = Color(0xFF28292C), // Container for grouping related elements

    surfaceContainerHigh = Color(0xFF222224),      // Higher elevation container surfaces
    surfaceContainerHighest = Color(0xFF121214),   // Highest level container surfaces

    surfaceBright = Color(0xff3a3939),             // Bright variant for highlighted surfaces
    surfaceDim = Color(0xff141313),                // Dimmed surface for subtle backgrounds
    surfaceTint = Color(0xFFC9CACB),               // Tint overlay for surface variations

    // Primary Color Palette
    primary = Color(0xFFD1D2D3),              // Primary brand color
    primaryContainer = Color(0xFF28292C),     // Container for primary elements
    onPrimary = Color(0xff313030),            // Text/icon color on primary background
    onPrimaryContainer = Color(0xffa09d9d),   // Text color within primary container

    // Secondary Color Palette
    secondary = Color(0xFF38332A),            // Secondary accent color
    secondaryContainer = Color(0xFF38332A),   // Container for secondary elements
    onSecondary = Color(0xFFDBBC49),          // Text/icon color on secondary background
    onSecondaryContainer = Color(0xffe3dfdf), // Text color within secondary container

    // Tertiary Color Palette
    tertiary = Color(0xffcac5c5),             // Tertiary accent color
    tertiaryContainer = Color(0xff111010),    // Container for tertiary elements
    onTertiary = Color(0xFF878688),           // Text/icon color on tertiary background
    onTertiaryContainer = Color(0xffa19d9d),  // Text color within tertiary container

    // Text and Content Colors
    onSurface = Color(0xFFD1D2D3),            // Primary text color on surfaces
    onSurfaceVariant = Color(0xff5f5e5e),     // Secondary/hint text on surface variants
    onBackground = Color(0xFFD1D2D3),         // Text color on background

    // Outline and Divider Colors
    outline = Color(0xFF323234),              // Soft divider/border color
    outlineVariant = Color(0xff4d4b4b),       // Subtle variant for additional separation

    // Error and Feedback Colors
    error = Color(0xffffb4ab),                // Error state color
    errorContainer = Color(0xff93000a),       // Container for error states
    onError = Color(0xff690005),              // Text color on error backgrounds
    onErrorContainer = Color(0xffffdad6),     // Text color within error containers

    // Inverse Color Variants (for contrast and accessibility)
    inverseOnSurface = Color(0xff313030),     // Inverse text on surface
    inversePrimary = Color(0xff5f5e5e),       // Inverse of primary color
    inverseSurface = Color(0xffe5e2e1),       // Inverse surface color

    // Utility Colors
    scrim = Color(0xff000000)                 // Overlay for dimming surfaces
)
val LightColorScheme = ColorScheme(
    // Background Colors (Fundamental Surface Layers)
    background = Color(0xfffaf9fc),           // Soft, light background for entire app screens
    surface = Color(0xfffaf9fc),              // Base surface color for components
    surfaceVariant = Color(0xffe3e2e5),       // Subtle surface variation for differentiation
    surfaceContainer = Color(0xffeeedf0),     // Container for grouping related elements

    // Surface Containers (Hierarchical Depth Levels)
    surfaceContainerLow = Color(0xfff4f3f6),        // Low-elevation container surfaces
    surfaceContainerLowest = Color(0xffffffff),     // Lowest level container (pure white)
    surfaceContainerHigh = Color(0xffe9e8eb),       // Higher elevation container surfaces
    surfaceContainerHighest = Color(0xffe3e2e5),    // Highest level container surfaces
    surfaceBright = Color(0xfffaf9fc),              // Bright surface color
    surfaceDim = Color(0xffdad9dd),                 // Dimmed surface for subtle backgrounds
    surfaceTint = Color(0xff25619b),                // Tint overlay for surface variations

    // Primary Color Palette
    primary = Color(0xff25619b),              // Primary brand color (blue)
    primaryContainer = Color(0xffd2e4ff),     // Container for primary elements
    onPrimary = Color(0xffffffff),            // Text/icon color on primary background
    onPrimaryContainer = Color(0xff001c37),   // Text color within primary container

    // Secondary Color Palette
    secondary = Color(0xff555f6e),            // Secondary accent color (muted blue-gray)
    secondaryContainer = Color(0xffd9e3f5),   // Container for secondary elements
    onSecondary = Color(0xffffffff),          // Text/icon color on secondary background
    onSecondaryContainer = Color(0xff121c29), // Text color within secondary container

    // Tertiary Color Palette
    tertiary = Color(0xff6a5775),             // Tertiary accent color (soft purple)
    tertiaryContainer = Color(0xfff3dafd),    // Container for tertiary elements
    onTertiary = Color(0xffffffff),           // Text/icon color on tertiary background
    onTertiaryContainer = Color(0xff25152e),  // Text color within tertiary container

    // Text and Content Colors
    onSurface = Color(0xff1a1c1e),            // Primary text color on surfaces
    onSurfaceVariant = Color(0xff464749),     // Secondary/hint text on surface variants
    onBackground = Color(0xff1a1c1e),         // Text color on background

    // Outline and Divider Colors
    outline = Color(0xff76777a),              // Soft divider/border color
    outlineVariant = Color(0xffb9c0ca),       // Subtle variant for additional separation

    // Error and Feedback Colors
    error = Color(0xffba1a1a),                // Error state color
    errorContainer = Color(0xffffdad6),       // Container for error states
    onError = Color(0xffffffff),              // Text color on error backgrounds
    onErrorContainer = Color(0xff410002),     // Text color within error containers

    // Inverse Color Variants (for contrast and accessibility)
    inverseOnSurface = Color(0xfff1f0f3),     // Inverse text on surface
    inversePrimary = Color(0xffa1c9ff),       // Inverse of primary color
    inverseSurface = Color(0xff2f3033),       // Inverse surface color

    // Utility Colors
    scrim = Color(0xff000000)                 // Overlay for dimming surfaces
)
@Composable
fun LingoLensTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}