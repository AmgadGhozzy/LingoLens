package com.venom.settings.presentation.components.dialogs

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.venom.domain.model.ThemeColor
import com.venom.domain.model.generateThemeColors
import com.venom.resources.R

@Composable
fun ColorPickerDialog(
    onColorSelected: (color: ThemeColor) -> Unit,
    defaultColors: List<ThemeColor> = generateThemeColors(),
    initialColor: ThemeColor? = null,
    onDismiss: () -> Unit
) {
    var selectedColor by remember { mutableStateOf(initialColor) }

    BaseDialog(
        title = stringResource(id = R.string.select_color),
        onDismiss = onDismiss,
        confirmText = stringResource(id = R.string.action_select),
        onConfirm = {
            selectedColor?.let { onColorSelected(it) }
            onDismiss()
        },
        showConfirmButton = selectedColor != null
    ) {
        ColorGrid(
            colors = defaultColors,
            selectedColor = selectedColor,
            onColorSelected = { selectedColor = it }
        )
    }
}

@Composable
private fun ColorGrid(
    colors: List<ThemeColor>,
    selectedColor: ThemeColor?,
    onColorSelected: (ThemeColor) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp),
    ) {
        items(colors.take(15)) { color ->
            ColorItem(
                color = Color(color.color),
                isSelected = selectedColor?.color == color.color,
                onClick = { onColorSelected(color) }
            )
        }
    }
}

@Composable
private fun ColorItem(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val animatedColor by animateColorAsState(
        targetValue = color,
        label = "colorAnimation"
    )

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .size(12.dp)
            .clip(MaterialTheme.shapes.large)
            .background(animatedColor)
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Unspecified,
                shape = MaterialTheme.shapes.large
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = if (color.luminance() > 0.5f) Color.Black else Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
