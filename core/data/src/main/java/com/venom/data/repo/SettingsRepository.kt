package com.venom.data.repo

import com.venom.data.model.PersonalPreference
import com.venom.data.model.SettingsPreferences
import com.venom.data.model.ThemePreference
import com.venom.domain.model.AppLanguage
import com.venom.domain.model.FontStyles
import com.venom.domain.model.PaletteStyle
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<SettingsPreferences>

    suspend fun updateSettings(update: SettingsPreferences.() -> SettingsPreferences)
    suspend fun updateThemePreferences(update: ThemePreference.() -> ThemePreference)
    suspend fun updatePersonalPreferences(update: PersonalPreference.() -> PersonalPreference)

    suspend fun setAppLanguage(language: AppLanguage)
    suspend fun setSpeechRate(rate: Float)
    suspend fun setNativeLanguage(language: AppLanguage)
    suspend fun setPrimaryColor(color: Int)
    suspend fun setPaletteStyle(style: PaletteStyle)
    suspend fun toggleAmoledBlack()
    suspend fun toggleWallpaperColor()
    suspend fun setFontFamily(style: FontStyles)
    suspend fun updateStreak(time: Long)
}