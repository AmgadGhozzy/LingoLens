package com.venom.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CustomDragHandle(
    modifier: Modifier = Modifier, width: Dp = 32.dp, height: Dp = 4.dp, verticalPadding: Dp = 8.dp
) {
    Box(
        modifier = modifier.padding(vertical = verticalPadding)
    ) {
        Box(
            Modifier
                .width(width)
                .height(height)
                .background(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(2.dp)
                )
        )
    }
}