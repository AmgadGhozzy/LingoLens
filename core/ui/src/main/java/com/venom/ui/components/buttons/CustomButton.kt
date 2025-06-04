package com.venom.ui.components.buttons

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.venom.resources.R

/**
 * A modern, animated, and customizable action bar icon component
 *
 * @param icon Drawable resource ID or ImageVector for the icon
 * @param contentDescription Accessibility content description
 * @param onClick Click handler for the icon
 * @param modifier Optional modifier for additional styling
 * @param enabled Whether the icon is interactive
 * @param selected Whether the icon is in a selected state
 * @param iconSize Size of the icon (default 24.dp)
 * @param iconPadding Padding around the icon (default 8.dp)
 * @param selectedTint Custom color when selected
 * @param disabledTint Custom color when disabled
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomButton(
    icon: Any, // Can be @DrawableRes Int or ImageVector
    contentDescription: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean = false,
    iconSize: Dp = 24.dp,
    iconPadding: Dp = 6.dp,
    selectedTint: Color? = null,
    disabledTint: Color? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else if (selected) 1.1f else 1f,
        label = "button_scale"
    )

    val iconColor by animateColorAsState(
        targetValue = when {
            !enabled -> disabledTint
                ?: MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)

            selected -> selectedTint ?: MaterialTheme.colorScheme.onSecondary
            else -> MaterialTheme.colorScheme.primary
        },
        label = "icon_color"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        TooltipBox(
            modifier = modifier,
            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
            tooltip = {
                PlainTooltip(caretSize = TooltipDefaults.caretSize) {
                    Text(contentDescription)
                }
            },
            state = rememberTooltipState()
        ) {
            IconButton(
                onClick = onClick,
                modifier = Modifier.scale(scale),
                enabled = enabled,
                interactionSource = interactionSource
            ) {
                when (icon) {
                    is Int -> Icon(
                        painter = painterResource(id = icon),
                        contentDescription = contentDescription,
                        tint = iconColor,
                        modifier = Modifier
                            .padding(iconPadding)
                            .size(iconSize)
                    )

                    is ImageVector -> Icon(
                        imageVector = icon,
                        contentDescription = contentDescription,
                        tint = iconColor,
                        modifier = Modifier
                            .padding(iconPadding)
                            .size(iconSize)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun CustomButtonPreview() {
    MaterialTheme {
        CustomButton(
            icon = R.drawable.icon_translate,
            contentDescription = stringResource(R.string.action_translate),
            selected = true
        )
    }
}

@Preview
@Composable
fun CustomButtonVectorPreview() {
    MaterialTheme {
        CustomButton(
            icon = Icons.Rounded.Add,
            contentDescription = "Add"
        )
    }
}