package com.venom.textsnap.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


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
            .padding(horizontal = 6.dp)
    ) {
        // Recognized text section
        RecognizedTextSection(text = selectedTexts.takeIf { it.isNotEmpty() }
            ?.joinToString(if (isParageraphMode) "\n\n" else " ") ?: recognizedText)

        // Text action bar
        if (recognizedText.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            TextActionBar(onCopy = { onCopy(recognizedText) },
                onShare = { onShare(recognizedText) },
                onSpeak = { onSpeak(recognizedText) },
                onTranslate = {})
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
        )

        // Selected text list
        LazyColumn(
            modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            items((if (selectedTexts.isEmpty()) recognizedList else selectedTexts).withIndex()
                .toList(), key = { it.index }) { (index, text) ->
                SelectedTextItem(text = text,
                    onCopy = { onCopy(text) },
                    onShare = { onShare(text) },
                    onSpeak = { onSpeak(text) })
            }
        }
    }
}

@Composable
private fun RecognizedTextSection(
    text: String, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        val scrollState = rememberScrollState()
        SelectionContainer {
            Text(
                text = text.ifEmpty { "No text recognized yet" },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (text.isEmpty()) 0.6f else 1f),
                overflow = TextOverflow.Ellipsis,
                maxLines = 5,
                minLines = 5,
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(scrollState)
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
