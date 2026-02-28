package com.venom.textsnap.ui.viewmodel

import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.data.local.entity.OcrEntity
import com.venom.data.mapper.ParagraphBoxMapper.convertToParagraphBoxes
import com.venom.data.remote.respnod.OriginalResponse
import com.venom.data.remote.respnod.ParagraphBox
import com.venom.data.repo.OcrRepository
import com.venom.data.repo.TranslationRepositoryImpl
import com.venom.domain.provider.AppConfigProvider
import com.venom.utils.ImageCompressor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class OcrViewModel @Inject constructor(
    private val repository: OcrRepository,
    private val translationRepository: TranslationRepositoryImpl,
    private val imageCompressor: ImageCompressor,
    private val appConfigProvider: AppConfigProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(OcrUiState())
    val uiState = _uiState.asStateFlow()

    fun processImage(input: ImageInput, processOcrAfter: Boolean = false) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, error = null) }

        runCatching {
            when (input) {
                is ImageInput.FromUri -> {
                    _uiState.update { it.copy(imageUri = input.uri) }
                    imageCompressor.compressImage(input.uri, appConfigProvider.ocrCompressionQuality)
                }
                is ImageInput.FromBitmap -> imageCompressor.compressImage(input.imageBitmap, appConfigProvider.ocrCompressionQuality)
            }
        }.onSuccess { (file, bitmap, uri) ->
            _uiState.update {
                it.copy(
                    imageUri = uri ?: it.imageUri,
                    imageBitmap = bitmap,
                    compressedFile = file,
                    isLoading = false
                )
            }
            if (processOcrAfter) processOcr()
        }.onFailure { e ->
            _uiState.update { it.copy(isLoading = false, error = e.message ?: "Image compression failed") }
        }
    }

    fun processOcr() = viewModelScope.launch {
        if (!appConfigProvider.isOcrEnabled) {
            _uiState.update { it.copy(error = "OCR feature is currently disabled remotely.") }
            return@launch
        }
        val file = _uiState.value.compressedFile ?: return@launch
        _uiState.update { it.copy(isLoading = true, error = null) }

        repository.performOcr(file)
            .onSuccess { response ->
                val original = response.google.originalResponse
                val paragraphs = convertToParagraphBoxes(original, true)

                _uiState.update {
                    it.copy(
                        originalResponse = original,
                        paragraphBoxes = paragraphs,
                        recognizedText = response.google.text,
                        isLoading = false,
                        error = null
                    )
                }

                repository.saveOcrEntry(
                    OcrEntity(
                        recognizedText = response.google.text,
                        imageData = file.readBytes(),
                        boundingBoxes = paragraphs,
                        apiResponse = response
                    )
                )
                translateParagraphs("ar")
            }
            .onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "OCR failed") }
            }
    }

    fun translateParagraphs(targetLang: String) = viewModelScope.launch {
        val paragraphs = _uiState.value.paragraphBoxes.ifEmpty { return@launch }
        _uiState.update { it.copy(isLoading = true, error = null) }

        val separator = "★"
        val combined = paragraphs.joinToString(separator) { it.text }

        translationRepository.translate(combined, targetLang = targetLang, providerId = "google", forceRefresh = false)
            .onSuccess { response ->
                val translated = response.translatedText.split(separator)
                _uiState.update {
                    it.copy(
                        translatedParagraphs = paragraphs.zip(translated) { box, text -> box.copy(text = text) },
                        isLoading = false
                    )
                }
            }
            .onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = "Translation failed: ${e.message}") }
            }
    }

    fun toggleLabels() = _uiState.update { it.copy(showLabels = !it.showLabels) }

    fun toggleParagraphs() = _uiState.update { state ->
        val newMode = !state.isParagraphMode
        val paragraphs = state.originalResponse?.let { convertToParagraphBoxes(it, newMode) }
            ?: state.paragraphBoxes
        state.copy(
            isParagraphMode = newMode,
            paragraphBoxes = paragraphs,
            showTranslation = newMode && state.showTranslation,
            selectedBoxes = emptySet()
        )
    }

    fun toggleTranslation() = _uiState.update { state ->
        if (!state.isParagraphMode) {
            val paragraphs = state.originalResponse?.let { convertToParagraphBoxes(it, true) }
                ?: state.paragraphBoxes
            state.copy(
                isParagraphMode = true,
                paragraphBoxes = paragraphs,
                showTranslation = true,
                selectedBoxes = emptySet()
            )
        } else {
            state.copy(showTranslation = !state.showTranslation, selectedBoxes = emptySet())
        }
    }

    fun toggleSelectBox(box: ParagraphBox) = _uiState.update { state ->
        val selected = if (box in state.selectedBoxes) state.selectedBoxes - box else state.selectedBoxes + box
        state.copy(selectedBoxes = selected)
    }

    fun toggleSelection() = _uiState.update { state ->
        state.copy(
            selectedBoxes = if (state.selectedBoxes.isEmpty()) state.paragraphBoxes.toSet() else emptySet()
        )
    }

    fun reset() { _uiState.value = OcrUiState() }
}

sealed interface ImageInput {
    data class FromUri(val uri: Uri) : ImageInput
    data class FromBitmap(val imageBitmap: ImageBitmap) : ImageInput
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
    val isParagraphMode: Boolean = true
) {
    val isSuccess get() = !isLoading && error == null && originalResponse != null
    val hasError get() = error != null
    val isAllSelected get() = selectedBoxes.isNotEmpty() && selectedBoxes.size == paragraphBoxes.size
    val hasContent get() = recognizedText.isNotEmpty()

    val currentParagraphs
        get() = if (showTranslation) translatedParagraphs ?: paragraphBoxes else paragraphBoxes

    private val separator get() = if (isParagraphMode) "\n" else " "

    val currentRecognizedText
        get() = if (showTranslation) {
            translatedParagraphs?.joinToString(separator) { it.text } ?: recognizedText
        } else recognizedText

    val selectedTexts
        get() = selectedBoxes.takeIf { it.isNotEmpty() }
            ?.joinToString(separator) { it.text }
            ?: currentRecognizedText
}