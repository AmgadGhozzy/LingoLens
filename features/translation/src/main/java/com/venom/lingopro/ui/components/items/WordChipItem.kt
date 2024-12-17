package com.venom.lingopro.ui.components.items

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A composable function that creates a clickable word chip.
 * The word is displayed inside a rounded chip with a background color and padding.
 *
 * @param word The word to display inside the chip.
 * @param onWordClick A lambda that will be triggered when the word chip is clicked.
 */
@Composable
fun WordChipItem(
    word: String, onWordClick: () -> Unit
) {
    // Define constants for padding and font size to avoid magic numbers
    val horizontalPadding = 7.dp
    val verticalPadding = 3.dp
    val fontSize = 12.sp

    Text(
        text = word,
        style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSecondary, fontSize = fontSize
        ),
        modifier = Modifier
            .padding(3.dp)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current,
                onClick = onWordClick
            )
            .padding(
                horizontal = horizontalPadding, vertical = verticalPadding
            )
    )
}

@Preview
@Composable
fun PreviewWordChipItem() {
    WordChipItem(word = "Word", onWordClick = {})
}
