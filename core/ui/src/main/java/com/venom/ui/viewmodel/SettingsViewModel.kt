package com.venom.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.domain.repo.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsState(
    val isDarkMode: Boolean = false,
    val isAutoTheme: Boolean = false,
    val selectedLanguage: String = "English",
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
                settingsRepository.selectedLanguage
            ) { isDarkMode, isAutoTheme, speechRate, language ->
                SettingsState(
                    isDarkMode = isDarkMode,
                    isAutoTheme = isAutoTheme,
                    speechRate = speechRate,
                    selectedLanguage = language
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun updateDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            try {
                Log.d("SettingsViewModel", "Updating dark mode to: $enabled")
                settingsRepository.setDarkMode(enabled)
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Error updating dark mode", e)
            }
        }
    }

    fun updateAutoTheme(enabled: Boolean) {
        viewModelScope.launch {
            try {
                Log.d("SettingsViewModel", "Updating auto theme to: $enabled")
                settingsRepository.setAutoTheme(enabled)
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Error updating auto theme", e)
            }
        }
    }

    fun updateSpeechRate(rate: Float) {
        viewModelScope.launch {
            settingsRepository.setSpeechRate(rate)
        }
    }

    fun updateLanguage(language: String) {
        viewModelScope.launch {
            settingsRepository.setLanguage(language)
        }
    }
}