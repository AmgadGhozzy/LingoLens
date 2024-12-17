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
import com.venom.textsnap.data.api.OcrApiService
import com.venom.textsnap.data.repository.OcrRepository
import com.venom.textsnap.ui.components.ImagePreviewCard
import com.venom.textsnap.ui.components.OcrBottomSheet
import com.venom.textsnap.ui.components.OcrTopAppBar
import com.venom.textsnap.ui.theme.TextsSnapTheme
import com.venom.textsnap.utils.Constant.sampleUiState
import com.venom.textsnap.utils.Extensions.copyToClipboard
import com.venom.textsnap.utils.Extensions.shareText
import com.venom.textsnap.utils.ImageCompressor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OcrScreen(
    viewModel: OcrViewModel = hiltViewModel(),
    ttsViewModel: TTSViewModel = hiltViewModel(),
    onFileClick: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onNavigateBack: () -> Unit
) {


    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val ttsState by ttsViewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded, skipHiddenState = true
    )

    val screenHeight = LocalConfiguration.current.screenHeightDp
    val peekHeight = screenHeight * if (uiState.recognizedText.isNullOrEmpty()) 0.235 else 0.325
    val maxHeight = screenHeight * 0.8


    // back press when bottom sheet is expanded
    BackHandler(enabled = sheetState.currentValue == SheetValue.Expanded) {
        scope.launch {
            sheetState.partialExpand()
        }
    }

    BottomSheetScaffold(scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState),
		modifier = Modifier.navigationBarsPadding(),
        sheetPeekHeight = peekHeight.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetTonalElevation = 12.dp,
        sheetShadowElevation = 12.dp,
        sheetDragHandle = { BottomSheetDefaults.DragHandle() },
        sheetSwipeEnabled = true,
        topBar = {
            OcrTopAppBar(onNavigateBack = onNavigateBack, onSettings = { })
        },
        content = { paddingValues ->
            OcrContent(uiState = uiState,
                viewModel = viewModel,
                modifier = Modifier.padding(paddingValues),
                onRetry = viewModel::processImage,
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

@Composable
private fun OcrContent(
    viewModel: OcrViewModel,
    modifier: Modifier = Modifier,
    uiState: OcrUiState,
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
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ImagePreviewCard(
            viewModel = viewModel,
            uiState = uiState,
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
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
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
    TextsSnapTheme {
        val context = LocalContext.current
        OcrScreen(viewModel = object : OcrViewModel(
            repository = OcrRepository(OcrApiService.create()),
            imageCompressor = ImageCompressor(context)
        ) {
            override val uiState = MutableStateFlow(sampleUiState).asStateFlow()
        }, onNavigateBack = {}, onCameraClick = {}, onGalleryClick = {}, onFileClick = {})
    }
}

