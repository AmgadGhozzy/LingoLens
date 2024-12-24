package com.venom.ui.components.bars

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomTextButton
import com.venom.ui.components.dialogs.CustomCard
import com.venom.ui.components.menus.DropdownMenuTrigger

/**
 * A Material 3 action bar component that displays text-related actions.
 *
 * @param actions List of [ActionItem]s to be displayed in the action bar
 * @param modifier Modifier for the action bar
 * @param containerColor Background color of the action bar
 * @param elevation Elevation of the action bar
 */
@Composable
fun TextActionBar(
    actions: List<ActionItem.Action>,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    elevation: Dp = 2.dp
) {
    CustomCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .padding(end = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DropdownMenuTrigger(onOptionSelected = { it.onClick() })
            actions.forEach { action ->
                CustomTextButton(
                    onClick = action.onClick,
                    icon = action.icon,
                    text = stringResource(action.textRes),
                    withText = false,
                    enabled = action.enabled
                )
            }
        }
    }
}

sealed class ActionItem {
    data class Action(
        val icon: Any, // Can be @DrawableRes Int or ImageVector
        @StringRes val textRes: Int,
        val onClick: () -> Unit,
        val enabled: Boolean = true,
        val selected: Boolean = false
    ) : ActionItem()

    data class DropdownMenuOption(
        val text: String, val icon: ImageVector? = null, val onClick: () -> Unit
    ) : ActionItem()
}

@Preview
@Composable
fun TextActionBarPreview() {
    TextActionBar(
        actions = listOf(
            ActionItem.Action(icon = R.drawable.icon_share,
                textRes = R.string.action_share,
                onClick = { }),
            ActionItem.Action(icon = R.drawable.icon_sound,
                textRes = R.string.action_speak,
                onClick = { }),
            ActionItem.Action(icon = R.drawable.icon_translate,
                textRes = R.string.action_translate,
                onClick = { }),
            ActionItem.Action(icon = R.drawable.icon_copy,
                textRes = R.string.action_copy,
                onClick = { }),
        )
    )
}
