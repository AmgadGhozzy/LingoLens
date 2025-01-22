package com.venom.textsnap.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.textsnap.ui.components.TextAction
import com.venom.textsnap.ui.components.sections.RecognizedTextSection
import com.venom.textsnap.ui.components.sections.SelectedTextList
import com.venom.ui.components.bars.TextActionBar
import com.venom.ui.components.common.ActionItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OcrBottomSheet(
    recognizedText: String,
    selectedTexts: List<String>,
    isParageraphMode: Boolean,
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
            .padding(horizontal = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Responsive recognized text section
        RecognizedTextSection(text = selectedTexts.takeIf { it.isNotEmpty() }
            ?.joinToString(if (isParageraphMode) "\n" else " ") ?: recognizedText,
            peekHeight = peekHeight,
            isExpanded = sheetState.currentValue == SheetValue.Expanded,
            onCopy = onCopy,
            onShare = onShare,
            onSpeak = onSpeak)

        if (recognizedText.isNotEmpty()) {
            TextActionBar(
                actions = listOf(
                    ActionItem.Action(icon = R.drawable.icon_share,
                        textRes = R.string.action_share,
                        onClick = { onShare(recognizedText) }),
                    ActionItem.Action(icon = R.drawable.icon_sound,
                        textRes = R.string.action_speak,
                        onClick = { onSpeak(recognizedText) }),
                    ActionItem.Action(icon = R.drawable.icon_translate,
                        textRes = R.string.action_translate,
                        onClick = { onTranslate(recognizedText) }),
                    ActionItem.Action(icon = R.drawable.icon_copy,
                        textRes = R.string.action_copy,
                        onClick = { onCopy(recognizedText) })
                )
            )
        }

        SelectedTextList(texts = selectedTexts,
            isSingleSelection = selectedTexts.size == 1,
            onAction = { text, action ->
                when (action) {
                    TextAction.Copy -> onCopy(text)
                    TextAction.Share -> onShare(text)
                    TextAction.Speak -> onSpeak(text)
                    TextAction.Translate -> onTranslate(text)
                }
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun OcrBottomSheetPreview() {
    OcrBottomSheet(recognizedText = "TextSnap\nThe\nbest\ntext\nrecognition\napp\nin\nmarket",
        selectedTexts = listOf(
            "This is a sample text for preview purposes",
            "TextSnap",
            "The",
            "Best",
            "Text",
            "Recognition",
            "App",
            "In",
            "Market"
        ),
        isParageraphMode = true,
        peekHeight = 0.dp,
        onCopy = {},
        onShare = {},
        onSpeak = {},
        onTranslate = {})
}
