package com.venom.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object QuoteColors {
    // Primary Colors
    private val IndigoPrimary = Color(0xFF4F46E5)
    private val IndigoSecondary = Color(0xFF6366F1)
    private val PurplePrimary = Color(0xFF7C3AED)
    private val PurpleSecondary = Color(0xFF8B5CF6)

    // Surface Colors
    private val SurfacePrimaryLight = Color(0xFFFFFFFF)
    private val SurfaceSecondaryLight = Color(0xFFF8FAFC)
    private val SurfacePrimaryDark = Color(0xFF1F2937)
    private val SurfaceSecondaryDark = Color(0xFF374151)

    // Text Colors
    private val TextPrimaryLight = Color(0xFF111827)
    private val TextSecondaryLight = Color(0xFF6B7280)
    private val TextTertiaryLight = Color(0xFF9CA3AF)

    private val TextPrimaryDark = Color(0xFFF9FAFB)
    private val TextSecondaryDark = Color(0xFF9CA3AF)
    private val TextTertiaryDark = Color(0xFF6B7280)

    // Accent Colors
    private val AccentBlueLight = Color(0xFF3B82F6)
    private val AccentBlueDark = Color(0xFF60A5FA)

    private val AccentGoldLight = Color(0xFFF59E0B)
    private val AccentGoldDark = Color(0xFFFBBF24)

    private val AccentPinkLight = Color(0xFFEC4899)
    private val AccentPinkDark = Color(0xFFF472B6)

    // Status Colors
    private val SuccessLight = Color(0xFF10B981)
    private val SuccessDark = Color(0xFF34D399)

    private val ErrorLight = Color(0xFFEF4444)
    private val ErrorDark = Color(0xFFF87171)

    // Border Colors
    private val BorderLightMode = Color(0xFFE0E7FF)
    private val BorderDarkMode = Color(0xFF6B7280)

    // Tag Colors
    private val TagBackgroundLight = Color(0x63EFF6FF)
    private val TagBackgroundDark = Color(0x63374151)

    private val TagTextLight = Color(0xFF3B82F6)
    private val TagTextDark = Color(0xFF93C5FD)

    // Gradients
    private val DailyQuoteGradientLight = Brush.Companion.linearGradient(
        colors = listOf(
            Color(0xFF7C3AED),
            Color(0xFF3B82F6),
            Color(0xFF4F46E5)
        )
    )

    private val DailyQuoteGradientDark = Brush.Companion.linearGradient(
        colors = listOf(
            Color(0xFF6D28D9),
            Color(0xFF2563EB),
            Color(0xFF4338CA)
        )
    )

    // Dynamic color getters that use isSystemInDarkTheme
    @Composable
    fun primary() = if (isSystemInDarkTheme()) IndigoSecondary else IndigoPrimary

    @Composable
    fun secondary() = if (isSystemInDarkTheme()) PurpleSecondary else PurplePrimary

    @Composable
    fun surfacePrimary() = if (isSystemInDarkTheme()) SurfacePrimaryDark else SurfacePrimaryLight

    @Composable
    fun surfaceSecondary() =
        if (isSystemInDarkTheme()) SurfaceSecondaryDark else SurfaceSecondaryLight

    @Composable
    fun textPrimary() = if (isSystemInDarkTheme()) TextPrimaryDark else TextPrimaryLight

    @Composable
    fun textSecondary() = if (isSystemInDarkTheme()) TextSecondaryDark else TextSecondaryLight

    @Composable
    fun textTertiary() = if (isSystemInDarkTheme()) TextTertiaryDark else TextTertiaryLight

    @Composable
    fun accentBlue() = if (isSystemInDarkTheme()) AccentBlueDark else AccentBlueLight

    @Composable
    fun accentGold() = if (isSystemInDarkTheme()) AccentGoldDark else AccentGoldLight

    @Composable
    fun accentPink() = if (isSystemInDarkTheme()) AccentPinkDark else AccentPinkLight

    @Composable
    fun success() = if (isSystemInDarkTheme()) SuccessDark else SuccessLight

    @Composable
    fun error() = if (isSystemInDarkTheme()) ErrorDark else ErrorLight

    @Composable
    fun border() = if (isSystemInDarkTheme()) BorderDarkMode else BorderLightMode

    @Composable
    fun tagBackground() = if (isSystemInDarkTheme()) TagBackgroundDark else TagBackgroundLight

    @Composable
    fun tagText() = if (isSystemInDarkTheme()) TagTextDark else TagTextLight

    @Composable
    fun dailyQuoteGradient() =
        if (isSystemInDarkTheme()) DailyQuoteGradientDark else DailyQuoteGradientLight
}