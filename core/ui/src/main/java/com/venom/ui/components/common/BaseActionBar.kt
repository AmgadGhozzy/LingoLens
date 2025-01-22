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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.venom.ui.components.buttons.CustomButton
import com.venom.ui.components.buttons.CustomTextButton
import com.venom.ui.components.dialogs.CustomCard
import com.venom.ui.components.menus.DropdownMenuTrigger
import com.venom.ui.components.menus.defaultDropdownOptions

sealed class ActionItem {
    data class Action(
        val icon: Any,
        @StringRes val textRes: Int,
        val onClick: () -> Unit,
        val enabled: Boolean = true,
        val selected: Boolean = false,
        val selectedTint: Color? = null
    ) : ActionItem()

    data class TextAction(val text: String, val onClick: () -> Unit) : ActionItem()
    data class MenuOption(
        val text: String,
        val icon: ImageVector? = null,
        val onClick: () -> Unit
    ) : ActionItem()
}

@Composable
private fun ActionItemContent(
    action: ActionItem,
    useTextButtons: Boolean
) {
    when (action) {
        is ActionItem.Action -> {
            if (useTextButtons) {
                CustomTextButton(
                    onClick = action.onClick,
                    icon = action.icon,
                    text = stringResource(action.textRes),
                    withText = false,
                    enabled = action.enabled
                )
            } else {
                CustomButton(
                    icon = action.icon,
                    contentDescription = stringResource(action.textRes),
                    onClick = action.onClick,
                    selected = action.selected,
                    selectedTint = action.selectedTint,
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

        is ActionItem.MenuOption -> {
            DropdownMenuTrigger(
                options = listOf(action),
                onOptionSelected = { it.onClick() }
            )
        }
    }
}

@Composable
fun BaseActionBar(
    leftAction: ActionItem? = null,
    actions: List<ActionItem>,
    showDropdownMenu: Boolean = false,
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
            if (showDropdownMenu || leftAction != null) {
                Box(modifier = Modifier.weight(1f)) {
                    if (showDropdownMenu) {
                        DropdownMenuTrigger(
                            options = defaultDropdownOptions(),
                            onOptionSelected = { it.onClick() }
                        )
                    } else {
                        leftAction?.let { ActionItemContent(it, useTextButtons) }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = if (showDropdownMenu) Modifier.padding(end = 16.dp) else Modifier
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