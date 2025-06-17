package com.venom.ui.components.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.ui.theme.LingoLensTheme

@Composable
fun WordChip(
    word: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isAlpha: Boolean = true
) {
    SuggestionChip(
        onClick = onClick,
        modifier = modifier.then(
            Modifier
                .background(
                    color = if (isAlpha) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    else MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)
                )
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ),
        label = {
            Text(
                text = word,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isAlpha) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onTertiaryContainer
                )
            )
        }
    )
}

@Preview
@Composable
private fun WordChipPreview() {
    LingoLensTheme() {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)) {
                WordChip(word = "Example", onClick = {}, isAlpha = true)
                WordChip(word = "Example", onClick = {}, isAlpha = false)
            }
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)) {
                WordChip(word = "Example", onClick = {}, isAlpha = true)
                WordChip(word = "Example", onClick = {}, isAlpha = false)
            }
        }
    }
}