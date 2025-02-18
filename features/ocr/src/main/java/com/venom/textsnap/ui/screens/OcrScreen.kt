package com.venom.textsnap.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.venom.textsnap.ui.viewmodel.OcrViewModel
import com.venom.ui.components.common.CustomDragHandle
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
    onNavigateToTranslation: (String) -> Unit,
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
    val peekHeight by remember(uiState.currentRecognizedText) {
        derivedStateOf {
            (screenHeight * if (uiState.currentRecognizedText.isEmpty()) 0.14 else 0.22).dp
        }
    }
    val maxHeight = screenHeight * 0.85

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

    BottomSheetScaffold(scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState),
        modifier = Modifier.navigationBarsPadding(),
        sheetPeekHeight = peekHeight,
        sheetShape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        sheetDragHandle = { CustomDragHandle() },
        sheetSwipeEnabled = true,
        content = { paddingValues ->
            OcrScreenContent(
                viewModel = viewModel,
                modifier = Modifier.padding(paddingValues),
                onFileClick = onFileClick,
                onCameraClick = onCameraClick,
                onGalleryClick = onGalleryClick,
                onRetry = viewModel::processOcr,
                onToggleSelected = viewModel::toggleSelection,
                onToggleLabels = viewModel::toggleLabels,
                onToggleParagraphs = viewModel::toggleParagraphs,
                onTranslate = viewModel::toggleTranslation
            )
        },
        sheetContent = {
            OcrBottomSheetContent(
                uiState = uiState,
                maxHeight = maxHeight.dp,
                peekHeight = peekHeight,
                sheetState = sheetState,
                onCopy = { text -> context.copyToClipboard(text) },
                onShare = { text -> context.shareText(text) },
                onSpeak = { text -> ttsViewModel.speak(text) },
                onTranslate = onNavigateToTranslation
            )
        })
}
