package com.venom.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.ColorUtils
import com.venom.domain.model.PaletteStyle
import kotlin.math.min

object ThemeColors {
    val Green = Color(0xFFBADB94)
    val Red = Color(0xFFE06565)
    val Blue = Color(0xFF0088CC)
    val Black = Color(0xFF142329)
    val StrongBlack = Color(0xFF141414)
    val White = Color(0xFFFFFFFF)
    val BitcoinColor = Color(0xFFF7931A)
    val USDTColor = Color(0xFF50AF95)
    val TONSpaceColor = Color(0xFF232328)
    val TONColor = Color(0xFF0098EA)
}

@Composable
fun createDynamicColorScheme(
    keyColor: Color,
    style: PaletteStyle = PaletteStyle.Neutral,
    isDark: Boolean = isSystemInDarkTheme(),
    isAmoledBlack: Boolean = false
): ColorScheme {
    val context = LocalContext.current
    val supportsDynamic = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    return when {
        style == PaletteStyle.Neutral && supportsDynamic -> {
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        isDark -> createColorScheme(keyColor, style, isDark, isAmoledBlack)
        else -> createColorScheme(keyColor, style, isDark, isAmoledBlack)
    }
}

private fun createColorScheme(
    keyColor: Color,
    style: PaletteStyle,
    isDark: Boolean,
    isAmoledBlack: Boolean
): ColorScheme {
    val backgroundColor = when {
        isDark && isAmoledBlack -> Color.Black
        isDark -> Color(0xFF121212)
        else -> Color(0xFFFAFAFA)
    }

    val surfaceColor = if (isDark) backgroundColor else Color.White

    val scheme = ColorSchemeGenerator(
        keyColor = keyColor,
        isDark = isDark,
        backgroundColor = backgroundColor,
        surfaceColor = surfaceColor
    )

    return when (style) {
        PaletteStyle.Neutral -> scheme.generateNeutral()
        PaletteStyle.Vibrant -> scheme.generateVibrant()
        PaletteStyle.Expressive -> scheme.generateExpressive()
        PaletteStyle.Rainbow -> scheme.generateRainbow()
        PaletteStyle.FruitSalad -> scheme.generateFruitSalad()
        PaletteStyle.Monochrome -> scheme.generateMonochrome()
    }
}

private class ColorSchemeGenerator(
    private val keyColor: Color,
    private val isDark: Boolean,
    private val backgroundColor: Color,
    private val surfaceColor: Color
) {
    private val baseColorInt = keyColor.toArgb()

    fun generateNeutral() = createScheme {
        primary = keyColor
    }

    fun generateFruitSalad() = createScheme {
        primary = keyColor
        secondary = keyColor.copy(alpha = 0.7f)
        tertiary = rotateHue(baseColorInt, 120f)
    }

    fun generateVibrant() = createScheme {
        primary = saturateColor(keyColor)
        secondary = saturateColor(keyColor.copy(alpha = 0.8f))
        tertiary = saturateColor(rotateHue(baseColorInt, 180f))
    }

    fun generateExpressive() = createScheme {
        primary = keyColor
        secondary = rotateHue(baseColorInt, 180f)
        tertiary = rotateHue(baseColorInt, 120f)
    }

    fun generateRainbow() = createScheme {
        primary = keyColor
        secondary = rotateHue(baseColorInt, 60f)
        tertiary = rotateHue(baseColorInt, 120f)
    }

    fun generateMonochrome() = createScheme {
        val (first, second, third) = createMonochromeTriple(baseColorInt)
        primary = first
        secondary = second
        tertiary = third
    }

    private fun createScheme(block: ColorSchemeBuilder.() -> Unit): ColorScheme {
        return ColorSchemeBuilder(isDark, backgroundColor, surfaceColor)
            .apply(block)
            .build()
    }

    private fun rotateHue(color: Int, degrees: Float): Color {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(color, hsv)
        hsv[0] = (hsv[0] + degrees) % 360
        return Color(android.graphics.Color.HSVToColor(hsv))
    }

    private fun saturateColor(color: Color): Color {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(color.toArgb(), hsv)
        hsv[1] = min(1f, hsv[1] * 1.2f)
        return Color(android.graphics.Color.HSVToColor(hsv))
    }

    private fun createMonochromeTriple(baseColor: Int): Triple<Color, Color, Color> {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(baseColor, hsv)

        return Triple(
            Color(baseColor),
            Color(android.graphics.Color.HSVToColor(hsv.clone().apply { this[2] *= 0.8f })),
            Color(android.graphics.Color.HSVToColor(hsv.clone().apply { this[2] *= 0.6f }))
        )
    }
}

private class ColorSchemeBuilder(
    private val isDark: Boolean,
    private val backgroundColor: Color,
    private val surfaceColor: Color
) {
    var primary: Color = Color.Unspecified
    var secondary: Color = Color.Unspecified
    var tertiary: Color = Color.Unspecified

    fun build(): ColorScheme {
        return if (isDark) {
            darkColorScheme(
                primary = primary,
                secondary = secondary,
                tertiary = tertiary,
                background = backgroundColor,
                surface = surfaceColor
            )
        } else {
            lightColorScheme(
                primary = primary,
                secondary = secondary,
                tertiary = tertiary,
                background = backgroundColor,
                surface = surfaceColor
            )
        }
    }
}

fun Color.harmonize(color: Color, fraction: Float = 0.2f): Color =
    Color(ColorUtils.blendARGB(this.toArgb(), color.toArgb(), fraction))

@Composable
fun Color.harmonizeWithPrimary(fraction: Float = 0.2f): Color =
    harmonize(MaterialTheme.colorScheme.primary, fraction)