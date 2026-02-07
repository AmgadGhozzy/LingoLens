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
import androidx.compose.ui.unit.Dp
import androidx.core.view.WindowCompat
import com.venom.domain.model.AppTheme
import com.venom.domain.model.FontStyles
import com.venom.domain.model.PaletteStyle
import com.venom.ui.components.common.adp
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

@Composable
fun LingoLensTheme(
    primaryColor: Color = BrandColors.Blue500,
    isAmoledBlack: Boolean = false,
    materialYou: Boolean = false,
    appTheme: AppTheme = AppTheme.LIGHT,
    colorStyle: PaletteStyle = PaletteStyle.Vibrant,
    fontFamilyStyle: FontStyles = FontStyles.ALEXANDRIA,
    content: @Composable (() -> Unit)
) {
    val view = LocalView.current
    val fontFamily = safeLoadFontFamily(fontFamilyStyle)

    val isDark = when (appTheme) {
        AppTheme.DARK -> true
        AppTheme.LIGHT -> false
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }

    val keyColor = when {
        materialYou && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            primaryColor
        }

        materialYou && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 -> {
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

    var surfaceColor by remember { mutableStateOf(Color.Transparent) }

    val colorScheme = if (materialYou) {
        createDynamicColorScheme(
            keyColor = keyColor,
            style = colorStyle,
            isDark = isDark,
            isAmoledBlack = isAmoledBlack,
            contrastLevel = if (isDark) 1.2 else 1.0
        )
    } else {
        when {
            isDark && isAmoledBlack -> DarkColorScheme.toAmoledBlack()
            isDark -> DarkColorScheme
            else -> LightColorScheme
        }
    }

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

    surfaceColor = colorScheme.surfaceColorAtElevation(3.adp)
}

object SettingsSpacing {
    val cardPadding: Dp @Composable get() = 16.adp
    val itemSpacing: Dp @Composable get() = 12.adp
    val contentPadding: Dp @Composable get() = 20.adp
    val iconSize: Dp @Composable get() = 24.adp
    val badgeSize: Dp @Composable get() = 8.adp
    val colorPreviewSize: Dp @Composable get() = 24.adp
}