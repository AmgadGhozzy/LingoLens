package com.venom.ui.theme.tokens

import androidx.compose.ui.graphics.Color
import com.venom.ui.theme.BrandColors

data class OnboardingColors(
    val primary: Color,
    val secondary: Color
)

object OnboardingColorSchemes {

    val AiPurple = OnboardingColors(
        primary = BrandColors.Purple500,
        secondary = BrandColors.Purple700
    )

    val TranslationTeal = OnboardingColors(
        primary = BrandColors.Cyan500,
        secondary = BrandColors.Cyan700
    )

    val CameraRose = OnboardingColors(
        primary = BrandColors.Rose400,
        secondary = BrandColors.Rose600
    )

    val QuizEmerald = OnboardingColors(
        primary = BrandColors.Emerald400,
        secondary = BrandColors.Emerald600
    )

    val PracticeAmber = OnboardingColors(
        primary = BrandColors.Amber400,
        secondary = BrandColors.Amber600
    )

    val PremiumIndigo = OnboardingColors(
        primary = BrandColors.Indigo500,
        secondary = BrandColors.Indigo700
    )

    fun onboardingPages(): List<OnboardingColors> = listOf(
        AiPurple,
        TranslationTeal,
        CameraRose,
        QuizEmerald,
        PracticeAmber
    )
}
