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

/**
 * A GlassCard is a special card that supports a glassy effect. It is a flexible and
 * customizable composable that can be used to enhance the UI of your app.
 *
 * @param contentPadding The padding of the content inside the card
 * @param glassAlpha The alpha value of the glass effect
 * @param glassPrimary The primary color of the glass effect
 * @param glassSecondary The secondary color of the glass effect
 * @param glassTertiary The tertiary color of the glass effect
 * @param borderAlpha The alpha value of the border
 * @param borderColor The color of the border
 * @param borderWidth The width of the border
 * @param padding The padding of the card
 * @param shape The shape of the card
 * @param solidBackground The solid background color of the card
 * @param solidBackgroundAlpha The alpha value of the solid background
 * @param onClick Click event handler for the card
 * @param content The content of the card
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    contentPadding: Dp = 0.dp,
    glassAlpha: Float = 0.15f,
    glassPrimary: Color = GlassPrimary,
    glassSecondary: Color = GlassSecondary,
    glassTertiary: Color = GlassTertiary,
    borderAlpha: Float = 0.1f,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    borderWidth: Dp = 1.dp,
    padding: Dp = 0.dp,
    shape: Shape = RoundedCornerShape(24.dp),
    solidBackgroundAlpha: Float = 0.1f,
    solidBackground: Color = MaterialTheme.colorScheme.background.copy(solidBackgroundAlpha),
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit = {},
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