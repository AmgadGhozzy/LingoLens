package com.venom.textsnap.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.venom.ui.components.buttons.CustomButton
import com.venom.ui.components.common.ActionItem
import com.venom.ui.components.menus.ButtonType
import com.venom.ui.components.menus.OverflowMenu

/**
 * Displays selected text with action buttons.
 *
 * @param text The text to be displayed.
 * @param actions A list of actions available for the selected text.
 * @param expanded Determines whether the text should be shown fully expanded.
 * @param modifier An optional modifier to be applied to the component.
 * @param maxActionsVisible The maximum number of actions to display before showing an overflow menu.
 * @param backgroundColor The background color of the component.
 */
@Composable
fun SelectedTextItem(
    text: String,
    actions: List<ActionItem.Action>,
    expanded: Boolean = false,
    maxActionsVisible: Int = 2,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = backgroundColor,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                maxLines = if (expanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )

            actions.take(maxActionsVisible).forEach { action ->
                CustomButton(
                    icon = action.icon,
                    contentDescription = action.description.toString(),
                    onClick = action.onClick,
                )
            }
            if (actions.size > maxActionsVisible) OverflowMenu(
                actions.drop(maxActionsVisible),
                buttonType = ButtonType.OUTLINED
            )
        }
    }
}
