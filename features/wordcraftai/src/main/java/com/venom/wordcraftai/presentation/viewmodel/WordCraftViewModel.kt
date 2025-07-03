package com.venom.wordcraftai.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.wordcraftai.data.model.SupportedLanguage
import com.venom.wordcraftai.domain.model.Language
import com.venom.wordcraftai.domain.model.LearningExample
import com.venom.wordcraftai.domain.model.LearningSession
import com.venom.wordcraftai.domain.repository.WordCraftRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WordCraftUiState(
    val session: LearningSession = LearningSession(),
    val selectedLanguage: Language = Language("Arabic", "ara"),
    val supportedLanguages: List<Language> = SupportedLanguage,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class WordCraftViewModel @Inject constructor(
    private val repository: WordCraftRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WordCraftUiState())
    val uiState: StateFlow<WordCraftUiState> = _uiState.asStateFlow()

    fun analyzeImage(imageData: String, imageUri: Uri?) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val imageAnalysis = repository.analyzeImage(imageData)
                _uiState.update {
                    it.copy(
                        session = it.session.copy(
                            imageUrl = imageUri,
                            imageAnalysis = imageAnalysis
                        ),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                // Log the full exception for debugging
                android.util.Log.e("WordCraftViewModel", "Image analysis failed", e)

                val errorMessage = when {
                    e.message?.contains("500") == true -> "Server error: The image data may be too large or in an invalid format. Try using a smaller image."
                    e.message?.contains("403") == true -> "Server rejected the request (HTTP 403). Please check logs for details."
                    e.message?.contains("timeout") == true -> "Connection timeout: Please check your internet connection"
                    else -> "Failed to analyze image: ${e.message}"
                }

                _uiState.update {
                    it.copy(
                        error = errorMessage,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun selectWord(word: String) {
        _uiState.update {
            it.copy(
                session = it.session.copy(selectedWord = word),
                isLoading = true
            )
        }

        viewModelScope.launch {
            try {
                val targetLanguage = _uiState.value.selectedLanguage.code
                val translation = repository.translateWord(word, targetLanguage)

                _uiState.update {
                    it.copy(
                        session = it.session.copy(wordTranslation = translation),
                        isLoading = false
                    )
                }

                generateExamples()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Failed to translate word: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun selectLanguage(language: Language) {
        _uiState.update { it.copy(selectedLanguage = language) }

        val currentWord = _uiState.value.session.selectedWord
        if (currentWord.isNotEmpty()) {
            selectWord(currentWord)
        }
    }

    private fun generateExamples() {
        val session = _uiState.value.session
        val targetLanguage = _uiState.value.selectedLanguage

        if (session.imageAnalysis.tags.isEmpty()) return

        viewModelScope.launch {
            try {
                val examples = repository.generateExamples(
                    targetLanguage = targetLanguage,
                    detectedObjects = session.imageAnalysis.tags,
                    caption = session.imageAnalysis.caption
                )

                _uiState.update {
                    it.copy(
                        session = it.session.copy(examples = examples),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Failed to generate examples: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun selectExample(example: LearningExample) {
        _uiState.update {
            it.copy(
                session = it.session.copy(selectedExample = example),
                isLoading = true
            )
        }

        viewModelScope.launch {
            try {
                val targetLanguage = _uiState.value.selectedLanguage.code
                val sentenceAnalysis = repository.explainSentence(
                    language = targetLanguage,
                    sentence = example.translatedSentence,
                    englishSentence = example.englishSentence
                )

                _uiState.update {
                    it.copy(
                        session = it.session.copy(sentenceAnalysis = sentenceAnalysis),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Failed to analyze sentence: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun resetSession() {
        _uiState.update {
            it.copy(
                session = LearningSession(),
                error = null,
                isLoading = false
            )
        }
    }
}

