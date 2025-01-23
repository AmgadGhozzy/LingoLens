package com.venom.textsnap.ui.components.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.venom.resources.R
import com.venom.textsnap.ui.components.ErrorOverlay
import com.venom.textsnap.ui.components.OcrLoadingOverlay
import com.venom.textsnap.ui.viewmodel.ImageInput.FromBitmap
import com.venom.textsnap.ui.viewmodel.OcrViewModel
import com.venom.ui.components.bars.ImageActionBar
import com.venom.ui.components.common.ActionItem
import com.venom.ui.components.common.EmptyState
import com.venom.ui.components.dialogs.ImageCropperDialog
import com.venom.ui.components.menus.ExpandableFAB

@Composable
fun ImagePreviewSection(
    viewModel: OcrViewModel,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onFileClick: () -> Unit,
    onToggleSelected: () -> Unit,
    onToggleLabels: () -> Unit,
    onToggleParagraphs: () -> Unit,
    onTranslate: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    var showImageCropper by remember { mutableStateOf(false) }

    if (showImageCropper && uiState.imageBitmap != null) {
        ImageCropperDialog(onDismissRequest = { showImageCropper = false; viewModel.reset() },
            imageBitmap = uiState.imageBitmap,
            onImageCropped = { croppedBitmap ->
                viewModel.processImage(FromBitmap(croppedBitmap), processOcrAfter = true)
                showImageCropper = false
            })
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.BottomCenter
    ) {
        when {
            uiState.imageBitmap != null -> ZoomableImageWithBoundingBoxes(
                viewModel = viewModel,
                imageBitmap = uiState.imageBitmap,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            )

            else -> EmptyState(
                icon = R.drawable.icon_ocr_mode,
                title = stringResource(id = R.string.ocr_empty_state_title),
                subtitle = stringResource(id = R.string.ocr_empty_state_description),
            )
        }

        // Loading Overlay
        if (uiState.isLoading) OcrLoadingOverlay()


        // Error Overlay
        if (uiState.hasError) ErrorOverlay(onRetry = onRetry)

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(12.dp)
        ) {

            ImageActionBar(
                modifier = Modifier.weight(1f),
                actions = listOf(
                    ActionItem.Action(
                        icon = R.drawable.ic_paragraph_on,
                        textRes = R.string.action_hide_paragraphs,
                        onClick = onToggleParagraphs,
                        selected = uiState.isParagraphMode && uiState.currentRecognizedText.isNotEmpty(),
                    ),
                    ActionItem.Action(
                        icon = R.drawable.ic_labels_shown,
                        textRes = R.string.action_hide_labels,
                        onClick = onToggleLabels,
                        selected = uiState.showLabels && uiState.currentRecognizedText.isNotEmpty(),
                    ),
                    ActionItem.Action(
                        icon = R.drawable.ic_select_all,
                        textRes = R.string.select_language,
                        onClick = onToggleSelected,
                        selected = uiState.isSelected && uiState.currentRecognizedText.isNotEmpty(),
                    ),
                    ActionItem.Action(
                        icon = R.drawable.icon_translate,
                        textRes = R.string.action_translate,
                        onClick = onTranslate,
                        selected = uiState.showTranslation && uiState.currentRecognizedText.isNotEmpty(),
                    ),
                ),
            )

            ExpandableFAB(modifier = Modifier.wrapContentSize(),
                onCameraClick = { onCameraClick();viewModel.reset(); showImageCropper = true },
                onGalleryClick = {
                    onGalleryClick();viewModel.reset(); //showImageCropper = true
                },
                onFileClick = { onFileClick();viewModel.reset(); showImageCropper = true })
        }
    }

}

@Preview
@Composable
fun ImagePreviewCardPreview() {
//    ImagePreviewCard(
//        onRetry = {},
//        onFileClick = {},
//        onToggleLabels = {},
//        onToggleSelected = {},
//        onToggleParagraphs = {},
//        onTranslate = {},
//        onCameraClick = {},
//        onGalleryClick = {},
//    )
}