package com.venom.ui.viewmodel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.data.repo.TranslationRepository
import com.venom.utils.Extensions.postprocessText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class SentenceCardState(
    val isExpanded: Boolean = false,
    val translatedText: String = "",
    val isLoading: Boolean = false
)

@HiltViewModel
class SentenceCardViewModel @Inject constructor(
    private val repository: TranslationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SentenceCardState())
    val uiState = _uiState.asStateFlow()

    private var translationJob: Job? = null
    private var lastTranslatedSentence: String? = null

    fun updateExpanded(expanded: Boolean) {
        _uiState.update { it.copy(isExpanded = expanded) }
    }

    fun translate(sentence: String) {
        // Skip if already translated this sentence or currently loading it
        if (lastTranslatedSentence == sentence ||
            (_uiState.value.isLoading && lastTranslatedSentence == sentence)) {
            return
        }

        _uiState.update { it.copy(isLoading = true, translatedText = "") }
        lastTranslatedSentence = sentence

        translationJob?.cancel()
        translationJob = viewModelScope.launch {
            repository.getTranslation(query = sentence)
                .onSuccess { response ->
                    _uiState.update {
                        it.copy(
                            translatedText = response.sentences?.firstOrNull()?.trans?.postprocessText().orEmpty(),
                            isLoading = false
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false, translatedText = "") }
                    lastTranslatedSentence = null
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        translationJob?.cancel()
    }
}