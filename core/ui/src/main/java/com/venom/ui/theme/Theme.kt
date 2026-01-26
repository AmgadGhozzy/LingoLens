package com.venom.ui.theme

import android.app.Activity
import android.app.WallpaperManager
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.venom.domain.model.AppTheme
import com.venom.domain.model.FontStyles
import com.venom.domain.model.PaletteStyle
import com.venom.ui.theme.tokens.DarkCefrColors
import com.venom.ui.theme.tokens.DarkDifficultyColors
import com.venom.ui.theme.tokens.DarkFeatureColors
import com.venom.ui.theme.tokens.DarkGlassColors
import com.venom.ui.theme.tokens.DarkSemanticColors
import com.venom.ui.theme.tokens.DarkStickyColors
import com.venom.ui.theme.tokens.LightCefrColors
import com.venom.ui.theme.tokens.LightDifficultyColors
import com.venom.ui.theme.tokens.LightFeatureColors
import com.venom.ui.theme.tokens.LightGlassColors
import com.venom.ui.theme.tokens.LightSemanticColors
import com.venom.ui.theme.tokens.LightStickyColors
import com.venom.ui.theme.tokens.LocalCefrColors
import com.venom.ui.theme.tokens.LocalDifficultyColors
import com.venom.ui.theme.tokens.LocalFeatureColors
import com.venom.ui.theme.tokens.LocalGlassColors
import com.venom.ui.theme.tokens.LocalSemanticColors
import com.venom.ui.theme.tokens.LocalStickyColors

/**
 * LingoLens Application Theme
 *
 * Unified theme wrapper that provides:
 * - Material 3 ColorScheme (via MaterialTheme.colorScheme)
 * - Semantic Colors (via MaterialTheme.lingoLens.semantic)
 * - Glass Colors (via MaterialTheme.lingoLens.glass)
 * - Feature Colors (via MaterialTheme.lingoLens.feature)
 * - CEFR Colors (via MaterialTheme.lingoLens.cefr)
 *
 * @param primaryColor Base color for dynamic color generation (Material You)
 * @param isAmoledBlack Enable pure black backgrounds for OLED displays
 * @param materialYou Enable dynamic color extraction from wallpaper
 * @param appTheme Theme mode (LIGHT, DARK, SYSTEM)
 * @param colorStyle Palette style for dynamic color generation
 * @param fontFamilyStyle Typography font family
 */
@Composable
fun LingoLensTheme(
    primaryColor: Color = BrandColors.Blue500,
    isAmoledBlack: Boolean = false,
    materialYou: Boolean = false,
    appTheme: AppTheme = AppTheme.DARK,
    colorStyle: PaletteStyle = PaletteStyle.Vibrant,
    fontFamilyStyle: FontStyles = FontStyles.ALEXANDRIA,
    content: @Composable (() -> Unit)
) {
    val view = LocalView.current
    val fontFamily = remember(fontFamilyStyle) { fontFamilyStyle.toFontFamily() }

    // Determine light/dark mode
    val isDark = when (appTheme) {
        AppTheme.DARK -> true
        AppTheme.LIGHT -> false
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }

    // Extract color from wallpaper or system when Material You is enabled
    val keyColor = when {
        materialYou && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            primaryColor
        }

        materialYou && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 -> {
            // Android 8.1+ with wallpaper color extraction
            val extractedColor = remember {
                try {
                    WallpaperManager.getInstance(view.context)
                        .getWallpaperColors(WallpaperManager.FLAG_SYSTEM)
                        ?.primaryColor
                        ?.toArgb()
                } catch (_: Exception) {
                    null
                }
            }
            if (extractedColor != null) Color(extractedColor) else primaryColor
        }

        else -> primaryColor
    }

    // Track surface color for window decorations
    var surfaceColor by remember { mutableStateOf(Color.Transparent) }

    // Generate color scheme
    // When Material You is enabled, use dynamic colors; otherwise use our static schemes
    val colorScheme = if (materialYou) {
        createDynamicColorScheme(
            keyColor = keyColor,
            style = colorStyle,
            isDark = isDark,
            isAmoledBlack = isAmoledBlack,
            contrastLevel = if (isDark) 1.2 else 1.0  // Higher contrast for dark mode
        )
    } else {
        when {
            isDark && isAmoledBlack -> DarkColorScheme.toAmoledBlack()
            isDark -> DarkColorScheme
            else -> LightColorScheme
        }
    }

    // Handle window insets and system bars
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)

            window.navigationBarColor = surfaceColor.toArgb()
            WindowCompat.setDecorFitsSystemWindows(window, false)
            controller.isAppearanceLightStatusBars = !isDark
            controller.isAppearanceLightNavigationBars = !isDark
        }
    }


    // Provide all custom colors via CompositionLocal
    val semanticColors = if (isDark) DarkSemanticColors else LightSemanticColors
    val glassColors = if (isDark) DarkGlassColors else LightGlassColors
    val featureColors = if (isDark) DarkFeatureColors else LightFeatureColors
    val cefrColors = if (isDark) DarkCefrColors else LightCefrColors
    val stickyColors = if (isDark) DarkStickyColors else LightStickyColors
    val difficultyColors = if (isDark) DarkDifficultyColors else LightDifficultyColors

    CompositionLocalProvider(
        LocalSemanticColors provides semanticColors,
        LocalGlassColors provides glassColors,
        LocalFeatureColors provides featureColors,
        LocalCefrColors provides cefrColors,
        LocalStickyColors provides stickyColors,
        LocalDifficultyColors provides difficultyColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = getTypography(fontFamily),
            content = content
        )
    }

    surfaceColor = colorScheme.surfaceColorAtElevation(3.dp)
}

object SettingsSpacing {
    val cardPadding = 16.dp
    val itemSpacing = 12.dp
    val contentPadding = 20.dp
    val iconSize = 24.dp
    val badgeSize = 8.dp
    val colorPreviewSize = 24.dp
}