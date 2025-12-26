package com.venom.textsnap.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.venom.ui.components.buttons.CustomButton
import com.venom.ui.components.common.ActionItem
import com.venom.ui.components.menus.ButtonType
import com.venom.ui.components.menus.OverflowMenu
import com.venom.ui.components.other.GlassCard

@Composable
fun SelectedTextItem(
    text: String,
    actions: List<ActionItem.Action>,
    expanded: Boolean = false,
    maxActionsVisible: Int = 2,
    modifier: Modifier = Modifier,
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        solidBackgroundAlpha = 0.1f,
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 8.dp, start = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f),
                maxLines = if (expanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                actions.take(maxActionsVisible).forEach { action ->
                    CustomButton(
                        icon = action.icon,
                        contentDescription = action.description.toString(),
                        onClick = action.onClick,
                        showBorder = false
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