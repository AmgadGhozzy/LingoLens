package com.venom.textsnap.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.venom.ui.components.buttons.CustomButton
import com.venom.ui.components.common.ActionItem
import com.venom.ui.components.menus.ButtonType
import com.venom.ui.components.menus.OverflowMenu

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
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
                maxLines = if (expanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                actions.take(maxActionsVisible).forEach { action ->
                    CustomButton(
                        icon = action.icon,
                        contentDescription = action.description.toString(),
                        onClick = action.onClick
                    )
                }

                if (actions.size > maxActionsVisible) {
                    OverflowMenu(
                        actions.drop(maxActionsVisible),
                        buttonType = ButtonType.OUTLINED
                    )
                }
            }
        }
    }
}