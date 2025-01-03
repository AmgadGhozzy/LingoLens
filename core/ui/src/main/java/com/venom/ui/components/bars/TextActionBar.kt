package com.venom.ui.components.bars

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.components.common.ActionItem
import com.venom.ui.components.common.BaseActionBar

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
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    elevation: Dp = 2.dp
) {
    BaseActionBar(
        actions = actions,
        showDropdownMenu = true,
        useCard = true,
        containerColor = containerColor,
        elevation = elevation,
        useTextButtons = true,
        modifier = modifier
    )
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
