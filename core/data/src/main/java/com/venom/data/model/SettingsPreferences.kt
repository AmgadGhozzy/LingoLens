package com.venom.data.model

import com.venom.domain.model.AppLanguage
import com.venom.domain.model.AppTheme
import com.venom.domain.model.FontStyles
import com.venom.domain.model.PaletteStyle
import com.venom.domain.model.ThemeColor
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class SettingsPreferences(
    val appLanguage: AppLanguage = AppLanguage.ENGLISH,
    val nativeLanguage: AppLanguage = AppLanguage.ARABIC,
    val speechRate: Float = 1.2f,

    val themePrefs: ThemePreference = ThemePreference(),
    val personalPrefs: PersonalPreference = PersonalPreference(),

    val autoPronounciation: Boolean = true,
)

@Serializable
data class ThemePreference(
    val appTheme: AppTheme = AppTheme.SYSTEM,
    val extractWallpaperColor: Boolean = false,
    val isAmoledBlack: Boolean = true,
    val colorStyle: PaletteStyle = PaletteStyle.Neutral,
    val fontFamily: FontStyles = FontStyles.Default,
    @Contextual val primaryColor: ThemeColor = ThemeColor("Sky Blue", 0xFF00B8FF)
)
@Serializable
data class PersonalPreference(
    val maxStreakCount: Int = 0,
    val currentStreakCount: Int = 0,
    val lastLoginDate: Long = 0L
)
