package com.venom.ui.components.buttons

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.venom.ui.components.common.adp
import com.venom.ui.theme.lingoLens

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
 * @param showBorder Whether to show the border around the button (default true)
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomButton(
    icon: Any,
    contentDescription: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean = false,
    iconSize: Dp = 24.adp,
    iconPadding: Dp = 0.adp,
    selectedTint: Color? = null,
    disabledTint: Color? = null,
    showBorder: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val inactiveGradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.lingoLens.glass.tintPrimary.copy(0.05f),
            MaterialTheme.lingoLens.glass.tintSecondary.copy(0.05f),
            MaterialTheme.lingoLens.glass.tintAccent.copy(0.05f)
        )
    )

    // Memoize shape
    val radius = 12.adp
    val buttonShape = remember(radius) { RoundedCornerShape(radius) }

    // Memoize animation spec
    val scaleAnimSpec = remember {
        spring<Float>(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    }

    val scale by animateFloatAsState(
        targetValue = when {
            !enabled -> 0.95f
            isPressed -> 0.92f
            selected -> 1.05f
            else -> 1f
        },
        animationSpec = scaleAnimSpec
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val onSecondaryColor = MaterialTheme.colorScheme.onSecondary
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    val iconColor by animateColorAsState(
        targetValue = when {
            !enabled -> disabledTint ?: onSurfaceVariant.copy(0.38f)
            selected -> selectedTint ?: onSecondaryColor
            else -> primaryColor
        }
    )

    val borderWidth = 0.5.adp
    val borderModifier = remember(showBorder, buttonShape, borderWidth) {
        if (showBorder) {
            Modifier
                .border(borderWidth, Color.Transparent, buttonShape)
                .background(brush = inactiveGradient, shape = buttonShape)
        } else {
            Modifier
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        TooltipBox(
            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
            tooltip = {
                PlainTooltip {
                    Text(contentDescription)
                }
            },
            state = rememberTooltipState()
        ) {
            IconButton(
                onClick = onClick,
                modifier = Modifier
                    .padding(4.adp)
                    .scale(scale)
                    .then(borderModifier),
                enabled = enabled,
                interactionSource = interactionSource
            ) {
                IconContent(
                    icon = icon,
                    contentDescription = contentDescription,
                    iconColor = iconColor,
                    iconPadding = iconPadding,
                    iconSize = iconSize
                )
            }
        }
    }
}

@Composable
private fun IconContent(
    icon: Any,
    contentDescription: String,
    iconColor: Color,
    iconPadding: Dp,
    iconSize: Dp
) {
    val iconModifier = remember(iconPadding, iconSize) {
        Modifier
            .padding(iconPadding)
            .size(iconSize)
    }

    when (icon) {
        is Int -> Icon(
            painter = painterResource(id = icon),
            contentDescription = contentDescription,
            tint = iconColor.copy(0.9f),
            modifier = iconModifier
        )

        is ImageVector -> Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconColor.copy(0.9f),
            modifier = iconModifier
        )
    }
}