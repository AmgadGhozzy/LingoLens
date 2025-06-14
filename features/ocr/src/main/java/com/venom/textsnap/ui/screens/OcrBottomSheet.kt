package com.venom.textsnap.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.textsnap.ui.components.sections.RecognizedTextSection
import com.venom.textsnap.ui.components.sections.SelectedTextList
import com.venom.ui.components.bars.TextActionBar
import com.venom.ui.components.common.ActionItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OcrBottomSheet(
    recognizedText: String,
    selectedTexts: List<String>,
    peekHeight: Dp,
    sheetState: SheetState = rememberStandardBottomSheetState(),
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    onSpeak: (String) -> Unit,
    onTranslate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        RecognizedTextSection(
            text = recognizedText,
            peekHeight = peekHeight,
            isExpanded = sheetState.currentValue == SheetValue.Expanded,
            onCopy = onCopy,
            onShare = onShare,
            onSpeak = onSpeak
        )

        if (recognizedText.isNotEmpty()) {
            TextActionBar(
                actions = listOf(
                    ActionItem.Action(
                        icon = R.drawable.icon_share,
                        description = R.string.action_share,
                        onClick = { onShare(recognizedText) }
                    ),
                    ActionItem.Action(
                        icon = R.drawable.icon_sound,
                        description = R.string.action_speak,
                        onClick = { onSpeak(recognizedText) }
                    ),
                    ActionItem.Action(
                        icon = R.drawable.icon_translate,
                        description = R.string.action_translate,
                        onClick = { onTranslate(recognizedText) }
                    ),
                    ActionItem.Action(
                        icon = R.drawable.icon_copy,
                        description = R.string.action_copy,
                        onClick = { onCopy(recognizedText) }
                    )
                )
            )
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