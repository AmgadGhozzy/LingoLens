package com.venom.ui.components.menus

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomButton
import com.venom.ui.components.buttons.CustomFilledIconButton
import com.venom.ui.components.common.ActionItem
import com.venom.ui.components.common.adp

enum class ButtonType { FILLED_ICON, OUTLINED }

@Composable
fun OverflowMenu(
    options: List<ActionItem.Action>,
    modifier: Modifier = Modifier,
    buttonType: ButtonType = ButtonType.FILLED_ICON
) {
    var expanded by remember { mutableStateOf(false) }
    val desc = stringResource(R.string.action_more_options)

    Box(modifier) {
        when (buttonType) {
            ButtonType.FILLED_ICON -> CustomFilledIconButton(
                icon = Icons.Rounded.MoreHoriz,
                contentDescription = desc,
                onClick = { expanded = !expanded },
                iconSize = 32.adp
            )
            ButtonType.OUTLINED -> CustomButton(
                icon = Icons.Rounded.MoreVert,
                contentDescription = desc,
                onClick = { expanded = !expanded },
                iconSize = 32.adp,
                showBorder = false
            )
        }

        AnimatedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            options = options
        )
    }
}