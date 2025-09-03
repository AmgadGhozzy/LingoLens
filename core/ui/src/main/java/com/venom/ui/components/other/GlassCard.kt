package com.venom.ui.components.other

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.venom.ui.theme.ThemeColors.GlassPrimary
import com.venom.ui.theme.ThemeColors.GlassSecondary
import com.venom.ui.theme.ThemeColors.GlassTertiary

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    glassPrimary: Color = GlassPrimary,
    glassSecondary: Color = GlassSecondary,
    glassTertiary: Color = GlassTertiary,
    glassAlpha: Float = 0.15f,
    solidBackgroundAlpha: Float = 0.1f,
    solidBackground: Color = MaterialTheme.colorScheme.background.copy(solidBackgroundAlpha),
    borderAlpha: Float = 0.1f,
    shape: Shape = RoundedCornerShape(24.dp),
    padding: Dp = 0.dp,
    contentPadding: Dp = 0.dp,
    borderWidth: Dp = 1.dp,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    content: @Composable () -> Unit = {}
) {

    Box(
        modifier = modifier
            .padding(padding)
            .clip(shape)
            .background(solidBackground)
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        glassPrimary.copy(glassAlpha),
                        glassSecondary.copy(glassAlpha * 0.8f),
                        glassTertiary.copy(glassAlpha * 0.6f)
                    )
                )
            )
            .border(
                width = borderWidth,
                color = borderColor.copy(borderAlpha),
                shape = shape
            )
            .clickable(enabled = onClick != null) { onClick?.invoke() }
    ) {
        Box(
            modifier = Modifier.padding(contentPadding)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                content()
            }
        }
    }
}
