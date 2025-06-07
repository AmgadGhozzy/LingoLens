package com.venom.ui.components.onboarding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.filled.GTranslate
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.venom.resources.R
import com.venom.ui.theme.ThemeColors.CoralPrimary
import com.venom.ui.theme.ThemeColors.CoralSecondary
import com.venom.ui.theme.ThemeColors.CyanPrimary
import com.venom.ui.theme.ThemeColors.CyanSecondary
import com.venom.ui.theme.ThemeColors.MagentaPrimary
import com.venom.ui.theme.ThemeColors.MagentaSecondary
import com.venom.ui.theme.ThemeColors.OrangePrimary
import com.venom.ui.theme.ThemeColors.OrangeSecondary
import com.venom.ui.theme.ThemeColors.PurplePrimary
import com.venom.ui.theme.ThemeColors.PurpleSecondary
import com.venom.ui.theme.ThemeColors.TealPrimary
import com.venom.ui.theme.ThemeColors.TealSecondary

@Composable
fun getPages(): List<OnboardingPage> = listOf(
    OnboardingPage(
        title = stringResource(id = R.string.onboarding_page1_title),
        subtitle = stringResource(id = R.string.onboarding_page1_subtitle),
        description = stringResource(id = R.string.onboarding_page1_description),
        icon = Icons.Rounded.Psychology,
        primaryColor = PurplePrimary,
        secondaryColor = PurpleSecondary,
        features = emptyList()
    ),
    OnboardingPage(
        title = stringResource(id = R.string.onboarding_page2_title),
        subtitle = stringResource(R.string.onboarding_page2_subtitle),
        description = stringResource(id = R.string.onboarding_page2_description),
        icon = Icons.Default.GTranslate,
        primaryColor = TealPrimary,
        secondaryColor = TealSecondary,
        features = listOf(
            stringResource(id = R.string.onboarding_feature1),
            stringResource(id = R.string.onboarding_feature2),
            stringResource(id = R.string.onboarding_feature3)
        )
    ),
    OnboardingPage(
        title = stringResource(id = R.string.onboarding_page3_title),
        subtitle = stringResource(id = R.string.onboarding_page3_subtitle),
        description = stringResource(id = R.string.onboarding_page3_description),
        icon = Icons.Rounded.PhotoCamera,
        primaryColor = CoralPrimary,
        secondaryColor = CoralSecondary,
        features = listOf(
            stringResource(id = R.string.onboarding_feature4),
            stringResource(id = R.string.onboarding_feature5),
            stringResource(id = R.string.onboarding_feature6)
        )
    ),
    OnboardingPage(
        title = stringResource(id = R.string.onboarding_page4_title),
        subtitle = stringResource(id = R.string.onboarding_page4_subtitle),
        description = stringResource(id = R.string.onboarding_page4_description),
        icon = Icons.AutoMirrored.Rounded.MenuBook,
        primaryColor = CyanPrimary,
        secondaryColor = CyanSecondary,
        features = listOf(
            stringResource(id = R.string.onboarding_feature7),
            stringResource(id = R.string.onboarding_feature8),
            stringResource(id = R.string.onboarding_feature9)
        )
    ),
    OnboardingPage(
        title = stringResource(id = R.string.onboarding_page5_title),
        subtitle = stringResource(id = R.string.onboarding_page5_subtitle),
        description = stringResource(id = R.string.onboarding_page5_description),
        icon = Icons.Rounded.Mic,
        primaryColor = OrangePrimary,
        secondaryColor = OrangeSecondary,
        features = listOf(
            stringResource(id = R.string.onboarding_feature10),
            stringResource(id = R.string.onboarding_feature11),
            stringResource(id = R.string.onboarding_feature12)
        )
    ),
    OnboardingPage(
        title = stringResource(id = R.string.onboarding_page6_title),
        subtitle = stringResource(id = R.string.onboarding_page6_subtitle),
        description = stringResource(id = R.string.onboarding_page6_description),
        icon = Icons.Default.RocketLaunch,
        primaryColor = MagentaPrimary,
        secondaryColor = MagentaSecondary,
        features = listOf(
            stringResource(id = R.string.onboarding_feature13),
            stringResource(id = R.string.onboarding_feature14),
            stringResource(id = R.string.onboarding_feature15)
        )
    )
)

data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: ImageVector,
    val primaryColor: Color,
    val secondaryColor: Color,
    val features: List<String> = emptyList()
)