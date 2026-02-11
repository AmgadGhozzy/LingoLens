package com.venom.ui.components.common

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.venom.ui.components.buttons.CustomButton
import com.venom.ui.components.buttons.CustomTextButton
import com.venom.ui.components.dialogs.CustomCard
import com.venom.ui.components.menus.ButtonType
import com.venom.ui.components.menus.OverflowMenu

sealed class ActionItem {
    data class Action(
        val icon: Any,
        @StringRes val description: Int,
        val onClick: () -> Unit,
        val enabled: Boolean = true,
        val selected: Boolean = false
    ) : ActionItem()

    data class TextAction(val text: String, val onClick: () -> Unit) : ActionItem()
}

@Composable
fun BaseActionBar(
    actions: List<ActionItem>,
    modifier: Modifier = Modifier,
    leftAction: ActionItem? = null,
    showOverflowMenu: Boolean = false,
    overflowOptions: List<ActionItem.Action> = emptyList(),
    useCard: Boolean = false,
    containerColor: Color = Color.Unspecified,
    elevation: Dp = 2.adp,
    useTextButtons: Boolean = false,
    trailingContent: @Composable (() -> Unit)? = null
) {
    val content = @Composable {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.adp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showOverflowMenu || leftAction != null) {
                Box(Modifier.weight(1f)) {
                    if (showOverflowMenu) {
                        OverflowMenu(overflowOptions, ButtonType.OUTLINED)
                    } else {
                        leftAction?.let { ActionItemView(it, useTextButtons) }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = if (showOverflowMenu) Modifier.padding(end = 16.adp) else Modifier
            ) {
                actions.forEach { ActionItemView(it, useTextButtons) }
            }

            trailingContent?.invoke()
        }
    }

    if (useCard) {
        CustomCard(
            elevation = elevation,
            containerColor = containerColor,
            modifier = modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = 6.adp)
        ) { content() }
    } else {
        Box(modifier.fillMaxWidth().background(containerColor)) { content() }
    }
}

@Composable
private fun ActionItemView(action: ActionItem, useTextButtons: Boolean) {
    when (action) {
        is ActionItem.Action -> {
            if (useTextButtons) {
                CustomTextButton(
                    onClick = action.onClick,
                    icon = action.icon,
                    text = stringResource(action.description),
                    withText = false,
                    enabled = action.enabled
                )
            } else {
                CustomButton(
                    icon = action.icon,
                    contentDescription = stringResource(action.description),
                    onClick = action.onClick,
                    selected = action.selected,
                    enabled = action.enabled
                )
            }
        }
        is ActionItem.TextAction -> {
            Text(
                action.text,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}