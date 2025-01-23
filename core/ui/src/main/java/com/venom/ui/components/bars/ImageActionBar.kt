package com.venom.ui.components.bars

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomFilledIconButton
import com.venom.ui.components.menus.OverflowMenu

data class ActionItem(
    val icon: Any,
    @StringRes val description: Int,
    val onClick: () -> Unit,
    val selected: Boolean = false,
    val isEnabled: Boolean = true
)

@Composable
fun ImageActionBar(
    actions: List<ActionItem>,
    modifier: Modifier = Modifier,
    maxActionsVisible: Int = 4,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer,
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = backgroundColor,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (actions.size > maxActionsVisible) OverflowMenu(actions.drop(maxActionsVisible))
            actions.take(maxActionsVisible).forEach { action ->
                CustomFilledIconButton(
                    icon = action.icon,
                    size = 42.dp,
                    contentDescription = action.description.toString(),
                    onClick = action.onClick,
                    enabled = action.isEnabled,
                    selected = action.selected,
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
            ActionItem(
                icon = R.drawable.ic_paragraph_on,
                onClick = {},
                description = R.string.action_hide_paragraphs,
            ), ActionItem(
                icon = R.drawable.ic_labels_shown,
                onClick = {},
                description = R.string.action_hide_labels,
                isEnabled = true
            ), ActionItem(
                icon = R.drawable.ic_select_all,
                onClick = {},
                description = R.string.select_language,
                isEnabled = false
            ), ActionItem(
                icon = R.drawable.icon_translate,
                onClick = {},
                description = R.string.action_translate,
            )
        )
    )
}
