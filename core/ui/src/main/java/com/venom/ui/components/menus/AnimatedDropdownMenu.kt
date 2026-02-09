package com.venom.ui.components.menus

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import com.venom.ui.components.common.ActionItem
import com.venom.ui.components.common.adp

@Composable
fun AnimatedDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    options: List<ActionItem.Action>,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.adp, 8.adp),
    shape: Shape = RoundedCornerShape(12.adp),
    customContent: (@Composable (Int, ActionItem.Action) -> Unit)? = null
) {
    val progress by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = tween(400)
    )
    val colors = MaterialTheme.colorScheme

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        offset = offset,
        shape = shape,
        modifier = modifier
            .background(
                Brush.linearGradient(
                    listOf(
                        colors.primary.copy(0.05f),
                        colors.secondary.copy(0.05f),
                        colors.tertiary.copy(0.05f)
                    )
                ), shape
            )
            .alpha(progress)
            .graphicsLayer {
                scaleY = progress
                transformOrigin = TransformOrigin(0f, 0f)
            }
    ) {
        options.forEachIndexed { index, action ->
            DropdownMenuItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.adp)
                    .clip(RoundedCornerShape(12.adp)),
                onClick = {
                    action.onClick()
                    onDismissRequest()
                },
                text = {
                    if (customContent != null) customContent(index, action)
                    else Text(stringResource(action.description), style = MaterialTheme.typography.bodyMedium)
                },
                leadingIcon = if (customContent == null) {
                    { ActionIcon(action, colors.primary.copy(0.8f)) }
                } else null
            )

            if (index < options.lastIndex) {
                GradientDivider(colors.outline.copy(0.2f))
            }
        }
    }
}

@Composable
private fun ActionIcon(action: ActionItem.Action, tint: Color) {
    val desc = stringResource(action.description)
    when (val icon = action.icon) {
        is ImageVector -> Icon(icon, desc, tint = tint)
        is Int -> Icon(painterResource(icon), desc, tint = tint)
    }
}

@Composable
private fun GradientDivider(color: Color) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(1.adp)
            .background(
                Brush.horizontalGradient(
                    listOf(Color.Transparent, color, Color.Transparent)
                )
            )
    )
}