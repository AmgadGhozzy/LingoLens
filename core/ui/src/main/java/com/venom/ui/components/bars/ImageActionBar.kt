package com.venom.ui.components.bars

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.venom.ui.components.buttons.CustomFilledIconButton
import com.venom.ui.components.common.ActionItem
import com.venom.ui.components.menus.OverflowMenu

@Composable
fun ImageActionBar(
    actions: List<ActionItem.Action>,
    modifier: Modifier = Modifier,
    maxActionsVisible: Int = 4,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer,
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = backgroundColor,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (actions.size > maxActionsVisible) {
                OverflowMenu(actions.drop(maxActionsVisible))
            }

            actions.take(maxActionsVisible).forEach { action ->
                CustomFilledIconButton(
                    icon = action.icon,
                    size = 42.dp,
                    contentDescription = action.description.toString(),
                    onClick = action.onClick,
                    enabled = action.enabled,
                    selected = action.selected,
                )
            }
        }
    }
}