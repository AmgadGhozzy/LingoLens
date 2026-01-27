package com.venom.ui.components.onboarding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.filled.GTranslate
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.venom.resources.R
import com.venom.ui.theme.tokens.OnboardingColorSchemes as ColorSchemes

@Composable
fun getPages(): List<OnboardingPage> {
    val colors = ColorSchemes.getPageColors()

    return listOf(
        // Page 1: WordCraft AI Focus
        OnboardingPage(
            title = stringResource(id = R.string.onboarding_page1_title),
            subtitle = stringResource(id = R.string.onboarding_page1_subtitle),
            description = stringResource(id = R.string.onboarding_page1_description),
            icon = Icons.Rounded.Psychology,
            primaryColor = colors[0].primary,
            secondaryColor = colors[0].secondary,
            features = listOf(
                stringResource(id = R.string.onboarding_feature_ai_analysis)
            )
        ),
        // Page 2: Translation Engine Focus
        OnboardingPage(
            title = stringResource(id = R.string.onboarding_page2_title),
            subtitle = stringResource(R.string.onboarding_page2_subtitle),
            description = stringResource(id = R.string.onboarding_page2_description),
            icon = Icons.Default.GTranslate,
            primaryColor = colors[1].primary,
            secondaryColor = colors[1].secondary,
            features = listOf(
                stringResource(id = R.string.onboarding_feature_multi_provider),
                "100+ Languages"
            )
        ),
        // Page 3: OCR / TextSnap Focus
        OnboardingPage(
            title = stringResource(id = R.string.onboarding_page3_title),
            subtitle = stringResource(id = R.string.onboarding_page3_subtitle),
            description = stringResource(id = R.string.onboarding_page3_description),
            icon = Icons.Rounded.PhotoCamera,
            primaryColor = colors[2].primary,
            secondaryColor = colors[2].secondary,
            features = listOf(
                stringResource(id = R.string.onboarding_feature_text_snap),
                stringResource(id = R.string.onboarding_feature_object_ocr)
            )
        ),
        // Page 4: Quiz & Progression Focus
        OnboardingPage(
            title = stringResource(id = R.string.onboarding_page4_title),
            subtitle = stringResource(id = R.string.onboarding_page4_subtitle),
            description = stringResource(id = R.string.onboarding_page4_description),
            icon = Icons.AutoMirrored.Rounded.MenuBook,
            primaryColor = colors[3].primary,
            secondaryColor = colors[3].secondary,
            features = listOf(
                stringResource(id = R.string.onboarding_feature_word_levels),
                stringResource(id = R.string.onboarding_feature_streaks)
            )
        ),
        // Page 5: StackCards & Practice Focus
        OnboardingPage(
            title = stringResource(id = R.string.onboarding_page5_title),
            subtitle = stringResource(id = R.string.onboarding_page5_subtitle),
            description = stringResource(id = R.string.onboarding_page5_description),
            icon = Icons.Rounded.Mic,
            primaryColor = colors[4].primary,
            secondaryColor = colors[4].secondary,
            features = listOf(
                stringResource(id = R.string.onboarding_feature_flashcards),
                stringResource(id = R.string.onboarding_feature_stt),
                stringResource(id = R.string.onboarding_feature_tts)
            )
        )
    )
}

data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: ImageVector,
    val primaryColor: Color,
    val secondaryColor: Color,
    val features: List<String> = emptyList()
)