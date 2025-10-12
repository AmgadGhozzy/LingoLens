package com.venom.ui.viewmodel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.data.model.LANGUAGES_LIST
import com.venom.data.model.LanguageItem
import com.venom.data.model.TranslationProvider
import com.venom.data.repo.SettingsRepository
import com.venom.data.repo.TranslationRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class CardTranslationState(
    val expandedCardId: String? = null,
    val currentTranslation: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val targetLanguage: LanguageItem = LANGUAGES_LIST[1],
    val selectedProvider: TranslationProvider = TranslationProvider.GOOGLE
)

@HiltViewModel
class ExpandableCardViewModel @Inject constructor(
    private val repository: TranslationRepositoryImpl,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CardTranslationState())
    val state = _state.asStateFlow()

    private var translationJob: Job? = null

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                _state.update {
                    it.copy(
                        selectedProvider = settings.selectedProvider,
                        targetLanguage = settings.targetLanguage
                    )
                }
            }
        }
    }

    fun toggleCard(cardId: String, text: String) {
        val currentId = _state.value.expandedCardId

        if (currentId == cardId) {
            // Collapse current card
            collapse()
        } else {
            // Expand new card and translate
            _state.update {
                it.copy(
                    expandedCardId = cardId,
                    currentTranslation = "",
                    error = null
                )
            }
            translate(text)
        }
    }

    fun collapse() {
        translationJob?.cancel()
        _state.update {
            it.copy(
                expandedCardId = null,
                currentTranslation = "",
                isLoading = false,
                error = null
            )
        }
    }

    fun retry(text: String) {
        translate(text)
    }

    private fun translate(text: String) {
        translationJob?.cancel()

        _state.update { it.copy(isLoading = true, error = null) }

        translationJob = viewModelScope.launch {
            repository.translate(
                sourceText = text,
                targetLang = _state.value.targetLanguage.code,
                providerId = _state.value.selectedProvider.id,
                forceRefresh = false
            )
                .onSuccess { response ->
                    _state.update {
                        it.copy(
                            currentTranslation = response.translatedText,
                            isLoading = false
                        )
                    }
                }
                .onFailure { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Translation failed"
                        )
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        translationJob?.cancel()
    }
}