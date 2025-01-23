package com.venom.ui.components.menus

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.components.bars.ActionItem
import com.venom.ui.components.buttons.CustomButton

@Composable
fun OverflowMenu(
    hiddenActions: List<ActionItem>, modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    val rollAnimation by animateFloatAsState(
        targetValue = if (isExpanded) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "Roll Animation"
    )

    Box(modifier = modifier) {
        CustomButton(
            icon = Icons.Rounded.MoreVert,
            contentDescription = stringResource(R.string.action_more_options),
            onClick = { isExpanded = !isExpanded },
            iconSize = 32.dp
        )
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            offset = DpOffset(x = 0.dp, y = 8.dp),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.surface)
                .alpha(rollAnimation)
                .graphicsLayer(
                    scaleY = rollAnimation, transformOrigin = TransformOrigin(0.5f, 0f)
                )
        ) {
            hiddenActions.forEachIndexed { index, action ->
                DropdownMenuItem(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(12.dp)), onClick = {
                    action.onClick()
                    isExpanded = false
                }, text = {
                    Text(
                        text = stringResource(action.description),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }, leadingIcon = {
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
                })
                if (index < hiddenActions.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .alpha(0.5f)
                    )
                }
            }
        }
    }
}
