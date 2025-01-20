package com.venom.ui.components.buttons

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.venom.resources.R

@Composable
fun ExpandIndicator(
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    containerColor: Color = Color.Transparent,
) {
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f, animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow
        ), label = ""
    )

    val scale by animateFloatAsState(
        targetValue = if (expanded) 1.2f else 1f, animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        ), label = ""
    )

    IconButton(
        onClick = onClick,
        modifier = modifier.padding(vertical = 4.dp),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = containerColor, contentColor = tint
        )
    ) {
        Icon(
            imageVector = Icons.Rounded.ExpandMore, contentDescription = stringResource(
                if (expanded) R.string.action_collapse
                else R.string.action_expand
            ), modifier = Modifier
                .scale(scale)
                .rotate(rotation), tint = tint
        )
    }
}
