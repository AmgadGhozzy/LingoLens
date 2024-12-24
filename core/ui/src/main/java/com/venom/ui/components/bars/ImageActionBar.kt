package com.venom.ui.components.bars

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomButton
import com.venom.ui.components.dialogs.CustomCard
import com.venom.ui.components.menus.DropdownMenuTrigger

@Composable
fun ImageActionBar(
    actions: List<ActionItem.Action>,
    modifier: Modifier = Modifier,
) {
    CustomCard (
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 6.dp)
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .padding(end = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            DropdownMenuTrigger(onOptionSelected = { it.onClick() })
            actions.forEach { action ->
                CustomButton(
                    icon = action.icon,
                    contentDescription = stringResource(action.textRes),
                    onClick = action.onClick,
                    enabled = action.enabled,
                    selected = action.selected
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ImageActionBarPreview() {
    ImageActionBar(
        actions = listOf(
            ActionItem.Action(
                icon = R.drawable.ic_paragraph_on,
                textRes = R.string.action_hide_paragraphs,
                onClick = {},
            ), ActionItem.Action(
                icon = R.drawable.ic_labels_shown,
                textRes = R.string.action_hide_labels,
                onClick = {},
                selected = true
            ), ActionItem.Action(
                icon = R.drawable.ic_select_all,
                textRes = R.string.select_language,
                onClick = {},
                enabled = false,
            ), ActionItem.Action(
                icon = R.drawable.icon_translate,
                textRes = R.string.action_translate,
                onClick = {},
            )
        )
    )
}
