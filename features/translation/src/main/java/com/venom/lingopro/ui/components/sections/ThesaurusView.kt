package com.venom.lingopro.ui.components.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.ui.components.items.WordChipItem

/**
 * A composable function that displays a thesaurus view with a list of synonyms.
 *
 * @param synonyms A list of synonyms to display. The list cannot be empty.
 * @param title The title of the thesaurus view. Default is "Synonyms".
 * @param modifier A [Modifier] for styling the layout of the thesaurus view.
 * @param onWordClick An optional lambda to handle word click events.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ThesaurusView(
    synonyms: List<String>,
    title: String = "Synonyms",
    modifier: Modifier = Modifier,
    onWordClick: ((String) -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(12.dp)
            .verticalScroll(rememberScrollState()) // Enables scrolling
    ) {
        // Title
        Text(
            text = title, style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ), modifier = Modifier.padding(bottom = 8.dp)
        )

        // Synonyms Flow Row
        FlowRow(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start
        ) {
            // Display a list of synonyms sorted alphabetically
            synonyms.sortedBy { it } // Sort alphabetically
                .forEach { word ->
                    WordChipItem(word = word, onWordClick = { onWordClick?.invoke(word) })
                }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun ThesaurusViewPreview() {
    MaterialTheme {
        ThesaurusView(synonyms = listOf(
            "joyful",
            "content",
            "happy",
            "cheerful",
            "delighted",
            "pleased",
            "glad",
            "merry",
            "jubilant"
        ), onWordClick = { selectedWord ->
            println("Clicked on: $selectedWord")
        })
    }
}
