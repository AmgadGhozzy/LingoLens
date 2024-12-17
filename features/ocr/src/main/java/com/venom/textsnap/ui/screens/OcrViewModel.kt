package com.venom.textsnap.ui.screens

import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.textsnap.data.model.OcrResponse
import com.venom.textsnap.data.model.OriginalResponse
import com.venom.textsnap.data.model.ParagraphBox
import com.venom.textsnap.data.repository.OcrRepository
import com.venom.textsnap.utils.ImageCompressor
import com.venom.textsnap.utils.convertToParagraphBoxes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class OcrUiState(
    val imageUri: Uri? = null,
    val imageBitmap: ImageBitmap? = null,
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
    val isSuccess: Boolean
        get() = !isLoading && error == null && originalResponse != null

    val hasError: Boolean
        get() = error != null

    val currentParagraphs: List<ParagraphBox>
        get() = if (showTranslation) translatedParagraphs ?: paragraphBoxes else paragraphBoxes
}

@HiltViewModel
open class OcrViewModel @Inject constructor(
    private val repository: OcrRepository, private val imageCompressor: ImageCompressor
) : ViewModel() {

    private val _uiState = MutableStateFlow(OcrUiState())
    open val uiState: StateFlow<OcrUiState> = _uiState.asStateFlow()

    fun processImage() {
        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        isLoading = true, error = null
                    )
                }

                val (compressedImageFile, compressedImageBitmap) = imageCompressor.compressImage(
                    uiState.value.imageUri
                )
                Log.d(
                    "OcrViewModel", "Compressed image: $compressedImageBitmap, $compressedImageFile"
                )

                setImageBitmap(compressedImageBitmap)
                compressedImageFile?.let {
                    performOcr(compressedImageFile)
                }
            } catch (e: Exception) {
                Log.e("OcrViewModel", "Failed to process image: $e")
                _uiState.update {
                    it.copy(
                        isLoading = false, error = e.message ?: "Failed to process image"
                    )
                }
            }
        }
    }

    fun setUri(uri: Uri?) {
        _uiState.update { it.copy(imageUri = uri) }
    }

    fun setImageBitmap(imageBitmap: ImageBitmap?) {
        _uiState.update { it.copy(imageBitmap = imageBitmap) }
    }

    private suspend fun performOcr(imageFile: File) {
        try {
            Log.d("OcrViewModel", "Starting OCR processing")
            repository.performOcr(imageFile).onSuccess { ocrResponse ->
                Log.d("OcrViewModel", "Received OCR response")
                processParagraphs(ocrResponse)
            }.onFailure { exception ->
                Log.e("OcrViewModel", "Failed to process OCR", exception)
                _uiState.update {
                    it.copy(
                        isLoading = false, error = exception.message ?: "OCR processing failed"
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("OcrViewModel", "Failed to process OCR", e)
            _uiState.update {
                it.copy(
                    isLoading = false, error = e.message ?: "OCR processing failed"
                )
            }
        }
    }

    private fun processParagraphs(ocrResponse: OcrResponse) {
        val originalResponse = ocrResponse.google.originalResponse
        val paragraphs = convertToParagraphBoxes(originalResponse, _uiState.value.isParagraphMode)

        _uiState.update {
            it.copy(
                isLoading = false,
                originalResponse = originalResponse,
                paragraphBoxes = paragraphs,
                recognizedText = ocrResponse.google.text,
                error = null,
            )
        }
    }

    fun toggleLabels() {
        _uiState.update { it.copy(showLabels = !it.showLabels) }
    }

    fun toggleParagraphs() {
        _uiState.update { currentState ->
            val newParagraphMode = !currentState.isParagraphMode
            val newParagraphs = currentState.originalResponse?.let { response ->
                convertToParagraphBoxes(response, newParagraphMode)
            } ?: currentState.paragraphBoxes

            currentState.copy(
                isParagraphMode = newParagraphMode,
                paragraphBoxes = newParagraphs,
                selectedBoxes = emptySet()
            )
        }
    }

    fun toggleSelectBox(box: ParagraphBox) {
        _uiState.update { currentState ->
            val updatedSelectedBoxes = if (currentState.selectedBoxes.contains(box)) {
                currentState.selectedBoxes - box
            } else {
                currentState.selectedBoxes + box
            }
            currentState.copy(selectedBoxes = updatedSelectedBoxes)
        }
    }

    fun toggleSelection() {
        _uiState.update { currentState ->
            val isCurrentlyEmpty = currentState.selectedBoxes.isEmpty()
            val newSelectedBoxes = if (isCurrentlyEmpty) {
                currentState.paragraphBoxes.toSet()
            } else {
                emptySet()
            }

            currentState.copy(
                isSelected = isCurrentlyEmpty,
                selectedBoxes = newSelectedBoxes
            )
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    fun reset() {
        _uiState.value = OcrUiState()
    }

//    fun translateText() {
//        viewModelScope.launch {
//            try {
//                _uiState.update { it.copy(isLoading = true) }
//
//                val textToTranslate =
//                    _uiState.value.paragraphBoxes.joinToString("&") { it.text }.replace("\n", "")
//                        .replace(".", "").replace(",", "")
//
//                repository.translateText(textToTranslate).onSuccess { translationResult ->
//                        val translatedTexts = URLDecoder.decode(
//                            translationResult.sentences?.firstOrNull()?.trans ?: "", "UTF-8"
//                        )
//
//                        val translatedParagraphs =
//                            _uiState.value.paragraphBoxes.mapIndexed { index, box ->
//                                ParagraphBox(
//                                    text = translatedTexts.split("&").getOrNull(index) ?: box.text,
//                                    boundingBlock = box.boundingBlock
//                                )
//                            }
//
//                        _uiState.update {
//                            it.copy(
//                                isLoading = false,
//                                translatedParagraphs = translatedParagraphs,
//                                showTranslation = true
//                            )
//                        }
//                    }.onFailure { exception ->
//                        _uiState.update {
//                            it.copy(
//                                isLoading = false, error = exception.message ?: "Translation failed"
//                            )
//                        }
//                    }
//            } catch (e: Exception) {
//                _uiState.update {
//                    it.copy(
//                        isLoading = false, error = e.message ?: "Translation failed"
//                    )
//                }
//            }
//        }
//    }

}