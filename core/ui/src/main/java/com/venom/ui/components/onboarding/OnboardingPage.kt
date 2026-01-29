package com.venom.ui.components.onboarding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.filled.GTranslate
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.venom.resources.R
import com.venom.ui.theme.tokens.OnboardingColorSchemes
import com.venom.ui.theme.tokens.OnboardingColors

@Composable
fun onboardingPages(): List<OnboardingPage> = listOf(

    /* ───────────── Page 1: AI / Intelligence ───────────── */
    OnboardingPage(
        title = stringResource(R.string.onboarding_page1_title),
        subtitle = stringResource(R.string.onboarding_page1_subtitle),
        description = stringResource(R.string.onboarding_page1_description),
        icon = Icons.Rounded.Psychology,
        colors = OnboardingColorSchemes.AiPurple,
        features = listOf(
            stringResource(R.string.onboarding_feature_ai_analysis)
        )
    ),

    /* ───────────── Page 2: Translation ───────────── */
    OnboardingPage(
        title = stringResource(R.string.onboarding_page2_title),
        subtitle = stringResource(R.string.onboarding_page2_subtitle),
        description = stringResource(R.string.onboarding_page2_description),
        icon = Icons.Default.GTranslate,
        colors = OnboardingColorSchemes.TranslationTeal,
        features = listOf(
            stringResource(R.string.onboarding_feature_multi_provider),
            "100+ Languages"
        )
    ),

    /* ───────────── Page 3: OCR / Camera ───────────── */
    OnboardingPage(
        title = stringResource(R.string.onboarding_page3_title),
        subtitle = stringResource(R.string.onboarding_page3_subtitle),
        description = stringResource(R.string.onboarding_page3_description),
        icon = Icons.Rounded.PhotoCamera,
        colors = OnboardingColorSchemes.CameraRose,
        features = listOf(
            stringResource(R.string.onboarding_feature_text_snap),
            stringResource(R.string.onboarding_feature_object_ocr)
        )
    ),

    /* ───────────── Page 4: Quiz & Progress ───────────── */
    OnboardingPage(
        title = stringResource(R.string.onboarding_page4_title),
        subtitle = stringResource(R.string.onboarding_page4_subtitle),
        description = stringResource(R.string.onboarding_page4_description),
        icon = Icons.AutoMirrored.Rounded.MenuBook,
        colors = OnboardingColorSchemes.QuizEmerald,
        features = listOf(
            stringResource(R.string.onboarding_feature_word_levels),
            stringResource(R.string.onboarding_feature_streaks)
        )
    ),

    /* ───────────── Page 5: Practice / Voice ───────────── */
    OnboardingPage(
        title = stringResource(R.string.onboarding_page5_title),
        subtitle = stringResource(R.string.onboarding_page5_subtitle),
        description = stringResource(R.string.onboarding_page5_description),
        icon = Icons.Rounded.Mic,
        colors = OnboardingColorSchemes.PracticeAmber,
        features = listOf(
            stringResource(R.string.onboarding_feature_flashcards),
            stringResource(R.string.onboarding_feature_stt),
            stringResource(R.string.onboarding_feature_tts)
        )
    )
)

data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: ImageVector,
    val colors: OnboardingColors,
    val features: List<String> = emptyList()
)