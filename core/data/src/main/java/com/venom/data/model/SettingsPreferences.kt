
package com.venom.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class SettingsPreferences(
    val isDarkMode: Boolean = false,
    val isAutoTheme: Boolean = true,
    val speechRate: Float = 1.0f,
    val selectedLanguage: String = "English",
    @Transient
    val lastUpdated: Long = System.currentTimeMillis()
)
