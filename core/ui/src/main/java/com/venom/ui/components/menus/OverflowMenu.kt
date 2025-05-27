package com.venom.ui.components.menus

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomButton
import com.venom.ui.components.buttons.CustomFilledIconButton
import com.venom.ui.components.common.ActionItem

@Composable
fun OverflowMenu(
    options: List<ActionItem.Action>,
    modifier: Modifier = Modifier,
    buttonType: ButtonType = ButtonType.FILLED_ICON
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        when (buttonType) {
            ButtonType.FILLED_ICON -> CustomFilledIconButton(
                icon = Icons.Rounded.MoreHoriz,
                contentDescription = stringResource(R.string.action_more_options),
                onClick = { isExpanded = !isExpanded },
                iconSize = 32.dp
            )

            ButtonType.OUTLINED -> CustomButton(
                icon = Icons.Rounded.MoreVert,
                contentDescription = stringResource(R.string.action_more_options),
                onClick = { isExpanded = !isExpanded },
                iconSize = 32.dp
            )
        }

        AnimatedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            options = options
        )
    }
}

enum class ButtonType {
    FILLED_ICON, OUTLINED
}