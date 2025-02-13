package com.venom.data.repo

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.venom.data.local.SettingsPreferencesSerializer
import com.venom.data.model.PersonalPreference
import com.venom.data.model.SettingsPreferences
import com.venom.data.model.ThemePreference
import com.venom.domain.model.AppLanguage
import com.venom.domain.model.FontStyles
import com.venom.domain.model.PaletteStyle
import com.venom.utils.SETTING_FILE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val context: Context
) : SettingsRepository {

    private val Context.settingsDataStore: DataStore<SettingsPreferences> by dataStore(
        fileName = SETTING_FILE,
        serializer = SettingsPreferencesSerializer
    )

    override val settings: Flow<SettingsPreferences> = context.settingsDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(SettingsPreferences())
            } else {
                throw exception
            }
        }

    override suspend fun updateSettings(update: SettingsPreferences.() -> SettingsPreferences) {
        context.settingsDataStore.updateData { prefs ->
            update(prefs).copy()
        }
    }

    override suspend fun updateThemePreferences(update: ThemePreference.() -> ThemePreference) {
        updateSettings { copy(themePrefs = update(themePrefs)) }
    }

    override suspend fun updatePersonalPreferences(update: PersonalPreference.() -> PersonalPreference) {
        updateSettings { copy(personalPrefs = update(personalPrefs)) }
    }

    override suspend fun setAppLanguage(language: AppLanguage) {
        updateSettings { copy(appLanguage = language) }
    }

    override suspend fun setSpeechRate(rate: Float) {
        updateSettings { copy(speechRate = rate) }
    }

    override suspend fun setNativeLanguage(language: AppLanguage) {
        updateSettings { copy(nativeLanguage = language) }
    }

    override suspend fun setPrimaryColor(color: Int) {
        updateThemePreferences {
            copy(
                primaryColor = color,
                extractWallpaperColor = false
            )
        }
    }

    override suspend fun toggleAmoledBlack() {
        updateThemePreferences { copy(isAmoledBlack = !isAmoledBlack) }
    }

    override suspend fun toggleWallpaperColor() {
        updateThemePreferences { copy(extractWallpaperColor = !extractWallpaperColor) }
    }

    override suspend fun setPaletteStyle(style: PaletteStyle) {
        updateThemePreferences { copy(colorStyle = style) }
    }

    override suspend fun setFontFamily(style: FontStyles) {
        updateThemePreferences { copy(fontFamily = style) }
    }

    override suspend fun updateStreak(time: Long) {
        val dayFormatter = SimpleDateFormat("dd MM yyyy", Locale.ROOT)
        val currentPrefs = context.settingsDataStore.data.first()

        val today = dayFormatter.format(time)
        val yesterday = dayFormatter.format(System.currentTimeMillis() - (1000 * 60 * 60 * 24))
        val lastLoginDate = dayFormatter.format(currentPrefs.personalPrefs.lastLoginDate)

        val (currentStreak, maxStreak) = when (lastLoginDate) {
            today -> return
            yesterday -> listOf(
                currentPrefs.personalPrefs.currentStreakCount + 1,
                maxOf(
                    currentPrefs.personalPrefs.maxStreakCount,
                    currentPrefs.personalPrefs.currentStreakCount + 1
                )
            )

            else -> listOf(
                1,
                maxOf(
                    currentPrefs.personalPrefs.maxStreakCount,
                    currentPrefs.personalPrefs.currentStreakCount
                )
            )
        }

        updatePersonalPreferences {
            copy(
                lastLoginDate = time,
                maxStreakCount = maxStreak,
                currentStreakCount = currentStreak
            )
        }
    }
}