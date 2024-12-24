package com.venom.textsnap.ui.components.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.venom.resources.R
import com.venom.textsnap.ui.viewmodel.ImageInput.FromBitmap
import com.venom.textsnap.ui.viewmodel.OcrViewModel
import com.venom.ui.components.bars.ActionItem
import com.venom.ui.components.bars.ImageActionBar
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

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            when {
                uiState.imageBitmap != null -> ZoomableImageWithBoundingBoxes(
                    viewModel = viewModel,
                    imageBitmap = uiState.imageBitmap,
                    modifier = Modifier.fillMaxSize()
                )

                else -> EmptyStateContent()
            }

            // Loading Overlay
            if (uiState.isLoading) LoadingOverlay()


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
                            selected = uiState.isParagraphMode && uiState.recognizedText.isNotEmpty(),
                        ),
                        ActionItem.Action(
                            icon = R.drawable.ic_labels_shown,
                            textRes = R.string.action_hide_labels,
                            onClick = onToggleLabels,
                            selected = uiState.showLabels && uiState.recognizedText.isNotEmpty(),
                        ),
                        ActionItem.Action(
                            icon = R.drawable.ic_select_all,
                            textRes = R.string.select_language,
                            onClick = onToggleSelected,
                            selected = uiState.isSelected && uiState.recognizedText.isNotEmpty(),
                        ),
                        ActionItem.Action(
                            icon = R.drawable.icon_translate,
                            textRes = R.string.action_translate,
                            onClick = onTranslate,
                            enabled = uiState.recognizedText.isNotEmpty(),
                        ),
                    ),
                )

                ExpandableFAB(modifier = Modifier.wrapContentSize(),
                    onCameraClick = { onCameraClick();viewModel.reset(); showImageCropper = true },
                    onGalleryClick = {
                        onGalleryClick();viewModel.reset(); showImageCropper = true
                    },
                    onFileClick = { onFileClick();viewModel.reset(); showImageCropper = true })
            }
        }
    }
}

@Composable
private fun EmptyStateContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.empty_state_title),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Use camera or select from gallery",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LoadingOverlay(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.Center),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp
        )
    }
}

@Composable
private fun ErrorOverlay(
    modifier: Modifier = Modifier, onRetry: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
            .padding(32.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_error),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Failed to process image",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.error_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onRetry, colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_retry),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(id = R.string.retry))
            }
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