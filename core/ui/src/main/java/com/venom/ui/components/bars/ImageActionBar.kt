package com.venom.ui.components.bars

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.venom.resources.R
import com.venom.ui.components.common.ActionItem
import com.venom.ui.components.common.BaseActionBar

@Composable
fun ImageActionBar(
    actions: List<ActionItem.Action>,
    modifier: Modifier = Modifier
) {
    BaseActionBar(
        actions = actions,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        showDropdownMenu = true, useCard = true, modifier = modifier
    )
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
