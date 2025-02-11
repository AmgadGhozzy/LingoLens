package com.venom.data.model

import com.venom.domain.model.AppLanguage
import com.venom.domain.model.AppTheme
import com.venom.domain.model.ColorStyle
import com.venom.domain.model.FontFamilyStyle
import com.venom.domain.model.UserLanguage
import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable
data class SettingsPreferences(

    val themePrefs: ThemePreference = ThemePreference(),

    val speechRate: Float = 1.0f,

    // Language Settings
    val appLanguage: AppLanguage = AppLanguage.FOLLOW_SYSTEM,
    val userLanguage: UserLanguage = UserLanguage.entries.firstOrNull {
        it.isoCode == Locale.getDefault().language
    } ?: UserLanguage.NOT_SPECIFIED,

    // Personal Settings
    val personalPrefs: PersonalPreference = PersonalPreference(),

    // Feature Settings
    val autoPronounciation: Boolean = true,
    val autoTranslate: Boolean = true,
    val autoCopy: Boolean = true,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Serializable
data class ThemePreference(
    val appTheme: AppTheme = AppTheme.FOLLOW_SYSTEM,
    val extractWallpaperColor: Boolean = false,
    val isAmoledBlack: Boolean = false,
    val randomColor: Boolean = false,
    val colorfulBackground: Boolean = false,
    val colorStyle: ColorStyle = ColorStyle.TonalSpot,
    val fontFamily: FontFamilyStyle = FontFamilyStyle.SANS_SERIF,
    val primaryColor: Int = 0xFF00C853.toInt()
)

@Serializable
data class PersonalPreference(
    val maxStreakCount: Int = 0,
    val currentStreakCount: Int = 0,
    val lastLoginDate: Long = 0L
)
