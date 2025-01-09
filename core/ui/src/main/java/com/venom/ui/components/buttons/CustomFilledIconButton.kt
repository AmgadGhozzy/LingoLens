package com.venom.ui.components.buttons

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * An enhanced Material 3 FilledIconButton that supports both resource and vector icons
 * with customizable styling and animations
 *
 * @param icon Either a drawable resource ID (Int) or ImageVector
 * @param contentDescription Accessibility description for the icon
 * @param onClick Callback invoked when button is clicked
 * @param modifier Modifier for the button
 * @param enabled Whether the button is enabled
 * @param shape Shape of the button (defaults to Material 3 spec)
 * @param colors IconButtonColors for the button
 * @param interactionSource Optional interaction source for the button
 * @param selected Whether the button is in selected state
 * @param size Size of the button
 * @param iconSize Size of the icon
 * @param iconPadding Padding around the icon
 */
@Composable
fun CustomFilledIconButton(
    icon: Any,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = IconButtonDefaults.filledShape,
    colors: IconButtonColors = IconButtonDefaults.filledIconButtonColors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    selected: Boolean = false,
    size: Dp = 48.dp,
    iconSize: Dp = 24.dp,
    iconPadding: Dp = 8.dp
) {
    val scale = if (selected) 1.1f else 1f

    FilledIconButton(
        onClick = onClick,
        modifier = modifier
            .scale(scale)
            .size(size),
        enabled = enabled,
        shape = shape,
        colors = colors,
        interactionSource = interactionSource
    ) {
        when (icon) {
            is Int -> Icon(
                painter = painterResource(id = icon),
                contentDescription = contentDescription,
                modifier = Modifier
                    .padding(iconPadding)
                    .size(iconSize)
            )

            is ImageVector -> Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier
                    .padding(iconPadding)
                    .size(iconSize)
            )

            else -> throw IllegalArgumentException("Icon must be either a drawable resource ID or an ImageVector")
        }
    }
}

// Custom color scheme for selected state
@Composable
fun selectedFilledIconButtonColors(
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    disabledContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    disabledContentColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
) = IconButtonDefaults.filledIconButtonColors(
    containerColor = containerColor,
    contentColor = contentColor,
    disabledContainerColor = disabledContainerColor,
    disabledContentColor = disabledContentColor
)

@Preview(showBackground = true)
@Composable
private fun EnhancedFilledIconButtonPreview() {
    MaterialTheme {
        CustomFilledIconButton(icon = Icons.Rounded.Add, contentDescription = "Add", onClick = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun EnhancedFilledIconButtonSelectedPreview() {
    MaterialTheme {
        CustomFilledIconButton(
            icon = Icons.Rounded.Add,
            contentDescription = "Add",
            onClick = {},
            selected = true,
            colors = selectedFilledIconButtonColors()
        )
    }
}