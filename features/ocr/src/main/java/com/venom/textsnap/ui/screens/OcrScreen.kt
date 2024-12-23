package com.venom.textsnap.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.venom.textsnap.ui.components.sections.ImagePreviewSection
import com.venom.textsnap.ui.viewmodel.OcrUiState
import com.venom.textsnap.ui.viewmodel.OcrViewModel
import com.venom.ui.components.common.CustomDragHandle
import com.venom.ui.theme.LingoLensTheme
import com.venom.ui.theme.LocalPeekHeight
import com.venom.ui.viewmodel.TTSViewModel
import com.venom.utils.Extensions.copyToClipboard
import com.venom.utils.Extensions.shareText
import kotlinx.coroutines.launch


/**
 * Main OCR screen that handles image processing and text recognition.
 *
 * @param viewModel ViewModel handling OCR business logic
 * @param ttsViewModel ViewModel for text-to-speech functionality
 * @param onFileClick Callback when file selection is requested
 * @param onCameraClick Callback when camera capture is requested
 * @param onGalleryClick Callback when gallery selection is requested
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OcrScreen(
    viewModel: OcrViewModel = hiltViewModel(),
    ttsViewModel: TTSViewModel = hiltViewModel(),
    onFileClick: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded, skipHiddenState = true
    )

    val screenHeight = LocalConfiguration.current.screenHeightDp
    val peekHeight by remember(uiState.recognizedText) {
        derivedStateOf {
            screenHeight * if (uiState.recognizedText.isEmpty()) 0.14 else 0.22
        }
    }
    val maxHeight = screenHeight * 0.8

    // back press when bottom sheet is expanded
    BackHandler(enabled = sheetState.currentValue == SheetValue.Expanded) {
        scope.launch {
            sheetState.partialExpand()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            ttsViewModel.stopSpeaking()
        }
    }

    LocalPeekHeight provides peekHeight.dp

    CompositionLocalProvider {
        BottomSheetScaffold(scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState),
            modifier = Modifier.navigationBarsPadding(),
            sheetPeekHeight = peekHeight.dp,
            sheetShape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
            sheetDragHandle = { CustomDragHandle() },
            sheetSwipeEnabled = true,
            content = { paddingValues ->
                OcrContent(viewModel = viewModel,
                    modifier = Modifier.padding(paddingValues),
                    onRetry = viewModel::processOcr,
                    onFileClick = onFileClick,
                    onCameraClick = onCameraClick,
                    onGalleryClick = onGalleryClick,
                    onToggleSelected = viewModel::toggleSelection,
                    onToggleLabels = viewModel::toggleLabels,
                    onToggleParagraphs = viewModel::toggleParagraphs,
                    onTranslate = { })
            },
            sheetContent = {
                OcrBottomSheetContent(uiState = uiState,
                    maxHeight = maxHeight.dp,
                    onCopy = { text -> context.copyToClipboard(text) },
                    onShare = { text -> (context as Activity).shareText(text) },
                    onSpeak = { text -> ttsViewModel.speak(text) })
            })
    }
}

@Composable
private fun OcrContent(
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

@Composable
private fun OcrBottomSheetContent(
    uiState: OcrUiState,
    maxHeight: Dp,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    onSpeak: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = maxHeight)
            .padding(horizontal = 12.dp, vertical = 6.dp),

    ) {
        OcrBottomSheet(
            recognizedText = uiState.recognizedText,
            recognizedList = uiState.selectedBoxes.map { it.text },
            selectedTexts = uiState.selectedBoxes.map { it.text },
            isParageraphMode = uiState.isParagraphMode,
            onCopy = onCopy,
            onShare = onShare,
            onSpeak = onSpeak
        )
    }
}


@Preview(showBackground = true, device = "id:pixel_8")
@Composable
fun OcrScreenPreview() {
    LingoLensTheme() {
        OcrScreen(onCameraClick = {}, onGalleryClick = {}, onFileClick = {})
    }
}

