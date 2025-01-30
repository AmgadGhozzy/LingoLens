package com.venom.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.venom.data.model.SettingsPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsDataStore @Inject constructor(
    private val context: Context
) {
    private val Context.dataStore: DataStore<SettingsPreferences> by dataStore(
        fileName = "settings_prefs.pb",
        serializer = SettingsSerializer
    )

    val settingsFlow: Flow<SettingsPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(SettingsPreferences())
            } else {
                throw exception
            }
        }

    suspend fun updateDarkMode(isDarkMode: Boolean) {
        context.dataStore.updateData { preferences ->
            preferences.copy(
                isDarkMode = isDarkMode,
                lastUpdated = System.currentTimeMillis()
            )
        }
    }

    suspend fun updateAutoTheme(isAutoTheme: Boolean) {
        context.dataStore.updateData { preferences ->
            preferences.copy(
                isAutoTheme = isAutoTheme,
                lastUpdated = System.currentTimeMillis()
            )
        }
    }

    suspend fun updateSpeechRate(speechRate: Float) {
        context.dataStore.updateData { preferences ->
            preferences.copy(
                speechRate = speechRate,
                lastUpdated = System.currentTimeMillis()
            )
        }
    }

    suspend fun updateLanguage(language: String) {
        context.dataStore.updateData { preferences ->
            preferences.copy(
                selectedLanguage = language,
                lastUpdated = System.currentTimeMillis()
            )
        }
    }
}
