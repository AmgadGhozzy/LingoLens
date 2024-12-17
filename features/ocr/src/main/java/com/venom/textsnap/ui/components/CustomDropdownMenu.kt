package com.venom.textsnap.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.venom.textsnap.R

@Composable
fun CustomDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    options: List<DropdownMenuOption>,
    onOptionSelected: (DropdownMenuOption) -> Unit,
    modifier: Modifier = Modifier
) {
    val rollAnimation by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "Roll Animation"
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
                scaleY = rollAnimation, transformOrigin = TransformOrigin(
                    pivotFractionX = 0.5f, pivotFractionY = 0f
                )
            )
    ) {
        options.forEachIndexed { index, option ->
            DropdownMenuItem(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .clip(RoundedCornerShape(12.dp)), onClick = {
                onOptionSelected(option)
                onDismissRequest()
            }, text = {
                Text(
                    text = option.text, style = MaterialTheme.typography.bodyMedium
                )
            }, leadingIcon = option.icon?.let { icon ->
                {
                    Icon(
                        imageVector = icon,
                        contentDescription = option.text,
                        modifier = Modifier.padding(end = 8.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    )
                }
            })

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
    options: List<DropdownMenuOption> = defaultDropdownOptions(),
    onOptionSelected: (DropdownMenuOption) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { isExpanded = !isExpanded }) {
            Icon(
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = stringResource(id = R.string.more_options)
            )
        }

        CustomDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            options = options,
            onOptionSelected = onOptionSelected
        )
    }
}

// Companion function to provide default options
fun defaultDropdownOptions() = listOf(
    DropdownMenuOption("Show Paragraphs", Icons.Filled.LibraryBooks),
    DropdownMenuOption("Show Labels", Icons.Filled.RemoveRedEye),
    DropdownMenuOption("Select All", Icons.Filled.SelectAll),
    DropdownMenuOption("Translate", Icons.Filled.Translate),
    DropdownMenuOption("Share", Icons.Filled.Share),
    DropdownMenuOption("Speak", Icons.Filled.VolumeUp),
    DropdownMenuOption("Copy", Icons.Filled.ContentCopy)
)

// Existing data class
data class DropdownMenuOption(
    val text: String, val icon: ImageVector? = null
)

@Preview(showBackground = true)
@Composable
fun DropdownMenuTriggerPreview() {
    DropdownMenuTrigger(onOptionSelected = { selectedOption -> })
}