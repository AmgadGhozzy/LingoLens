package com.venom.textsnap.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.venom.textsnap.ui.viewmodel.OcrUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OcrBottomSheetContent(
    uiState: OcrUiState,
    maxHeight: Dp,
    peekHeight: Dp,
    sheetState: SheetState,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    onSpeak: (String) -> Unit,
    onTranslate: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = maxHeight)
            .padding(horizontal = 8.dp)
    ) {
        OcrBottomSheet(
            recognizedText = uiState.selectedTexts,
            selectedTexts = uiState.selectedBoxes.map { it.text },
            peekHeight = peekHeight,
            sheetState = sheetState,
            onCopy = onCopy,
            onShare = onShare,
            onSpeak = onSpeak,
            onTranslate = onTranslate
        )
    }
}