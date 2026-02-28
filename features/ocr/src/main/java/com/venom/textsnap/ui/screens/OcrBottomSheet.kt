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
import com.venom.resources.R
import com.venom.textsnap.ui.components.sections.RecognizedTextSection
import com.venom.textsnap.ui.components.sections.SelectedTextList
import com.venom.textsnap.ui.viewmodel.OcrUiState
import com.venom.ui.components.bars.TextActionBar
import com.venom.ui.components.common.ActionItem
import com.venom.ui.components.common.adp
import com.venom.ui.components.other.GlassCard
import com.venom.ui.components.other.GlassThickness

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
    val text = uiState.currentRecognizedText
    val selectedTexts = uiState.selectedBoxes.map { it.text }

    GlassCard(
        modifier = Modifier.fillMaxSize().heightIn(max = maxHeight),
        thickness = GlassThickness.UltraThin,
        contentPadding = 18.adp,
        showBorder = false,
        showShadow = false
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.adp)) {
            RecognizedTextSection(
                text = text,
                peekHeight = peekHeight,
                isExpanded = sheetState.currentValue == SheetValue.Expanded,
                onCopy = onCopy,
                onShare = onShare,
                onSpeak = onSpeak
            )

            if (text.isNotEmpty()) {
                GlassCard(thickness = GlassThickness.Thick, shape = MaterialTheme.shapes.medium) {
                    TextActionBar(
                        actions = listOf(
                            ActionItem.Action(R.drawable.icon_share, R.string.action_share, { onShare(text) }),
                            ActionItem.Action(R.drawable.icon_sound, R.string.action_speak, { onSpeak(text) }),
                            ActionItem.Action(R.drawable.icon_translate, R.string.action_translate, { onTranslate(text) }),
                            ActionItem.Action(R.drawable.icon_copy, R.string.action_copy, { onCopy(text) })
                        )
                    )
                }
            }

            SelectedTextList(
                texts = selectedTexts,
                isSingleSelection = selectedTexts.size == 1,
                onCopy = onCopy,
                onShare = onShare,
                onSpeak = onSpeak,
                onTranslate = onTranslate
            )
        }
    }
}