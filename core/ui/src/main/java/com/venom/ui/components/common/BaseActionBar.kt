package com.venom.ui.components.common

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
private fun ActionItemContent(
    action: ActionItem, useTextButtons: Boolean
) {
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
                text = action.text,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun BaseActionBar(
    leftAction: ActionItem? = null,
    actions: List<ActionItem>,
    showOverflowMenu: Boolean = false,
    overflowOptions: List<ActionItem.Action> = emptyList(),
    useCard: Boolean = false,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    elevation: Dp = 2.dp,
    useTextButtons: Boolean = false,
    modifier: Modifier = Modifier
) {
    val content = @Composable {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showOverflowMenu || leftAction != null) {
                Box(modifier = Modifier.weight(1f)) {
                    if (showOverflowMenu) {
                        OverflowMenu(options = overflowOptions, buttonType = ButtonType.OUTLINED)
                    } else {
                        leftAction?.let { ActionItemContent(it, useTextButtons) }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = if (showOverflowMenu) Modifier.padding(end = 16.dp) else Modifier
            ) {
                actions.forEach { ActionItemContent(it, useTextButtons) }
            }
        }
    }

    if (useCard) {
        CustomCard(
            elevation = elevation,
            containerColor = containerColor,
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 6.dp)
        ) { content() }
    } else {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(containerColor)
        ) { content() }
    }
}