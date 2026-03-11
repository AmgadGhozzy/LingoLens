package com.venom.ui.components.other

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.venom.ui.components.common.adp
import com.venom.ui.theme.LingoColors
import com.venom.ui.theme.lingoLens

enum class GlassThickness { UltraThin, Thin, Regular, Thick, UltraThick }
enum class GlassTint { Primary, Secondary, Accent }

@Composable
private fun GlassThickness.toElevation(): Dp = when (this) {
    GlassThickness.UltraThin -> 2.adp
    GlassThickness.Thin -> 4.adp
    GlassThickness.Regular -> 6.adp
    GlassThickness.Thick -> 8.adp
    GlassThickness.UltraThick -> 12.adp
}

@Composable
private fun GlassThickness.toLayer(): Color {
    val glass = MaterialTheme.lingoLens.glass
    return when (this) {
        GlassThickness.UltraThin -> glass.ultraThin
        GlassThickness.Thin -> glass.thin
        GlassThickness.Regular -> glass.regular
        GlassThickness.Thick -> glass.thick
        GlassThickness.UltraThick -> glass.ultraThick
    }
}

@Composable
private fun GlassThickness.toBorderColor(): Color {
    val glass = MaterialTheme.lingoLens.glass
    return when (this) {
        GlassThickness.UltraThin, GlassThickness.Thin -> glass.border
        else -> glass.borderStrong
    }
}

/**
 * A premium glassmorphism card with Apple-inspired liquid aesthetics.
 *
 * @param thickness     Glass material thickness level (controls fill opacity + elevation).
 *                      automatically match the active nav bar accent colour.
 * @param showBorder    Show glass border (auto-selects light/strong based on thickness).
 * @param showShadow    Show depth shadow using GlassColors ambient/spot tones.
 * @param shape         Card corner shape.
 * @param contentPadding Internal padding.
 * @param onClick       Optional click handler; adds ripple feedback when set.
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
    content: @Composable ColumnScope.() -> Unit,
) {
    val glass = MaterialTheme.lingoLens.glass

    val glassLayer = thickness.toLayer()
    val borderColor = thickness.toBorderColor()
    val elevation = thickness.toElevation()

    val tintColor: Color = when (tint) {
        GlassTint.Primary -> glass.tintPrimary
        GlassTint.Secondary -> glass.tintSecondary
        GlassTint.Accent -> glass.tintAccent
        null -> Color.Transparent
    }

    Box(
        modifier = modifier
            .then(
                if (showShadow) Modifier.shadow(
                    elevation = elevation,
                    shape = shape,
                    ambientColor = glass.shadow,
                    spotColor = glass.shadowStrong,
                ) else Modifier,
            )
            .clip(shape)
            .background(glassLayer)
            .then(if (tintColor != Color.Transparent) Modifier.background(tintColor) else Modifier)
            .then(if (showBorder) Modifier.border(1.adp, borderColor, shape) else Modifier)
            .then(
                if (onClick != null) Modifier.clickable(
                    onClick = onClick,
                    role = Role.Button,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(color = MaterialTheme.colorScheme.primary),
                ) else Modifier,
            ),
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(12.adp),
            content = content,
        )
    }
}

/**
 * Gradient glass card with optional orbet ambient glow particle.
 * The navigation bar will share its accent palette, glow hue, and shadow tint
 * automatically — no extra parameters needed.
 *
 * @param gradientColors  Linear gradient stops applied over the glass fill.
 *                        Defaults to [NavBarColors.cardGradient] (alpha pre-baked).
 * @param gradientAlpha   Multiplied into each gradient stop. Keep at 1f when
 *                        using the default [NavBarColors.cardGradient]; lower it
 *                        only when passing fully-opaque custom colors.
 * @param showOrbet       Draw a soft radial glow in the top-right corner.
 * @param orbetColor      Orbet hue.
 * @param shadowColor     Spot-shadow tint.
 * @param borderColor     Card outline.
 */
@Composable
fun GradientGlassCard(
    modifier: Modifier = Modifier,
    thickness: GlassThickness = GlassThickness.Regular,
    gradientColors: List<Color> = MaterialTheme.lingoLens.glass.gradientColors,
    gradientAlpha: Float = 0.1f,
    showOrbet: Boolean = false,
    orbetColor: Color = LingoColors.Blue500,
    showBorder: Boolean = false,
    borderColor: Color = MaterialTheme.lingoLens.glass.border,
    showShadow: Boolean = true,
    shadowColor: Color = Color.Unspecified,
    shape: Shape = RoundedCornerShape(24.adp),
    contentPadding: Dp = 0.adp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val glass = MaterialTheme.lingoLens.glass
    val glassLayer = thickness.toLayer()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (showShadow) Modifier.shadow(
                    elevation = 20.adp,
                    shape = shape,
                    ambientColor = shadowColor.copy(alpha = 0.50f),
                    spotColor = shadowColor.copy(alpha = 0.80f),
                ) else Modifier,
            )
            .clip(shape)
            .background(glassLayer)
            .background(
                brush = if (gradientColors.size >= 2) {
                    Brush.linearGradient(
                        colors = gradientColors.map { it.copy(alpha = it.alpha * gradientAlpha) },
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                    )
                } else {
                    SolidColor(
                        gradientColors.firstOrNull()
                            ?.let { it.copy(alpha = it.alpha * gradientAlpha) }
                            ?: Color.Transparent
                    )
                },
            )
            .then(if (showBorder) Modifier.border(0.5.adp, borderColor, shape) else Modifier)
            .then(
                if (onClick != null) Modifier.clickable(
                    onClick = onClick,
                    role = Role.Button,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(color = MaterialTheme.colorScheme.primary),
                ) else Modifier,
            ),
    ) {
        // Orbet: z-bottom so content Column renders on top.
        if (showOrbet) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 32.dp, y = (-48).dp)
                    .size(192.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(orbetColor.copy(alpha = 0.22f), Color.Transparent),
                            radius = 250f,
                        ),
                        shape = CircleShape,
                    ),
            )
        }

        Column(
            modifier = Modifier.padding(contentPadding),
            content = content,
        )
    }
}