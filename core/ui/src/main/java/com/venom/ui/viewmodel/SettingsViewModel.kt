package com.venom.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.data.model.SettingsPreferences
import com.venom.data.model.ThemePreference
import com.venom.data.repo.SettingsRepository
import com.venom.domain.model.AppLanguage
import com.venom.domain.model.AppTheme
import com.venom.domain.model.FontStyles
import com.venom.domain.model.PaletteStyle
import com.venom.domain.model.ThemeColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val themePrefs: ThemePreference = ThemePreference(),
    val appLanguage: AppLanguage = AppLanguage.ENGLISH,
    val nativeLanguage: AppLanguage = AppLanguage.ARABIC,
    val selectedLanguage: AppLanguage = AppLanguage.ENGLISH,
    val speechRate: Float = 1f,
    val autoPronounciation: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = settingsRepository.settings
        .map { it.toUiState() }
        .catch { e ->
            emit(SettingsUiState(error = e.message))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsUiState()
        )

    private fun SettingsPreferences.toUiState() = SettingsUiState(
        speechRate = speechRate,
        appLanguage = appLanguage,
        nativeLanguage = nativeLanguage,
        selectedLanguage = appLanguage,
        autoPronounciation = autoPronounciation,
        themePrefs = themePrefs
    )

    private fun updateSetting(action: suspend SettingsRepository.() -> Unit) {
        viewModelScope.launch {
            try {
                settingsRepository.action()
            } catch (_: Exception) {
            }
        }
    }

    fun updateLanguage(language: AppLanguage) = updateSetting {
        updateSettings { copy(appLanguage = language) }
    }

    fun updateNativeLanguage(language: AppLanguage) = updateSetting {
        updateSettings { copy(nativeLanguage = language) }
    }

    fun updateSpeechRate(rate: Float) = updateSetting {
        updateSettings { copy(speechRate = rate) }
    }

    fun updateAutoPronounciation(enabled: Boolean) = updateSetting {
        updateSettings { copy(autoPronounciation = enabled) }
    }

    fun updateAppTheme(theme: AppTheme) = updateSetting {
        updateSettings { copy(themePrefs = themePrefs.copy(appTheme = theme)) }
    }

    fun setPrimaryColor(color: ThemeColor) = updateSetting { this.setPrimaryColor(color) }
    fun setColorStyle(style: PaletteStyle) = updateSetting { setPaletteStyle(style) }
    fun setFontFamily(style: FontStyles) = updateSetting { setFontFamily(style) }
    fun toggleAmoledBlack() = updateSetting { toggleAmoledBlack() }
    fun toggleWallpaperColor() = updateSetting { toggleWallpaperColor() }

    // App interaction
    fun checkForUpdates() {
        // Implement app update check logic
    }

    fun openPrivacyPolicy() {
        // Implement privacy policy opening logic
    }

    fun openRateApp() {
        // Implement app rating logic
    }
}