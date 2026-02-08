package com.venom.data.repo

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.venom.data.local.SettingsPreferencesSerializer
import com.venom.data.model.LanguageItem
import com.venom.data.model.OrbPreferences
import com.venom.data.model.PersonalPreference
import com.venom.data.model.SettingsPreferences
import com.venom.data.model.SplashPreferences
import com.venom.data.model.ThemeColor
import com.venom.data.model.ThemePreference
import com.venom.data.model.TranslationProvider
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

    override suspend fun updateSplashPreferences(update: SplashPreferences.() -> SplashPreferences) {
        updateSettings { copy(splashPrefs = update(splashPrefs)) }
    }

    override suspend fun updateOrbPreferences(update: OrbPreferences.() -> OrbPreferences) {
        updateSettings { copy(orbPrefs = update(orbPrefs)) }
    }

    override suspend fun setAppLanguage(language: LanguageItem) {
        updateSettings { copy(appLanguage = language) }
    }

    override suspend fun setNativeLanguage(language: LanguageItem) {
        updateSettings { copy(nativeLanguage = language) }
    }

    override suspend fun setTargetLanguage(language: LanguageItem) {
        updateSettings { copy(targetLanguage = language) }
    }

    override suspend fun setSelectedProvider(provider: TranslationProvider) {
        updateSettings { copy(selectedProvider = provider) }
    }

    override suspend fun setSpeechRate(rate: Float) {
        updateSettings { copy(speechRate = rate) }
    }

    override suspend fun setPrimaryColor(color: ThemeColor) {
        updateThemePreferences {
            copy(
                primaryColor = color,
                materialYou = false
            )
        }
    }

    override suspend fun toggleAmoledBlack() {
        updateThemePreferences { copy(isAmoledBlack = !isAmoledBlack) }
    }

    override suspend fun toggleWallpaperColor() {
        updateThemePreferences { copy(materialYou = !materialYou) }
    }

    override suspend fun setPaletteStyle(style: PaletteStyle) {
        updateThemePreferences { copy(colorStyle = style) }
    }

    override suspend fun setFontFamily(style: FontStyles) {
        updateThemePreferences { copy(fontFamily = style) }
    }

    // Orb preferences
    override suspend fun toggleOrbs() {
        updateOrbPreferences { copy(enabled = !enabled) }
    }

    override suspend fun toggleFloatingAnimation() {
        updateOrbPreferences { copy(enableFloatingAnimation = !enableFloatingAnimation) }
    }

    override suspend fun toggleScaleAnimation() {
        updateOrbPreferences { copy(enableScaleAnimation = !enableScaleAnimation) }
    }

    override suspend fun toggleAlphaAnimation() {
        updateOrbPreferences { copy(enableAlphaAnimation = !enableAlphaAnimation) }
    }

    override suspend fun setAnimationSpeed(speed: Float) {
        updateOrbPreferences { copy(animationSpeed = speed) }
    }

    // First launch state management
    override suspend fun isFirstLaunch(): Boolean {
        val prefs = context.settingsDataStore.data.first()
        return prefs.personalPrefs.isFirstLaunch
    }

    override suspend fun markFirstLaunchComplete() {
        updatePersonalPreferences {
            copy(isFirstLaunch = false)
        }
    }

    // Splash screen preferences
    override suspend fun setSplashBackgroundColor(color: Long) {
        updateSplashPreferences {
            copy(splashBackgroundColor = color)
        }
    }

    override suspend fun setSplashTextColor(color: Long) {
        updateSplashPreferences {
            copy(splashTextColor = color)
        }
    }

    override suspend fun setSplashAnimationDuration(duration: Int) {
        updateSplashPreferences {
            copy(splashAnimationDuration = duration)
        }
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