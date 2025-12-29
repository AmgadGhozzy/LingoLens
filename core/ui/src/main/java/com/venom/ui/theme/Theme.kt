package com.venom.ui.theme

import android.app.Activity
import android.app.WallpaperManager
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
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

@Composable
fun LingoLensTheme(
    primaryColor: Color = ThemeColors.GlassPrimary,
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
    val isLight = when (appTheme) {
        AppTheme.DARK -> false
        AppTheme.LIGHT -> true
        AppTheme.SYSTEM -> !isSystemInDarkTheme()
    }

    // Extract color from wallpaper or system when Material You is enabled
    val keyColor = when {
        materialYou && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> { primaryColor }
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
    val colorScheme = createDynamicColorScheme(
        isDark = !isLight,
        isAmoledBlack = isAmoledBlack,
        keyColor = keyColor,
        style = colorStyle,
        contrastLevel = 0.0
    )

    // Handle window insets and system bars
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)

            window.navigationBarColor = surfaceColor.toArgb()
            WindowCompat.setDecorFitsSystemWindows(window, false)
            controller.isAppearanceLightStatusBars = isLight
            controller.isAppearanceLightNavigationBars = isLight
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = getTypography(fontFamily),
        content = content
    )

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
