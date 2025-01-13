package com.venom.textsnap.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.venom.textsnap.ui.viewmodel.OcrUiState

@Composable
fun OcrBottomSheetContent(
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
