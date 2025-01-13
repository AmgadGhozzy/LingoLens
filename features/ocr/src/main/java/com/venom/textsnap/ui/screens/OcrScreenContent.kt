package com.venom.textsnap.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.venom.resources.R
import com.venom.textsnap.ui.components.sections.ImagePreviewSection
import com.venom.textsnap.ui.viewmodel.OcrViewModel
import com.venom.ui.components.bars.TopBar
import com.venom.ui.components.buttons.CustomButton

@Composable
fun OcrScreenContent(
    viewModel: OcrViewModel,
    navController: NavController,
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
            .padding(bottom = 6.dp)
            .padding(horizontal = 8.dp),
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
