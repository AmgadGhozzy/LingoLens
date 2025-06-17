package com.venom.ui.theme

import android.app.Activity
import android.app.WallpaperManager
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.*
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
    primaryColor: Color = Color.Unspecified,
    isAmoledBlack: Boolean = false,
    materialYou: Boolean = false,
    appTheme: AppTheme = AppTheme.SYSTEM,
    colorStyle: PaletteStyle = PaletteStyle.Neutral,
    fontFamilyStyle: FontStyles = FontStyles.SANS_SERIF,
    content: @Composable (() -> Unit)
) {
    val view = LocalView.current
    val fontFamily = remember(fontFamilyStyle) { fontFamilyStyle.toFontFamily() }
    val isLight = when (appTheme) {
        AppTheme.DARK -> false
        AppTheme.LIGHT -> true
        AppTheme.SYSTEM -> !isSystemInDarkTheme()
    }

    // Extract Material You color if enabled and supported
    val keyColor = remember(materialYou, primaryColor) {
        if (materialYou && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            try {
                WallpaperManager.getInstance(view.context)
                    .getWallpaperColors(WallpaperManager.FLAG_SYSTEM)
                    ?.primaryColor
                    ?.toArgb()
                    ?.let { Color(it) }
            } catch (_: Exception) {
                null
            }
        } else null
    } ?: primaryColor

    val colorScheme = createDynamicColorScheme(
        isDark = !isLight,
        isAmoledBlack = isAmoledBlack,
        keyColor = keyColor,
        style = colorStyle,
    )

    val surfaceColor = colorScheme.surfaceColorAtElevation(3.dp)

    // Handle window insets and colors
    if (!view.isInEditMode) {
        DisposableEffect(view, isLight, surfaceColor) {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)

            window.navigationBarColor = surfaceColor.toArgb()
            WindowCompat.setDecorFitsSystemWindows(window, false)
            controller.isAppearanceLightStatusBars = isLight
            controller.isAppearanceLightNavigationBars = isLight

            onDispose { }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = getTypography(fontFamily),
        content = content
    )
}

object SettingsSpacing {
    val cardPadding = 16.dp
    val itemSpacing = 12.dp
    val contentPadding = 20.dp
    val iconSize = 24.dp
    val badgeSize = 8.dp
    val colorPreviewSize = 24.dp
}
