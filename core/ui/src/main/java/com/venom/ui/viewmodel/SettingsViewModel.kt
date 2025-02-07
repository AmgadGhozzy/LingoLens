package com.venom.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.domain.repo.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

data class SettingsState(
    val isDarkMode: Boolean = false,
    val isAutoTheme: Boolean = true,
    val selectedLanguage: String = Locale.getDefault().language,
    val speechRate: Float = 1f,
    val autoPronounciation: Boolean = true,
    val autoTranslate: Boolean = true,
    val autoCopy: Boolean = true,
    val realTimeOcr: Boolean = true
)


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsState())
    val uiState: StateFlow<SettingsState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                settingsRepository.isDarkMode,
                settingsRepository.isAutoTheme,
                settingsRepository.speechRate,
                settingsRepository.selectedLanguage,
                ::createSettingsState
            ).catch { e ->
                Log.e("SettingsViewModel", "Error collecting settings", e)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun createSettingsState(
        isDarkMode: Boolean,
        isAutoTheme: Boolean,
        speechRate: Float,
        language: String
    ): SettingsState = SettingsState(
        isDarkMode = isDarkMode,
        isAutoTheme = isAutoTheme,
        speechRate = speechRate,
        selectedLanguage = language
    )

    fun updateSetting(update: suspend (SettingsRepository) -> Unit) {
        viewModelScope.launch {
            try {
                update(settingsRepository)
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Error updating setting", e)
            }
        }
    }

    fun updateDarkMode(enabled: Boolean) = updateSetting { it.setDarkMode(enabled) }
    fun updateAutoTheme(enabled: Boolean) = updateSetting { it.setAutoTheme(enabled) }
    fun updateSpeechRate(rate: Float) = updateSetting { it.setSpeechRate(rate) }
    fun updateLanguage(language: String) = updateSetting { it.setLanguage(language) }

    companion object {
        fun setLocale(context: Context, languageCode: String): Context {
            val locale = when (languageCode) {
                "" -> Locale.getDefault()
                else -> Locale(languageCode)
            }

            val config = context.resources.configuration
            val localeList = LocaleListCompat.create(locale)
            ConfigurationCompat.setLocales(config, localeList)

            return context.createConfigurationContext(config).also {
                Locale.setDefault(locale)
            }
        }
    }
}