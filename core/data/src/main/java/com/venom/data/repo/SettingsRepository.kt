package com.venom.data.repo

import com.venom.data.model.PersonalPreference
import com.venom.data.model.SettingsPreferences
import com.venom.data.model.ThemePreference
import com.venom.domain.model.AppLanguage
import com.venom.domain.model.ColorStyle
import com.venom.domain.model.FontFamilyStyle
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<SettingsPreferences>

    suspend fun updateSettings(update: SettingsPreferences.() -> SettingsPreferences)
    suspend fun updateThemePreferences(update: ThemePreference.() -> ThemePreference)
    suspend fun updatePersonalPreferences(update: PersonalPreference.() -> PersonalPreference)

    suspend fun setSpeechRate(rate: Float)
    suspend fun setAppLanguage(language: AppLanguage)
    suspend fun setUserLanguage(language: AppLanguage)
    suspend fun setColor(color: Int)
    suspend fun setColorStyle(style: ColorStyle)
    suspend fun setFontFamily(style: FontFamilyStyle)
    suspend fun toggleAmoledBlack()
    suspend fun toggleWallpaperColor()
    suspend fun toggleRandomColor()
    suspend fun toggleColorfulBackground()
    suspend fun updateStreak(time: Long)
}