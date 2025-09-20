package com.venom.ui.viewmodel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.data.remote.respnod.NetworkResult
import com.venom.data.remote.respnod.SentenceResponse
import com.venom.data.repo.SentenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class SentenceUiState(
    val isLoading: Boolean = false,
    val sentenceResponse: SentenceResponse? = null,
    val error: String? = null
)

@HiltViewModel
class SentenceViewModel @Inject constructor(
    private val repository: SentenceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SentenceUiState())
    val uiState = _uiState.asStateFlow()

    fun searchWord(word: String, limit: Int = 20, source: String = "all") {
        if (word.isBlank()) return

        viewModelScope.launch {
            repository.getSentences(word.trim().lowercase(), limit, source).collect { result ->
                _uiState.value = when (result) {
                    is NetworkResult.Loading -> _uiState.value.copy(isLoading = true, error = null)
                    is NetworkResult.Success -> _uiState.value.copy(
                        isLoading = false,
                        sentenceResponse = result.data,
                        error = null
                    )

                    is NetworkResult.Error -> _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun getCacheInfo() = viewModelScope.launch { repository.getCacheInfo() }
    fun clearCache() = viewModelScope.launch { repository.clearCache() }
    fun clearError() = _uiState.update { it.copy(error = null) }
}