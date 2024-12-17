package com.venom.textsnap.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.textsnap.R

@Composable
fun ImageActionBar(
    modifier: Modifier = Modifier,
    showLabels: Boolean,
    isParageraphMode: Boolean,
    isSelected: Boolean,
    isTranslate: Boolean,
    onToggleSelected: () -> Unit,
    onToggleLabels: () -> Unit,
    onToggleParagraphs: () -> Unit,
    onTranslateClick: () -> Unit,
    onMoreOptionsClick: () -> Unit = {}
) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp,horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // More Options Button with Dropdown
            DropdownMenuTrigger(onOptionSelected = { selectedOption ->
                when (selectedOption.text) {
                    "Show Paragraphs" -> {
                    }

                    "Show Labels" -> {
                    }

                    "Select All" -> {
                    }

                    "Translate" -> {
                    }

                    "Share" -> {
                    }

                    "Speak" -> {
                    }
                }
            }

            )

            // Paragraphs Toggle
            ActionBarIcon(
                icon = if (isParageraphMode) R.drawable.ic_paragraph_on else R.drawable.ic_paragraph_off,
                contentDescription = if (isParageraphMode) stringResource(R.string.hide_paragraphs)
                else stringResource(R.string.show_paragraphs),
                onClick = onToggleParagraphs,
                selected = isParageraphMode
            )

            // Labels Toggle
            ActionBarIcon(
                icon = if (showLabels) R.drawable.ic_labels_shown else R.drawable.ic_labels_hidden,
                contentDescription = if (showLabels) stringResource(R.string.hide_labels)
                else stringResource(R.string.show_labels),
                onClick = onToggleLabels,
                selected = showLabels
            )

            // Select/Deselect Toggle
            ActionBarIcon(
                icon = if (isSelected) R.drawable.ic_select_all else R.drawable.ic_deselect,
                contentDescription = if (isSelected) stringResource(R.string.select_all)
                else stringResource(R.string.deselect_all),
                onClick = onToggleSelected,
                selected = isSelected
            )

            // Translate Action
            ActionBarIcon(
                icon = R.drawable.ic_translate,
                contentDescription = stringResource(R.string.action_translate),
                onClick = onTranslateClick,
                enabled = isTranslate
            )
        }

    }
}

@Composable
private fun ActionBarIcon(
    @DrawableRes icon: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean = false
) {
    // Animated color and scale
    val iconColor by animateColorAsState(
        targetValue = when {
            !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            selected -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        }, animationSpec = spring(stiffness = Spring.StiffnessLow), label = "icon-color-animation"
    )

    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .size(40.dp)
            .scale(if (selected) 1.1f else 1f)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = contentDescription,
            tint = iconColor,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ImageActionBarPreview() {
    ImageActionBar(showLabels = true,
        isParageraphMode = true,
        isTranslate = true,
        isSelected = true,
        onToggleSelected = {},
        onToggleLabels = {},
        onToggleParagraphs = {},
        onTranslateClick = {},
        onMoreOptionsClick = {})
}

@Preview(showBackground = true)
@Composable
fun ImageActionBarPreviewDisabled() {
    ImageActionBar(showLabels = false,
        isParageraphMode = false,
        isTranslate = false,
        isSelected = false,
        onToggleSelected = {},
        onToggleLabels = {},
        onToggleParagraphs = {},
        onTranslateClick = {},
        onMoreOptionsClick = {})
}