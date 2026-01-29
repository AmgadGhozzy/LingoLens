package com.venom.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.venom.domain.model.FontStyles
import com.venom.resources.R

private val defaultType = Typography()

val Cairo = FontFamily(
    Font(R.font.cairo_bold, FontWeight.Bold),
    Font(R.font.cairo_light, FontWeight.Light),
    Font(R.font.cairo_medium, FontWeight.Medium),
    Font(R.font.cairo_regular, FontWeight.Normal)
)

val Alexandria = FontFamily(
    Font(R.font.alexandria_bold, FontWeight.Bold),
    Font(R.font.alexandria_light, FontWeight.Light),
    Font(R.font.alexandria_medium, FontWeight.Medium),
    Font(R.font.alexandria_regular, FontWeight.Normal)
)

val Caveat = FontFamily(
    Font(R.font.caveat_medium, FontWeight.Medium),
    Font(R.font.caveat_regular, FontWeight.Normal),
    Font(R.font.caveat_bold, FontWeight.Bold)
)

val Roboto = FontFamily(
    Font(R.font.roboto_medium, FontWeight.Medium),
    Font(R.font.roboto_light, FontWeight.Light),
    Font(R.font.roboto_regular, FontWeight.Normal),
    Font(R.font.roboto_bold, FontWeight.Bold)
)

val Quicksand = FontFamily(
    Font(R.font.quicksand_medium, FontWeight.Medium),
    Font(R.font.quicksand_light, FontWeight.Light),
    Font(R.font.quicksand_regular, FontWeight.Normal),
    Font(R.font.quicksand_bold, FontWeight.Bold)
)

val JosefinSans = FontFamily(
    Font(R.font.josefin_sans_medium, FontWeight.Medium),
    Font(R.font.josefin_sans_light, FontWeight.Light),
    Font(R.font.josefin_sans_regular, FontWeight.Normal),
    Font(R.font.josefin_sans_bold, FontWeight.Bold)
)

val PlaypenSans = FontFamily(
    Font(R.font.playpen_sans, FontWeight.Normal)
)


fun getTypography(fontFamily: FontFamily) = defaultType.copy(
    displayLarge = defaultType.displayLarge.copy(fontFamily = fontFamily),
    displayMedium = defaultType.displayMedium.copy(fontFamily = fontFamily),
    displaySmall = defaultType.displaySmall.copy(fontFamily = fontFamily),
    headlineLarge = defaultType.headlineLarge.copy(fontFamily = fontFamily),
    headlineMedium = defaultType.headlineMedium.copy(fontFamily = fontFamily),
    headlineSmall = defaultType.headlineSmall.copy(fontFamily = fontFamily),
    titleLarge = defaultType.titleLarge.copy(fontFamily = fontFamily),
    titleMedium = defaultType.titleMedium.copy(fontFamily = fontFamily),
    titleSmall = defaultType.titleSmall.copy(fontFamily = fontFamily),
    bodyLarge = defaultType.bodyLarge.copy(fontFamily = fontFamily),
    bodyMedium = defaultType.bodyMedium.copy(fontFamily = fontFamily),
    bodySmall = defaultType.bodySmall.copy(fontFamily = fontFamily),
    labelLarge = defaultType.labelLarge.copy(fontFamily = fontFamily),
    labelMedium = defaultType.labelMedium.copy(fontFamily = fontFamily),
    labelSmall = defaultType.labelSmall.copy(fontFamily = fontFamily)
)

fun FontStyles.toFontFamily(): FontFamily = when (this) {
    FontStyles.Default -> FontFamily.Default
    FontStyles.SANS_SERIF -> FontFamily.SansSerif
    FontStyles.SERIF -> FontFamily.Serif
    FontStyles.MONOSPACE -> FontFamily.Monospace
    FontStyles.CURSIVE -> FontFamily.Cursive

    FontStyles.CAIRO -> Cairo
    FontStyles.ALEXANDRIA -> Alexandria
    FontStyles.CAVEAT -> Caveat
    FontStyles.ROBOTO -> Roboto
    FontStyles.QUICKSAND -> Quicksand
    FontStyles.JOSEFIN_SANS -> JosefinSans
}