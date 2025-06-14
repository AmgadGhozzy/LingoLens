package com.venom.textsnap.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.venom.textsnap.ui.components.sections.ImagePreviewSection
import com.venom.textsnap.ui.viewmodel.OcrViewModel

@Composable
fun OcrScreenContent(
    viewModel: OcrViewModel,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onToggleSelected: () -> Unit,
    onFileClick: () -> Unit,
    onToggleLabels: () -> Unit,
    onToggleParagraphs: () -> Unit,
    onTranslate: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 8.dp)
            .padding(horizontal = 12.dp)
    ) {
        ImagePreviewSection(
            viewModel = viewModel,
            modifier = Modifier.weight(1f),
            onRetry = onRetry,
            onCameraClick = onCameraClick,
            onGalleryClick = onGalleryClick,
            onToggleSelected = onToggleSelected,
            onFileClick = onFileClick,
            onToggleLabels = onToggleLabels,
            onToggleParagraphs = onToggleParagraphs,
            onTranslate = onTranslate
        )
    }
}