package com.venom.ui.components.menus

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.venom.ui.components.common.ActionItem

@Composable
fun AnimatedDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    options: List<ActionItem.Action>,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(x = 0.dp, y = 8.dp),
    shape: Shape = RoundedCornerShape(12.dp),
    customContent: (@Composable (index: Int, action: ActionItem.Action) -> Unit)? = null
) {
    val rollAnimation by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "Roll Animation"
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        offset = offset,
        shape = shape,
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.surface)
            .alpha(rollAnimation)
            .graphicsLayer(
                scaleY = rollAnimation,
                transformOrigin = TransformOrigin(0.5f, 0f)
            )
    ) {
        options.forEachIndexed { index, action ->
            DropdownMenuItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(12.dp)),
                onClick = {
                    action.onClick()
                    onDismissRequest()
                },
                text = {
                    if (customContent != null) {
                        customContent(index, action)
                    } else {
                        Text(
                            text = stringResource(action.description),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                leadingIcon = if (customContent == null) {
                    {
                        when (val iconSource = action.icon) {
                            is ImageVector -> Icon(
                                imageVector = iconSource,
                                contentDescription = stringResource(action.description),
                                modifier = Modifier.padding(end = 8.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                            )

                            is Int -> Icon(
                                painter = painterResource(id = iconSource),
                                contentDescription = stringResource(action.description),
                                modifier = Modifier.padding(end = 8.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                            )

                            else -> throw IllegalArgumentException("Icon must be either a drawable resource ID or an ImageVector")
                        }
                    }
                } else null
            )

            // Add divider between items (except after the last item)
            if (index < options.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}