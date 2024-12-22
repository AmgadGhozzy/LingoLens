package com.venom.ui.components.bars

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.venom.domain.model.LANGUAGES_LIST
import com.venom.domain.model.LanguageItem
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomButton
import com.venom.ui.components.items.LanguageItemView

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
    flagSize: Dp = 32.dp
) {

    var isSelectingSourceLanguage by remember { mutableStateOf(true) }
    var showLanguageSelector by remember { mutableStateOf(false) }

    // Animation for the rotation of the swap button
    val rotation by animateFloatAsState(
        targetValue = if (isFromBottomSheet) 0f else 180f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "Swap Button Rotation"
    )

    // Language selection bottom sheet
//    if (showLanguageSelector && !isFromBottomSheet) {
//        LanguageSelectorBottomSheet(initialSourceLang = sourceLang,
//            initialTargetLang = targetLang,
//            isSelectingSourceLanguage = isSelectingSourceLanguage,
//            onLanguageSelected = { selectedLanguage ->
//                onLanguageSelect(isSelectingSourceLanguage, selectedLanguage)
//                showLanguageSelector = false
//            },
//            onDismiss = { showLanguageSelector = false })
//    }

    val interactionSource = remember { MutableInteractionSource() }
    val backgroundColor = if (isSelectingSourceLanguage) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    } else {
        Color.Transparent
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Source Language Selector
        LanguageItemView(language = sourceLang, showFlag = showFlag, onClick = {
            isSelectingSourceLanguage = true
            showLanguageSelector = true
        })
        // Swap Button
        CustomButton(
            icon = R.drawable.icon_swap,
            contentDescription = stringResource(id = R.string.action_swap_languages),
            onClick = onSwapLanguages,
            modifier = Modifier.rotate(rotation)
        )

        // Target Language Selector
        LanguageItemView(language = targetLang, showFlag = showFlag, onClick = {
            isSelectingSourceLanguage = true
            showLanguageSelector = true
        })
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun TranslationTopBarPreview() {
    LanguageBar(sourceLang = LANGUAGES_LIST[0],
        targetLang = LANGUAGES_LIST[1],
        onSwapLanguages = {},
        onLanguageSelect = { _, _ -> })
}
