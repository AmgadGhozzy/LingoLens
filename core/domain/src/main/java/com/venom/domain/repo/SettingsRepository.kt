package com.venom.domain.repo

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val isDarkMode: Flow<Boolean>
    val isAutoTheme: Flow<Boolean>
    val speechRate: Flow<Float>
    val selectedLanguage: Flow<String>

    suspend fun setDarkMode(enabled: Boolean)
    suspend fun setAutoTheme(enabled: Boolean)
    suspend fun setSpeechRate(rate: Float)
    suspend fun setLanguage(language: String)
}