package com.venom.ui.components.bars

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.venom.ui.components.buttons.CustomFilledIconButton
import com.venom.ui.components.common.ActionItem
import com.venom.ui.components.menus.OverflowMenu
import com.venom.ui.components.other.GlassCard

@Composable
fun ImageActionBar(
    actions: List<ActionItem.Action>,
    modifier: Modifier = Modifier,
    maxActionsVisible: Int = 4
) {
    GlassCard(
        modifier = modifier,
        contentPadding = 6.dp,
        solidBackgroundAlpha = 0.5f
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
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