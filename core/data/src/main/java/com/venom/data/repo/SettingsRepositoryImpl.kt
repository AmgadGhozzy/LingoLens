package com.venom.data.repo

import com.venom.data.local.SettingsDataStore
import com.venom.domain.repo.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : SettingsRepository {

    override val isDarkMode: Flow<Boolean> =
        settingsDataStore.settingsFlow.map { it.isDarkMode }.distinctUntilChanged()

    override val isAutoTheme: Flow<Boolean> =
        settingsDataStore.settingsFlow.map { it.isAutoTheme }.distinctUntilChanged()


    override val speechRate: Flow<Float> =
        settingsDataStore.settingsFlow.map { it.speechRate }.distinctUntilChanged()

    override val selectedLanguage: Flow<String> =
        settingsDataStore.settingsFlow.map { it.selectedLanguage }.distinctUntilChanged()

    override suspend fun setDarkMode(enabled: Boolean) {
        settingsDataStore.updateDarkMode(enabled)
    }

    override suspend fun setAutoTheme(enabled: Boolean) {
        settingsDataStore.updateAutoTheme(enabled)
    }

    override suspend fun setSpeechRate(rate: Float) {
        settingsDataStore.updateSpeechRate(rate)
    }

    override suspend fun setLanguage(language: String) {
        settingsDataStore.updateLanguage(language)
    }
}