package com.venom.ui.components.buttons

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.venom.resources.R
import com.venom.ui.components.common.adp

@Composable
fun ExpandIndicator(
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary,
    containerColor: Color = MaterialTheme.colorScheme.primary.copy(0.1f)
) {
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    val scale by animateFloatAsState(
        targetValue = if (expanded) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        )
    )

    IconButton(
        onClick = onClick,
        modifier = modifier
            .padding(vertical = 4.adp)
            .size(32.adp),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = containerColor,
            contentColor = tint
        )
    ) {
        Icon(
            imageVector = Icons.Rounded.ExpandMore,
            contentDescription = stringResource(
                if (expanded) R.string.action_collapse
                else R.string.action_expand
            ),
            modifier = Modifier
                .scale(scale)
                .rotate(rotation),
            tint = tint
        )
    }
}