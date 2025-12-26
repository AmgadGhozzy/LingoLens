package com.venom.textsnap.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.textsnap.ui.components.sections.RecognizedTextSection
import com.venom.textsnap.ui.components.sections.SelectedTextList
import com.venom.textsnap.ui.viewmodel.OcrUiState
import com.venom.ui.components.bars.TextActionBar
import com.venom.ui.components.common.ActionItem
import com.venom.ui.components.other.GlassCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OcrBottomSheet(
    uiState: OcrUiState,
    maxHeight: Dp,
    peekHeight: Dp,
    sheetState: SheetState,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    onSpeak: (String) -> Unit,
    onTranslate: (String) -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxSize()
            .heightIn(max = maxHeight),
        contentPadding = 18.dp,
        solidBackgroundAlpha = 0.01f
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RecognizedTextSection(
                text = uiState.recognizedText,
                peekHeight = peekHeight,
                isExpanded = sheetState.currentValue == SheetValue.Expanded,
                onCopy = onCopy,
                onShare = onShare,
                onSpeak = onSpeak
            )

            if (uiState.recognizedText.isNotEmpty()) {
                GlassCard(
                    solidBackgroundAlpha = 0.8f,
                    shape = MaterialTheme.shapes.medium
                ) {
                    TextActionBar(
                        actions = listOf(
                            ActionItem.Action(
                                icon = R.drawable.icon_share,
                                description = R.string.action_share,
                                onClick = { onShare(uiState.recognizedText) }
                            ),
                            ActionItem.Action(
                                icon = R.drawable.icon_sound,
                                description = R.string.action_speak,
                                onClick = { onSpeak(uiState.recognizedText) }
                            ),
                            ActionItem.Action(
                                icon = R.drawable.icon_translate,
                                description = R.string.action_translate,
                                onClick = { onTranslate(uiState.recognizedText) }
                            ),
                            ActionItem.Action(
                                icon = R.drawable.icon_copy,
                                description = R.string.action_copy,
                                onClick = { onCopy(uiState.recognizedText) }
                            )
                        )
                    )
                }
            }

            SelectedTextList(
                texts = uiState.selectedBoxes.map { it.text },
                isSingleSelection = uiState.selectedBoxes.map { it.text }.size == 1,
                onCopy = onCopy,
                onShare = onShare,
                onSpeak = onSpeak,
                onTranslate = onTranslate
            )
        }
    }
}