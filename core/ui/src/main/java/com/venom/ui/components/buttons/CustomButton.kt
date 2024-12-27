package com.venom.ui.components.buttons

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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

@Composable
fun CustomButton(
    icon: Any, // Can be @DrawableRes Int or ImageVector
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean = false,
    iconSize: Dp = 24.dp,
    iconPadding: Dp = 6.dp,
    selectedTint: Color? = null,
    disabledTint: Color? = null
) {
    // Animated color logic with more flexible tinting
    val defaultDisabledColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
    val defaultSelectedColor = MaterialTheme.colorScheme.onSurfaceVariant
    val defaultUnselectedColor = MaterialTheme.colorScheme.primary

    val iconColor by animateColorAsState(
        targetValue = when {
            !enabled -> disabledTint ?: defaultDisabledColor
            selected -> selectedTint ?: defaultSelectedColor
            else -> defaultUnselectedColor
        }, animationSpec = spring(stiffness = Spring.StiffnessLow), label = "Icon Color"
    )

    IconButton(onClick = onClick,
        modifier = modifier.scale(if (selected) 1.1f else 1f),
        enabled = enabled,
        interactionSource = remember { MutableInteractionSource() }) {
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

            else -> throw IllegalArgumentException("Icon must be either a drawable resource ID or an ImageVector")
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun CustomButtonPreview() {
    MaterialTheme {
        CustomButton(
            icon = R.drawable.icon_translate,
            contentDescription = stringResource(R.string.action_translate),
            onClick = {},
            selected = true,
            enabled = true
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun CustomButtonVectorPreview() {
    MaterialTheme {
        CustomButton(
            icon = Icons.Rounded.Add,
            contentDescription = "Add",
            onClick = {},
            selected = false,
            enabled = true
        )
    }
}