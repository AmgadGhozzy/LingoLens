package com.venom.ui.components.buttons

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
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
 * @param selected Whether the button is in selected state
 * @param shape Shape of the button (defaults to Material 3 spec)
 * @param colors IconButtonColors for the button
 * @param interactionSource Optional interaction source for the button
 * @param size Size of the button
 * @param iconSize Size of the icon
 * @param iconPadding Padding around the icon
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomFilledIconButton(
    icon: Any,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean = false,
    isAlpha: Boolean = false,
    shape: Shape = CircleShape,
    color: Color? = null,
    colors: IconButtonColors = selectedFilledIconButtonColors(
        isSelected = selected,
        color = color,
        isAlpha = isAlpha
    ),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    size: Dp = 48.dp,
    iconSize: Dp = 24.dp,
    iconPadding: Dp = 0.dp
) {

    Box(modifier = modifier) {
        TooltipBox(
            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
            tooltip = {
                PlainTooltip(caretSize = TooltipDefaults.caretSize) { Text(contentDescription) }
            },
            state = rememberTooltipState()
        ) {
            FilledIconButton(
                onClick = { if (enabled) onClick() },
                modifier = modifier
                    .padding(4.dp)
                    .scale(if (selected && enabled) 1.1f else 1f)
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
                }
            }
        }
    }
}

@Composable
fun selectedFilledIconButtonColors(
    color: Color? = null,
    isAlpha: Boolean = false,
    isSelected: Boolean = false,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    containerColorAlpha: Color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.1f),
    contentColorAlpha: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    selectedContainerColor: Color = MaterialTheme.colorScheme.primary,
    selectedContentColor: Color = MaterialTheme.colorScheme.onPrimary,
    disabledContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    disabledContentColor: Color = MaterialTheme.colorScheme.onSurface.copy(0.38f),
) = IconButtonDefaults.filledIconButtonColors(
    containerColor = when {
        isSelected -> selectedContainerColor
        isAlpha -> containerColorAlpha
        color != null -> color.copy(0.2f)
        else -> containerColor
    },
    contentColor = when {
        isSelected -> selectedContentColor
        isAlpha -> contentColorAlpha
        color != null -> color
        else -> contentColor
    },
    disabledContainerColor = disabledContainerColor,
    disabledContentColor = disabledContentColor
)

@Preview
@Composable
private fun CustomFilledIconButtonPreview() {
    MaterialTheme {
        CustomFilledIconButton(
            icon = Icons.Rounded.Add,
            contentDescription = "Add",
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun CustomFilledIconButtonSelectedPreview() {
    MaterialTheme {
        CustomFilledIconButton(
            icon = Icons.Rounded.Add,
            contentDescription = "Add",
            onClick = {},
            selected = true
        )
    }
}