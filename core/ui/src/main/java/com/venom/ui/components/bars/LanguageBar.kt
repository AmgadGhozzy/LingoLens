package com.venom.ui.components.bars


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.venom.domain.model.LANGUAGES_LIST
import com.venom.domain.model.LanguageItem
import com.venom.resources.R
import com.venom.ui.components.items.LanguageItemView
import com.venom.ui.screen.LanguageSelectorBottomSheet


/**
 * A customizable top bar for language translation.
 *
 * @param sourceLang The source language for translation.
 * @param targetLang The target language for translation.
 * @param onSwapLanguages Callback invoked when the swap languages button is clicked.
 * @param onLanguageSelect Callback invoked when a language is selected; boolean indicates if it's the source language.
 * @param showNativeNameHint Whether to show the native name of languages.
 * @param showFlag Whether to show flags for languages.
 * @param modifier Additional modifier for customization.
 * @param flagSize The size of the flag icon.
 * @param containerColor The color of the container.
 * @param contentColor The color of the content.
 */

@Composable
fun LanguageBar(
    sourceLang: LanguageItem,
    targetLang: LanguageItem,
    onSwapLanguages: () -> Unit,
    onLanguageSelect: (Boolean, LanguageItem) -> Unit,
    isFromBottomSheet: Boolean = false,
    showNativeNameHint: Boolean = false,
    showFlag: Boolean = false,
    modifier: Modifier = Modifier,
    flagSize: Dp = 32.dp,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    var isSelectingSourceLanguage by remember { mutableStateOf(true) }
    var showLanguageSelector by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isFromBottomSheet) 0f else 180f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "Swap Button Rotation"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            AnimatedContent(
                targetState = sourceLang,
                label = "Source Language Animation"
            ) { language ->
                LanguageItemView(
                    language = language,
                    showFlag = showFlag,
                    flagSize = flagSize,
                    showNativeNameHint = showNativeNameHint,
                    onClick = {
                        isSelectingSourceLanguage = true
                        showLanguageSelector = true
                    }
                )
            }

            IconButton(
                onClick = onSwapLanguages,
                modifier = Modifier.rotate(rotation)
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_swap),
                    contentDescription = stringResource(R.string.action_swap_languages),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedContent(
                targetState = targetLang,
                label = "Target Language Animation"
            ) { language ->
                LanguageItemView(
                    language = language,
                    showFlag = showFlag,
                    flagSize = flagSize,
                    showNativeNameHint = showNativeNameHint,
                    onClick = {
                        isSelectingSourceLanguage = false
                        showLanguageSelector = true
                    }
                )
            }
        }
    }

    if (showLanguageSelector && !isFromBottomSheet) {
        LanguageSelectorBottomSheet(
            initialSourceLang = sourceLang,
            initialTargetLang = targetLang,
            isSelectingSourceLanguage = isSelectingSourceLanguage,
            onLanguageSelected = { selectedLanguage ->
                onLanguageSelect(isSelectingSourceLanguage, selectedLanguage)
                showLanguageSelector = false
            },
            onDismiss = { showLanguageSelector = false }
        )
    }
}

@Preview
@Composable
fun TranslationTopBarPreview() {
    LanguageBar(sourceLang = LANGUAGES_LIST[0],
        targetLang = LANGUAGES_LIST[1],
        onSwapLanguages = {},
        onLanguageSelect = { _, _ -> })
}
