package com.venom.textsnap.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomButton
import com.venom.ui.components.menus.DropdownMenuTrigger

/**
 * Represents the actions available for selected text
 */
sealed class TextAction {
    data object Copy : TextAction()
    data object Share : TextAction()
    data object Speak : TextAction()
}

/**
 * A card component that displays selected text with action buttons.
 *
 * @param text The text to be displayed
 * @param onActionSelected Callback for when an action is selected
 * @param expanded Whether the text should be fully expanded
 * @param modifier Optional modifier for the component
 */
@Composable
fun SelectedTextItem(
    text: String,
    onActionSelected: (TextAction) -> Unit,
    expanded: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .animateContentSize()
            .padding(6.dp)
    ) {
        SelectedTextContent(
            text = text,
            expanded = expanded,
            onActionSelected = onActionSelected
        )
    }
}

@Composable
private fun SelectedTextContent(
    text: String,
    expanded: Boolean,
    onActionSelected: (TextAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Text content
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .weight(1f)
                .padding(12.dp),
            maxLines = if (expanded) Int.MAX_VALUE else 2,
            overflow = TextOverflow.Ellipsis
        )

        // Action buttons
        ActionButtons(onActionSelected = onActionSelected)
    }
}

@Composable
private fun ActionButtons(
    onActionSelected: (TextAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 4.dp)
    ) {
        // Copy button
        CustomButton(
            icon = R.drawable.icon_copy,
            contentDescription = stringResource(R.string.action_copy),
            onClick = { onActionSelected(TextAction.Copy) }
        )

        // More options dropdown
        DropdownMenuTrigger(
            onOptionSelected = { option ->
                when (option.text) {
                    "share" -> onActionSelected(TextAction.Share)
                    "speak" -> onActionSelected(TextAction.Speak)
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectedTextItemPreview() {
    SelectedTextItem(
        text = "This is a sample text for preview purposes that might be longer than two lines to demonstrate text overflow",
        onActionSelected = {},
        expanded = true
    )
}
