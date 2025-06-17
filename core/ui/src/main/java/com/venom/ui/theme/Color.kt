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
    val Indigo = Color(0xFF6366F1)
    val Purple = Color(0xFF8B5CF6)

    val PurplePrimary = Color(0xFF6C63FF)
    val PurpleSecondary = Color(0xFF5A4BDA)

    val TealPrimary = Color(0xFF00C4CC)
    val TealSecondary = Color(0xFF00A8B5)

    val CoralPrimary = Color(0xFFFF6B6B)
    val CoralSecondary = Color(0xFFFF5252)

    val CyanPrimary = Color(0xFF4ECDC4)
    val CyanSecondary = Color(0xFF45B7D1)

    val OrangePrimary = Color(0xFFFF9E80)
    val OrangeSecondary = Color(0xFFFF6E40)

    val MagentaPrimary = Color(0xFF9C27B0)
    val MagentaSecondary = Color(0xFF7B1FA2)
}

@Composable
fun createDynamicColorScheme(
    keyColor: Color,
    style: PaletteStyle = PaletteStyle.Neutral,
    isDark: Boolean = isSystemInDarkTheme(),
    isAmoledBlack: Boolean = false,
    primaryColorValue: Color? = null
): ColorScheme {
    val context = LocalContext.current
    val supportsDynamic = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    return when {
        // Use dynamic colors only for Neutral style and when supported
        style == PaletteStyle.Neutral && supportsDynamic -> {
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        // Use base color schemes with customization
        else -> createCustomColorScheme(
            keyColor = keyColor,
            style = style,
            isDark = isDark,
            isAmoledBlack = isAmoledBlack,
            primaryColorValue = primaryColorValue
        )
    }
}

private fun createCustomColorScheme(
    keyColor: Color,
    style: PaletteStyle,
    isDark: Boolean,
    isAmoledBlack: Boolean,
    primaryColorValue: Color?
): ColorScheme {
    // Start with base color schemes
    val baseScheme = if (isDark) darkColorScheme else lightColorScheme

    // Handle AMOLED black theme
    val backgroundColor = when {
        isDark && isAmoledBlack -> Color.Black
        else -> baseScheme.background
    }

    val surfaceColor = when {
        isDark && isAmoledBlack -> Color.Black
        else -> baseScheme.surface
    }

    // Determine the primary color to use
    val effectivePrimaryColor = primaryColorValue ?: keyColor

    // Apply customizations based on style
    return when (style) {
        PaletteStyle.Neutral -> baseScheme.copy(
            primary = effectivePrimaryColor,
            background = backgroundColor,
            surface = surfaceColor,
            // Update surface variants for AMOLED
            surfaceVariant = if (isDark && isAmoledBlack) Color(0xFF1A1A1A) else baseScheme.surfaceVariant,
            surfaceContainer = if (isDark && isAmoledBlack) Color(0xFF0A0A0A) else baseScheme.surfaceContainer,
            surfaceContainerHigh = if (isDark && isAmoledBlack) Color(0xFF151515) else baseScheme.surfaceContainerHigh,
            surfaceContainerHighest = if (isDark && isAmoledBlack) Color(0xFF202020) else baseScheme.surfaceContainerHighest,
            surfaceContainerLow = if (isDark && isAmoledBlack) Color(0xFF050505) else baseScheme.surfaceContainerLow,
            surfaceContainerLowest = if (isDark && isAmoledBlack) Color.Black else baseScheme.surfaceContainerLowest
        )

        else -> {
            // For other styles, use the existing generator with base scheme as foundation
            val generator = ColorSchemeGenerator(
                keyColor = effectivePrimaryColor,
                isDark = isDark,
                backgroundColor = backgroundColor,
                surfaceColor = surfaceColor,
                baseScheme = baseScheme
            )

            when (style) {
                PaletteStyle.Vibrant -> generator.generateVibrant()
                PaletteStyle.Expressive -> generator.generateExpressive()
                PaletteStyle.Rainbow -> generator.generateRainbow()
                PaletteStyle.FruitSalad -> generator.generateFruitSalad()
                PaletteStyle.Monochrome -> generator.generateMonochrome()
                else -> generator.generateNeutral()
            }
        }
    }
}

private class ColorSchemeGenerator(
    private val keyColor: Color,
    private val isDark: Boolean,
    private val backgroundColor: Color,
    private val surfaceColor: Color,
    private val baseScheme: ColorScheme // Add base scheme
) {
    private val baseColorInt = keyColor.toArgb()

    fun generateNeutral() = baseScheme.copy(
        primary = keyColor,
        background = backgroundColor,
        surface = surfaceColor
    )

    fun generateFruitSalad() = baseScheme.copy(
        primary = keyColor,
        secondary = keyColor.copy(alpha = 0.7f).harmonize(baseScheme.secondary),
        tertiary = rotateHue(baseColorInt, 120f),
        background = backgroundColor,
        surface = surfaceColor
    )

    fun generateVibrant() = baseScheme.copy(
        primary = saturateColor(keyColor),
        secondary = saturateColor(keyColor.copy(alpha = 0.8f)).harmonize(baseScheme.secondary),
        tertiary = saturateColor(rotateHue(baseColorInt, 180f)),
        background = backgroundColor,
        surface = surfaceColor
    )

    fun generateExpressive() = baseScheme.copy(
        primary = keyColor,
        secondary = rotateHue(baseColorInt, 180f),
        tertiary = rotateHue(baseColorInt, 120f),
        background = backgroundColor,
        surface = surfaceColor
    )

    fun generateRainbow() = baseScheme.copy(
        primary = keyColor,
        secondary = rotateHue(baseColorInt, 60f),
        tertiary = rotateHue(baseColorInt, 120f),
        background = backgroundColor,
        surface = surfaceColor
    )

    fun generateMonochrome() = baseScheme.copy(
        primary = keyColor,
        secondary = createMonochromeVariant(baseColorInt, 0.8f),
        tertiary = createMonochromeVariant(baseColorInt, 0.6f),
        background = backgroundColor,
        surface = surfaceColor
    )

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

    private fun createMonochromeVariant(baseColor: Int, factor: Float): Color {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(baseColor, hsv)
        hsv[2] *= factor
        return Color(android.graphics.Color.HSVToColor(hsv))
    }
}

fun Color.harmonize(color: Color, fraction: Float = 0.2f): Color =
    Color(ColorUtils.blendARGB(this.toArgb(), color.toArgb(), fraction))

@Composable
fun Color.harmonizeWithPrimary(fraction: Float = 0.2f): Color =
    harmonize(MaterialTheme.colorScheme.primary, fraction)

val lightColorScheme = ColorScheme(
    primary = Color(0xFF00668B),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFC1E8FF),
    onPrimaryContainer = Color(0xFF001E2C),
    secondary = Color(0xFF4E616C),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD1E5F4),
    onSecondaryContainer = Color(0xFF091E28),
    tertiary = Color(0xFF605A7C),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE6DEFF),
    onTertiaryContainer = Color(0xFF1D1736),
    background = Color(0xFFF2FBFF),
    onBackground = Color(0xFF161C20),
    surface = Color(0xFFF2FBFF),
    onSurface = Color(0xFF161C20),
    surfaceVariant = Color(0xFFDCE3E9),
    onSurfaceVariant = Color(0xFF40484D),
    surfaceTint = Color(0xFF00668B),
    inverseSurface = Color(0xFF2A3136),
    inverseOnSurface = Color(0xFFEBF1F8),
    error = Color(0xFFB3261E),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    outline = Color(0xFF70777C),
    outlineVariant = Color(0xFFC0C7CD),
    scrim = Color(0xFF000000),
    surfaceBright = Color(0xFFF2FBFF),
    surfaceDim = Color(0xFFD3DBE2),
    surfaceContainer = Color(0xFFE7EFF6),
    surfaceContainerHigh = Color(0xFFE1E9F0),
    surfaceContainerHighest = Color(0xFFDCE3E9),
    surfaceContainerLow = Color(0xFFEDF5FC),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    inversePrimary = Color(0xFF76D1FF),
)

val darkColorScheme = ColorScheme(
    primary = Color(0xFF76D1FF),
    onPrimary = Color(0xFF003549),
    primaryContainer = Color(0xFF004C69),
    onPrimaryContainer = Color(0xFFC1E8FF),
    secondary = Color(0xFFB5CAD7),
    onSecondary = Color(0xFF20333D),
    secondaryContainer = Color(0xFF374955),
    onSecondaryContainer = Color(0xFFD1E5F4),
    tertiary = Color(0xFFCAC1EA),
    onTertiary = Color(0xFF322C4C),
    tertiaryContainer = Color(0xFF484264),
    onTertiaryContainer = Color(0xFFE6DEFF),
    background = Color(0xFF0D1419),
    onBackground = Color(0xFFDCE3E9),
    surface = Color(0xFF0D1419),
    onSurface = Color(0xFFDCE3E9),
    surfaceVariant = Color(0xFF40484D),
    onSurfaceVariant = Color(0xFFC0C7CD),
    surfaceTint = Color(0xFF76D1FF),
    inverseSurface = Color(0xFFDCE3E9),
    inverseOnSurface = Color(0xFF2A3136),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    outline = Color(0xFF8A9297),
    outlineVariant = Color(0xFF40484D),
    scrim = Color(0xFF000000),
    surfaceBright = Color(0xFF343A40),
    surfaceDim = Color(0xFF0D1419),
    surfaceContainer = Color(0xFF1A2025),
    surfaceContainerHigh = Color(0xFF242B30),
    surfaceContainerHighest = Color(0xFF2F363B),
    surfaceContainerLow = Color(0xFF161C20),
    surfaceContainerLowest = Color(0xFF060F15),
    inversePrimary = Color(0xFF00668B),
)