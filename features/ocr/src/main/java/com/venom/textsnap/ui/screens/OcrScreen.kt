package com.venom.textsnap.ui.screens

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.venom.textsnap.ui.components.sections.ImagePreviewSection
import com.venom.textsnap.ui.viewmodel.ImageInput.FromUri
import com.venom.textsnap.ui.viewmodel.OcrViewModel
import com.venom.ui.components.common.CustomDragHandle
import com.venom.ui.components.common.adp
import com.venom.ui.viewmodel.TTSViewModel
import com.venom.utils.Extensions.copyToClipboard
import com.venom.utils.Extensions.shareText
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OcrScreen(
    viewModel: OcrViewModel = hiltViewModel(LocalContext.current as ComponentActivity),
    ttsViewModel: TTSViewModel = hiltViewModel(LocalContext.current as ComponentActivity),
    onNavigateToTranslation: (String) -> Unit,
    onFileClick: ((Uri?) -> Unit) -> Unit,
    onCameraClick: ((Uri?) -> Unit) -> Unit,
    onGalleryClick: ((Uri?) -> Unit) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        skipHiddenState = true
    )

    val screenHeight = LocalConfiguration.current.screenHeightDp
    val peekHeight = (screenHeight * if (uiState.hasContent) 0.22 else 0.14).adp

    BackHandler(enabled = sheetState.currentValue == SheetValue.Expanded) {
        scope.launch { sheetState.partialExpand() }
    }

    DisposableEffect(Unit) { onDispose { ttsViewModel.stop() } }

    BottomSheetScaffold(
        scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState),
        modifier = Modifier.navigationBarsPadding(),
        containerColor = Color.Transparent,
        sheetPeekHeight = peekHeight,
        sheetShape = RoundedCornerShape(topStart = 22.adp, topEnd = 22.adp),
        sheetDragHandle = { CustomDragHandle() },
        sheetSwipeEnabled = true,
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(bottom = 8.adp, start = 12.adp, end = 12.adp)
            ) {
                ImagePreviewSection(
                    viewModel = viewModel,
                    modifier = Modifier.weight(1f),
                    onRetry = viewModel::processOcr,
                    onCameraClick = {
                        onCameraClick { uri -> uri?.let { viewModel.processImage(FromUri(it)) } }
                    },
                    onGalleryClick = {
                        onGalleryClick { uri -> uri?.let { viewModel.processImage(FromUri(it), true) } }
                    },
                    onFileClick = {
                        onFileClick { uri -> uri?.let { viewModel.processImage(FromUri(it), true) } }
                    },
                    onToggleSelected = viewModel::toggleSelection,
                    onToggleLabels = viewModel::toggleLabels,
                    onToggleParagraphs = viewModel::toggleParagraphs,
                    onTranslate = viewModel::toggleTranslation
                )
            }
        },
        sheetContent = {
            OcrBottomSheet(
                uiState = uiState,
                maxHeight = (screenHeight * 0.85).adp,
                peekHeight = peekHeight,
                sheetState = sheetState,
                onCopy = { text -> context.copyToClipboard(text) },
                onShare = { text -> context.shareText(text) },
                onSpeak = { text -> ttsViewModel.toggle(text) },
                onTranslate = onNavigateToTranslation
            )
        }
    )
}