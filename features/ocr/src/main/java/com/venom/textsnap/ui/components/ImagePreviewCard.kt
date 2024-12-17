package com.venom.textsnap.ui.components

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.textsnap.R
import com.venom.textsnap.data.api.OcrApiService
import com.venom.textsnap.data.repository.OcrRepository
import com.venom.textsnap.ui.screens.OcrUiState
import com.venom.textsnap.ui.screens.OcrViewModel
import com.venom.textsnap.utils.Constant.sampleUiState
import com.venom.textsnap.utils.ImageCompressor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


@Composable
fun ImagePreviewCard(
    viewModel: OcrViewModel,
    uiState: OcrUiState,
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
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            when {
                uiState.imageBitmap != null -> {
                    ZoomableImageWithBoundingBoxes(
                        viewModel = viewModel,
                        imageBitmap = uiState.imageBitmap,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                else -> EmptyStateContent()
            }

            // Loading Overlay
            if (uiState.isLoading) {
                LoadingOverlay()
            }

            // Error Overlay
            if (uiState.hasError) {
                ErrorOverlay(onRetry = onRetry)
            }
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(12.dp)
            ) {
                ImageActionBar(
                    modifier = Modifier.weight(1f),
                    showLabels = uiState.showLabels && uiState.recognizedText.isNotEmpty(),
                    isParageraphMode = uiState.isParagraphMode && uiState.recognizedText.isNotEmpty(),
                    isSelected = uiState.isSelected,
                    isTranslate = uiState.isTranslate && uiState.recognizedText.isNotEmpty(),
                    onToggleSelected = onToggleSelected,
                    onTranslateClick = onTranslate,
                    onToggleLabels = onToggleLabels,
                    onToggleParagraphs = onToggleParagraphs
                )

                ExpandableFAB(
                    modifier = Modifier.wrapContentSize(),
                    onCameraClick = onCameraClick,
                    onGalleryClick = onGalleryClick,
                    onFileClick = onFileClick
                )
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
            text = "Add an image to start scanning",
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
                text = "Please try again",
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
                Text(text = "Retry")
            }
        }
    }
}


@Preview
@Composable
fun ImagePreviewCardPreview() {
    val context = LocalContext.current
    ImagePreviewCard(
        viewModel = object : OcrViewModel(
            repository = OcrRepository(OcrApiService.create()),
            imageCompressor = ImageCompressor(context)
        ) {
            override val uiState = MutableStateFlow(sampleUiState).asStateFlow()
        },
        uiState = sampleUiState,
        onRetry = {},
        onFileClick = {},
        onToggleLabels = {},
        onToggleSelected = {},
        onToggleParagraphs = {},
        onTranslate = {},
        onCameraClick = {},
        onGalleryClick = {},
    )
}