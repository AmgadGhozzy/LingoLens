package com.venom.textsnap.ui.viewmodel

import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.data.mapper.ParagraphBoxMapper.convertToParagraphBoxes
import com.venom.data.model.OcrResponse
import com.venom.data.model.OriginalResponse
import com.venom.data.model.ParagraphBox
import com.venom.data.repo.OcrRepository
import com.venom.utils.ImageCompressor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class OcrViewModel @Inject constructor(
    private val repository: OcrRepository, private val imageCompressor: ImageCompressor
) : ViewModel() {

    private val _uiState = MutableStateFlow(OcrUiState())
    val uiState: StateFlow<OcrUiState> = _uiState.asStateFlow()

    fun processImage(input: ImageInput, processOcrAfter: Boolean = false) = viewModelScope.launch {
        runCatching {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val (compressedFile, compressedBitmap, compressedUri) = when (input) {
                is ImageInput.FromUri -> {
                    setUri(input.uri)
                    imageCompressor.compressImage(input.uri)
                }

                is ImageInput.FromBitmap -> {
                    imageCompressor.compressImage(input.imageBitmap)
                }
            }

            Triple(compressedFile, compressedBitmap, compressedUri)
        }.onSuccess { (file, bitmap, uri) ->
            uri?.let { setUri(it) }
            setImageBitmap(bitmap)
            setcompressedFile(file)
            _uiState.update { it.copy(isLoading = false) }

            if (processOcrAfter) processOcr()
        }.onFailure { e ->
            _uiState.update {
                it.copy(
                    isLoading = false, error = e.message ?: "Image compression failed"
                )
            }
        }
    }

    fun processOcr() = viewModelScope.launch {
        val compressedFile = uiState.value.compressedFile ?: return@launch
        _uiState.update { it.copy(isLoading = true, error = null) }

        repository.performOcr(compressedFile).onSuccess { response ->
            processParagraphs(response)
            _uiState.update { it.copy(isLoading = false) }
        }.onFailure { e ->
            _uiState.update {
                it.copy(
                    isLoading = false, error = e.message ?: "OCR failed"
                )
            }
        }
    }

    private fun processParagraphs(ocrResponse: OcrResponse) {
        val originalResponse = ocrResponse.google.originalResponse
        val paragraphs = convertToParagraphBoxes(originalResponse, _uiState.value.isParagraphMode)

        _uiState.update {
            it.copy(
                originalResponse = originalResponse,
                paragraphBoxes = paragraphs,
                recognizedText = ocrResponse.google.text,
                error = null
            )
        }
    }

    // State updates
    fun setUri(uri: Uri) = _uiState.update { it.copy(imageUri = uri) }

    fun setImageBitmap(imageBitmap: ImageBitmap?) =
        _uiState.update { it.copy(imageBitmap = imageBitmap) }

    fun setcompressedFile(file: File?) = _uiState.update { it.copy(compressedFile = file) }

    // UI actions
    fun toggleLabels() = _uiState.update { it.copy(showLabels = !it.showLabels) }

    fun toggleParagraphs() = _uiState.update { state ->
        val newMode = !state.isParagraphMode
        val newParagraphs = state.originalResponse?.let {
            convertToParagraphBoxes(it, newMode)
        } ?: state.paragraphBoxes

        state.copy(
            isParagraphMode = newMode, paragraphBoxes = newParagraphs, selectedBoxes = emptySet()
        )
    }

    fun toggleSelectBox(box: ParagraphBox) = _uiState.update { state ->
        state.copy(
            selectedBoxes = if (box in state.selectedBoxes) {
                state.selectedBoxes - box
            } else {
                state.selectedBoxes + box
            }
        )
    }

    fun toggleSelection() = _uiState.update { state ->
        val newSelection =
            if (state.selectedBoxes.isEmpty()) state.paragraphBoxes.toSet() else emptySet()
        state.copy(isSelected = state.selectedBoxes.isEmpty(), selectedBoxes = newSelection)
    }

    fun dismissError() = _uiState.update { it.copy(error = null) }

    fun reset() {
        _uiState.value = OcrUiState()
    }
}


sealed class ImageInput {
    data class FromUri(val uri: Uri) : ImageInput()
    data class FromBitmap(val imageBitmap: ImageBitmap) : ImageInput()
}

data class OcrUiState(
    val imageUri: Uri = Uri.EMPTY,
    val imageBitmap: ImageBitmap? = null,
    val compressedFile: File? = null,
    val recognizedText: String = "",
    val paragraphBoxes: List<ParagraphBox> = emptyList(),
    val translatedParagraphs: List<ParagraphBox>? = null,
    val selectedBoxes: Set<ParagraphBox> = emptySet(),
    val originalResponse: OriginalResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showLabels: Boolean = true,
    val showTranslation: Boolean = false,
    val isParagraphMode: Boolean = true,
    val isSelected: Boolean = false,
    val isTranslate: Boolean = false,
) {
    val isSuccess get() = !isLoading && error == null && originalResponse != null
    val hasError get() = error != null
    val currentParagraphs
        get() = if (showTranslation) translatedParagraphs ?: paragraphBoxes else paragraphBoxes
}