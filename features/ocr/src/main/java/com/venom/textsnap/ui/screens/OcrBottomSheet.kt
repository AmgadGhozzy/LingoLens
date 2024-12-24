package com.venom.textsnap.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.textsnap.ui.components.SelectedTextItem
import com.venom.textsnap.ui.components.TextAction
import com.venom.ui.components.bars.ActionItem
import com.venom.ui.components.bars.TextActionBar
import com.venom.ui.components.buttons.CustomButton
import com.venom.ui.components.dialogs.CustomCard
import com.venom.ui.components.inputs.CustomTextField


@Composable
fun OcrBottomSheet(
    recognizedText: String,
    recognizedList: List<String>,
    selectedTexts: List<String>,
    isParageraphMode: Boolean,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    onSpeak: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 6.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Recognized text section
        RecognizedTextSection(text = selectedTexts.takeIf { it.isNotEmpty() }
            ?.joinToString(if (isParageraphMode) "\n" else " ") ?: recognizedText)

        // Text action bar
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
                        onClick = {}),
                    ActionItem.Action(icon = R.drawable.icon_copy,
                        textRes = R.string.action_copy,
                        onClick = { onCopy(recognizedText) })
                )
            )
        }

        if (selectedTexts.isNotEmpty()) {
            // Selected text list
            LazyColumn(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                items((if (selectedTexts.isEmpty()) recognizedList else selectedTexts).withIndex()
                    .toList(), key = { it.index }) { (index, text) ->
                    SelectedTextItem(text = text,
                        expanded = selectedTexts.size == 1,
                        onActionSelected = { action ->
                            when (action) {
                                TextAction.Copy -> onCopy(text)
                                TextAction.Share -> onShare(text)
                                TextAction.Speak -> onSpeak(text)
                            }
                        })
                }
            }
        }

    }
}
@Composable
private fun RecognizedTextSection(
    text: String, modifier: Modifier = Modifier
) {
    CustomCard {
        Box {
            SelectionContainer(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                CustomTextField(
                    textValue = TextFieldValue(text),
                    placeHolderText = stringResource(R.string.recognized_text_placeholder),
                    maxLines = 4,
                    maxHeight = 106.dp,
                    minHeight = 106.dp,
                    isReadOnly = true
                )
            }
            CustomButton(
                icon = R.drawable.icon_fullscreen,
                contentDescription = stringResource(R.string.action_fullscreen),
                modifier = Modifier
                    .align(Alignment.TopEnd),
                onClick = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OcrBottomSheetPreview() {
    OcrBottomSheet(recognizedText = "TextSnap\nThe\nbest\ntext\nrecognition\napp\nin\nmarket",
        recognizedList = listOf(
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
        onCopy = {},
        onShare = {},
        onSpeak = {})
}
