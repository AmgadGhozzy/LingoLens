package com.venom.ui.screen.langselector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.data.model.LANGUAGES_LIST
import com.venom.data.model.LanguageItem
import com.venom.data.repo.OfflineTranslationOperations
import com.venom.data.repo.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LanguageSelectorState(
    val sourceLang: LanguageItem = LANGUAGES_LIST[0],
    val targetLang: LanguageItem = LANGUAGES_LIST[1],
    val searchQuery: String = "",
    val isSelectingSourceLanguage: Boolean = true,
    val filteredLanguages: List<LanguageItem> = LANGUAGES_LIST
)

@HiltViewModel
class LangSelectorViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val offlineTranslationOperations: OfflineTranslationOperations
) : ViewModel() {

    private val _state = MutableStateFlow(LanguageSelectorState())
    val state = _state.asStateFlow()

    private var isInternalUpdate = false

    init {
        loadSavedLanguages()
        loadOfflineLanguageModelsStatus()
    }

    private fun loadSavedLanguages() {
        viewModelScope.launch {
            settingsRepository.settings.collectLatest { preferences ->
                if (!isInternalUpdate) {
                    val nativeLang = LANGUAGES_LIST.find { it.code == preferences.nativeLanguage.code } ?: LANGUAGES_LIST[0]
                    val targetLang = LANGUAGES_LIST.find { it.code == preferences.targetLanguage.code } ?: LANGUAGES_LIST[1]

                    _state.update { current ->
                        current.copy(sourceLang = nativeLang, targetLang = targetLang)
                    }
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { current ->
            current.copy(
                searchQuery = query,
                filteredLanguages = filterLanguages(query)
            )
        }
    }

    fun setSelectingSourceLanguage(isSelecting: Boolean) {
        _state.update { current ->
            current.copy(isSelectingSourceLanguage = isSelecting)
        }
    }

    fun onLanguageSelected(language: LanguageItem) {
        viewModelScope.launch {
            isInternalUpdate = true
            try {
                _state.update { current ->
                    if (current.isSelectingSourceLanguage) {
                        if (language == current.targetLang) {
                            settingsRepository.setNativeLanguage(current.targetLang)
                            settingsRepository.setTargetLanguage(current.sourceLang)
                            current.copy(
                                sourceLang = current.targetLang,
                                targetLang = current.sourceLang,
                                isSelectingSourceLanguage = false
                            )
                        } else {
                            settingsRepository.setNativeLanguage(language)
                            current.copy(
                                sourceLang = language,
                                isSelectingSourceLanguage = false
                            )
                        }
                    } else {
                        if (language == current.sourceLang) {
                            settingsRepository.setNativeLanguage(current.targetLang)
                            settingsRepository.setTargetLanguage(current.sourceLang)
                            current.copy(
                                sourceLang = current.targetLang,
                                targetLang = current.sourceLang,
                                isSelectingSourceLanguage = true
                            )
                        } else {
                            settingsRepository.setTargetLanguage(language)
                            current.copy(
                                targetLang = language,
                                isSelectingSourceLanguage = true
                            )
                        }
                    }
                }
            } finally {
                isInternalUpdate = false
            }
        }
    }

    fun swapLanguages() {
        viewModelScope.launch {
            isInternalUpdate = true
            try {
                _state.update { current ->
                    settingsRepository.setNativeLanguage(current.targetLang)
                    settingsRepository.setTargetLanguage(current.sourceLang)
                    current.copy(
                        sourceLang = current.targetLang,
                        targetLang = current.sourceLang
                    )
                }
            } finally {
                isInternalUpdate = false
            }
        }
    }

    private fun loadOfflineLanguageModelsStatus() {
        viewModelScope.launch {
            val allMlKitLanguages = offlineTranslationOperations.getAllModels().toSet()
            val downloadedMlKitLanguages = offlineTranslationOperations.getDownloadedModels()

            _state.update { current ->
                val updatedLanguages = LANGUAGES_LIST.map { lang ->
                    val isMlKitSupported = allMlKitLanguages.contains(lang.code)
                    if (isMlKitSupported) {
                        lang.copy(
                            isDownloaded = downloadedMlKitLanguages.contains(lang.code),
                            downloadSizeMb = getModelSize(lang.code)
                        )
                    } else {
                        lang
                    }
                }
                current.copy(
                    filteredLanguages = filterLanguages(current.searchQuery, updatedLanguages),
                    sourceLang = updateLanguageItemDetails(current.sourceLang, updatedLanguages),
                    targetLang = updateLanguageItemDetails(current.targetLang, updatedLanguages)
                )
            }
        }
    }

    private fun getModelSize(langCode: String): Float {
        return when (langCode) {
            "en" -> 30.2f
            "es" -> 28.5f
            "fr" -> 29.1f
            "de" -> 31.4f
            "it" -> 27.8f
            "pt" -> 28.3f
            "ru" -> 33.7f
            "ja" -> 42.1f
            "ko" -> 39.6f
            "zh" -> 44.8f
            "ar" -> 35.2f
            "hi" -> 31.9f
            "th" -> 36.4f
            "vi" -> 29.7f
            "tr" -> 28.9f
            "pl" -> 30.6f
            "nl" -> 27.4f
            "sv" -> 26.8f
            "no" -> 26.1f
            "da" -> 25.9f
            "fi" -> 28.7f
            "cs" -> 29.3f
            "hu" -> 30.8f
            "ro" -> 27.6f
            "bg" -> 28.4f
            "hr" -> 27.9f
            "sk" -> 28.1f
            "sl" -> 26.5f
            "et" -> 25.7f
            "lv" -> 26.3f
            "lt" -> 27.2f
            "uk" -> 32.4f
            "be" -> 29.8f
            "mk" -> 28.6f
            "sq" -> 26.9f
            "sr" -> 28.2f
            "bs" -> 27.1f
            "mt" -> 25.4f
            "ga" -> 26.7f
            "cy" -> 26.0f
            "is" -> 25.8f
            "he" -> 29.4f
            "fa" -> 33.1f
            "ur" -> 30.5f
            "bn" -> 34.6f
            "ta" -> 35.8f
            "te" -> 33.9f
            "ml" -> 32.7f
            "kn" -> 31.5f
            "gu" -> 30.9f
            "pa" -> 29.6f
            "mr" -> 28.8f
            "ne" -> 27.5f
            "si" -> 26.4f
            "my" -> 37.2f
            "km" -> 34.3f
            "lo" -> 31.8f
            "ka" -> 28.0f
            "hy" -> 27.3f
            "az" -> 26.6f
            "kk" -> 30.3f
            "ky" -> 28.5f
            "uz" -> 27.7f
            "tg" -> 26.8f
            "mn" -> 29.2f
            "id" -> 26.9f
            "ms" -> 26.2f
            "tl" -> 27.4f
            "sw" -> 25.6f
            "am" -> 28.3f
            "zu" -> 24.8f
            "xh" -> 24.5f
            "af" -> 25.1f
            "eu" -> 26.5f
            "ca" -> 27.8f
            "gl" -> 26.7f
            else -> 28.0f
        }
    }

    private fun updateLanguageItemDetails(lang: LanguageItem, allLangs: List<LanguageItem>): LanguageItem {
        return allLangs.find { it.code == lang.code } ?: lang
    }

    fun downloadLanguage(language: LanguageItem) {
        viewModelScope.launch {
            updateLanguageItemState(language.code, isDownloading = true)
            offlineTranslationOperations.downloadLanguageModel(language.code)
                .onSuccess {
                    updateLanguageItemState(language.code, isDownloading = false, isDownloaded = true)
                }
                .onFailure {
                    updateLanguageItemState(language.code, isDownloading = false)
                }
        }
    }

    fun deleteLanguage(language: LanguageItem) {
        viewModelScope.launch {
            offlineTranslationOperations.deleteLanguageModel(language.code)
                .onSuccess {
                    updateLanguageItemState(language.code, isDownloaded = false)
                }
        }
    }

    private fun updateLanguageItemState(
        langCode: String,
        isDownloading: Boolean? = null,
        isDownloaded: Boolean? = null
    ) {
        _state.update { current ->
            val updatedList = current.filteredLanguages.map { lang ->
                if (lang.code == langCode) {
                    lang.copy(
                        isDownloading = isDownloading ?: lang.isDownloading,
                        isDownloaded = isDownloaded ?: lang.isDownloaded
                    )
                } else lang
            }
            current.copy(
                filteredLanguages = updatedList,
                sourceLang = updateLanguageItemDetails(current.sourceLang, updatedList),
                targetLang = updateLanguageItemDetails(current.targetLang, updatedList)
            )
        }
    }

    private fun filterLanguages(
        query: String,
        languages: List<LanguageItem> = _state.value.filteredLanguages
    ): List<LanguageItem> {
        return if (query.isEmpty()) {
            languages
        } else {
            val lowerQuery = query.lowercase().trim()
            languages.filter { lang ->
                lang.code.contains(lowerQuery, true) ||
                        lang.englishName.contains(lowerQuery, true) ||
                        lang.nativeName.contains(lowerQuery, true)
            }
        }
    }
}