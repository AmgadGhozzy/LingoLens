package com.venom.ui.components.other

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import com.venom.ui.components.common.adp
import com.venom.ui.theme.lingoLens

/**
 * A GlassCard is a special card that supports a glassy effect. It is a flexible and
 * customizable composable that can be used to enhance the UI of your app.
 *
 * A premium glassmorphism card with Apple-inspired liquid aesthetics.
 * Supports multiple glass thickness levels, optional tints, and depth effects.
 *
 * @param modifier Modifier for the card
 * @param thickness Glass material thickness level
 * @param tint Optional color tint overlay (null for no tint)
 * @param showBorder Whether to show the glass border
 * @param showShadow Whether to show depth shadow
 * @param shape Card corner shape
 * @param contentPadding Padding inside the card
 * @param onClick Optional click handler
 * @param content Card content
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    thickness: GlassThickness = GlassThickness.Regular,
    tint: GlassTint? = GlassTint.Primary,
    showBorder: Boolean = false,
    showShadow: Boolean = true,
    shape: Shape = RoundedCornerShape(20.adp),
    contentPadding: Dp = 0.adp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val glass = MaterialTheme.lingoLens.glass

    // Select glass layer based on thickness
    val glassLayer = when (thickness) {
        GlassThickness.UltraThin -> glass.ultraThin
        GlassThickness.Thin -> glass.thin
        GlassThickness.Regular -> glass.regular
        GlassThickness.Thick -> glass.thick
        GlassThickness.UltraThick -> glass.ultraThick
    }

    // Select border based on thickness
    val borderColor = when (thickness) {
        GlassThickness.UltraThin, GlassThickness.Thin -> glass.border
        else -> glass.borderStrong
    }

    // Select tint color
    val tintColor = when (tint) {
        GlassTint.Primary -> glass.tintPrimary
        GlassTint.Secondary -> glass.tintSecondary
        GlassTint.Accent -> glass.tintAccent
        null -> Color.Transparent
    }

    Box(
        modifier = modifier
            .then(
                if (showShadow) {
                    Modifier.shadow(
                        elevation = when (thickness) {
                            GlassThickness.UltraThin -> 2.adp
                            GlassThickness.Thin -> 4.adp
                            GlassThickness.Regular -> 6.adp
                            GlassThickness.Thick -> 8.adp
                            GlassThickness.UltraThick -> 12.adp
                        },
                        shape = shape,
                        ambientColor = glass.shadow,
                        spotColor = glass.shadowStrong
                    )
                } else Modifier
            )
            .clip(shape)
            .background(glassLayer)
            .then(if (tintColor != Color.Transparent) Modifier.background(tintColor) else Modifier)
            .then(
                if (showBorder) {
                    Modifier.border(
                        width = 1.adp,
                        color = borderColor,
                        shape = shape
                    )
                } else Modifier
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        onClick = onClick,
                        role = Role.Button,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = MaterialTheme.colorScheme.primary)
                    )
                } else Modifier
            )
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(12.adp),
            content = content
        )
    }
}

/**
 * Glass thickness levels for material layers
 */
enum class GlassThickness {
    UltraThin,  // 40-50% opacity - Subtle hints
    Thin,       // 60-70% opacity - Light overlay
    Regular,    // 75-85% opacity - Standard glass
    Thick,      // 85-92% opacity - Heavy glass
    UltraThick  // 95-97% opacity - Maximum blur
}

/**
 * Predefined glass tint overlays
 */
enum class GlassTint {
    Primary,    // Blue-tinted glass
    Secondary,  // Purple-tinted glass
    Accent      // Cyan-tinted glass
}

@Composable
fun GradientGlassCard(
    modifier: Modifier = Modifier,
    thickness: GlassThickness = GlassThickness.Regular,
    gradientColors: List<Color> = listOf(
        MaterialTheme.lingoLens.glass.tintPrimary,
        MaterialTheme.lingoLens.glass.tintSecondary,
        MaterialTheme.lingoLens.glass.tintAccent
    ),
    gradientAlpha: Float = 0.1f,
    showBorder: Boolean = false,
    borderColor: Color = MaterialTheme.lingoLens.glass.border,
    showShadow: Boolean = true,
    shape: Shape = RoundedCornerShape(24.adp),
    contentPadding: Dp = 0.adp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val glass = MaterialTheme.lingoLens.glass

    val glassLayer = when (thickness) {
        GlassThickness.UltraThin -> glass.ultraThin
        GlassThickness.Thin -> glass.thin
        GlassThickness.Regular -> glass.regular
        GlassThickness.Thick -> glass.thick
        GlassThickness.UltraThick -> glass.ultraThick
    }

    Box(
        modifier = modifier
            .then(
                if (showShadow) {
                    Modifier.shadow(
                        elevation = 12.adp,
                        shape = shape,
                        ambientColor = glass.shadow,
                        spotColor = glass.shadowStrong
                    )
                } else Modifier
            )
            .clip(shape)
            .background(glassLayer)
            .background(
                brush = if (gradientColors.size >= 2) {
                    Brush.linearGradient(
                        colors = gradientColors.map { it.copy(alpha = gradientAlpha) }
                    )
                } else {
                    SolidColor(gradientColors.firstOrNull()?.copy(alpha = gradientAlpha) ?: Color.Transparent)
                }
            )
            .then(
                if (showBorder) {
                    Modifier.border(
                        width = 0.5.adp,
                        color = borderColor,
                        shape = shape
                    )
                } else Modifier
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        onClick = onClick,
                        role = Role.Button,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = MaterialTheme.colorScheme.primary)
                    )
                } else Modifier
            )
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}
