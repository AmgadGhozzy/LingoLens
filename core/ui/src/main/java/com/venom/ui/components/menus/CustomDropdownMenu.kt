package com.venom.ui.components.menus

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.components.bars.ActionItem
import com.venom.ui.components.buttons.CustomButton

@Composable
fun CustomDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    options: List<ActionItem.DropdownMenuOption>,
    modifier: Modifier = Modifier
) {
    val rollAnimation by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = tween(durationMillis = 600), label = "Roll Animation"
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        offset = DpOffset(x = 0.dp, y = 8.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.surface)
            .alpha(rollAnimation)
            .graphicsLayer(
                scaleY = rollAnimation,
                transformOrigin = TransformOrigin(0.5f, 0f)
            )
    ) {
        options.forEachIndexed { index, option ->
            DropdownMenuItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(12.dp)),
                onClick = {
                    option.onClick()
                    onDismissRequest()
                },
                text = {
                    Text(text = option.text, style = MaterialTheme.typography.bodyMedium)
                },
                leadingIcon = option.icon?.let { icon ->
                    {
                        Icon(
                            imageVector = icon,
                            contentDescription = option.text,
                            modifier = Modifier.padding(end = 8.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                        )
                    }
                }
            )
            if (index < options.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .alpha(0.5f)
                )
            }
        }
    }
}

@Composable
fun DropdownMenuTrigger(
    options: List<ActionItem.DropdownMenuOption> = defaultDropdownOptions(),
    onOptionSelected: (ActionItem.DropdownMenuOption) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box {
        CustomButton(
            icon = Icons.Rounded.MoreVert,
            contentDescription = stringResource(id = R.string.action_more_options),
            onClick = { isExpanded = !isExpanded },
            iconSize = 32.dp
        )
        CustomDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            options = options
        )
    }
}

fun defaultDropdownOptions() = listOf(
    ActionItem.DropdownMenuOption("Show Paragraphs", Icons.AutoMirrored.Filled.LibraryBooks) {},
    ActionItem.DropdownMenuOption("Show Labels", Icons.Filled.RemoveRedEye) {},
    ActionItem.DropdownMenuOption("Select All", Icons.Filled.SelectAll) {},
    ActionItem.DropdownMenuOption("Translate", Icons.Filled.Translate) {},
    ActionItem.DropdownMenuOption("Share", Icons.Filled.Share) {},
    ActionItem.DropdownMenuOption("Speak", Icons.AutoMirrored.Filled.VolumeUp) {},
    ActionItem.DropdownMenuOption("Copy", Icons.Filled.ContentCopy) {}
)

@Preview(showBackground = true)
@Composable
fun DropdownMenuTriggerPreview() {
    DropdownMenuTrigger(onOptionSelected = { selectedOption -> selectedOption.onClick() })
}