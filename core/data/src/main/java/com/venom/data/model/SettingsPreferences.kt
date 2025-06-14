package com.venom.data.model

import com.venom.domain.model.AppTheme
import com.venom.domain.model.FontStyles
import com.venom.domain.model.PaletteStyle
import kotlinx.serialization.Serializable

@Serializable
data class SettingsPreferences(
    val appLanguage: LanguageItem = LANGUAGES_LIST[1],
    val nativeLanguage: LanguageItem = LANGUAGES_LIST[0],
    val targetLanguage: LanguageItem = LANGUAGES_LIST[1],
    val selectedProvider: TranslationProvider = TranslationProvider.GOOGLE,
    val speechRate: Float = 1.2f,

    val themePrefs: ThemePreference = ThemePreference(),
    val personalPrefs: PersonalPreference = PersonalPreference(),
    val splashPrefs: SplashPreferences = SplashPreferences(),

    val autoPronounciation: Boolean = true,

    )

@Serializable
data class ThemePreference(
    val appTheme: AppTheme = AppTheme.SYSTEM,
    val extractWallpaperColor: Boolean = false,
    val isAmoledBlack: Boolean = false,
    val colorStyle: PaletteStyle = PaletteStyle.Neutral,
    val fontFamily: FontStyles = FontStyles.Default,
    val primaryColor: ThemeColor = ThemeColor("Sky Blue", 0xFF00B8FF)
)

@Serializable
data class PersonalPreference(
    val maxStreakCount: Int = 0,
    val currentStreakCount: Int = 0,
    val lastLoginDate: Long = 0L,
    val isFirstLaunch: Boolean = true
)

@Serializable
data class SplashPreferences(
    val splashBackgroundColor: Long = 0xFF6200EE,
    val splashTextColor: Long = 0xFFFFFFFF,
    val splashAnimationDuration: Int = 1000
)
