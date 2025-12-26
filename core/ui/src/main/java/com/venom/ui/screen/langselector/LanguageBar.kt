package com.venom.ui.screen.langselector

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.venom.data.model.LANGUAGES_LIST
import com.venom.data.model.LanguageItem
import com.venom.resources.R

/**
 * A customizable top bar for language translation.
 *
 * @param langSelectorViewModel The view model for language selection.
 * @param languages List of available languages.
 * @param isFromBottomSheet Whether this is being rendered inside a bottom sheet.
 * @param showNativeNameHint Whether to show the native name of languages.
 * @param showFlag Whether to show flags for languages.
 * @param modifier Additional modifier for customization.
 * @param flagSize The size of the flag icon.
 * @param containerColor The color of the container.
 * @param contentColor The color of the content.
 */

@Composable
fun LanguageBar(
    langSelectorViewModel: LangSelectorViewModel,
    languages: List<LanguageItem> = LANGUAGES_LIST,
    isFromBottomSheet: Boolean = false,
    showNativeNameHint: Boolean = false,
    showFlag: Boolean = false,
    modifier: Modifier = Modifier,
    flagSize: Dp = 32.dp,
    containerColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified
) {
    val state by langSelectorViewModel.state.collectAsState()
    var showLanguageSelector by remember { mutableStateOf(false) }

    LaunchedEffect(languages) {
        langSelectorViewModel.setCustomLanguagesList(languages)
    }

    val rotation by animateFloatAsState(
        targetValue = if (isFromBottomSheet) 0f else 180f, animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow
        )
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Source Language
            AnimatedContent(
                targetState = state.sourceLang,
                transitionSpec = {
                    (scaleIn(tween(300)) + fadeIn(tween(300)))
                        .togetherWith(scaleOut(tween(300)) + fadeOut(tween(300)))
                }
            ) { language ->
                LanguageItemView(
                    language = language,
                    showFlag = showFlag,
                    flagSize = flagSize,
                    showNativeNameHint = showNativeNameHint,
                    showArrow = !isFromBottomSheet,
                    onClick = {
                        langSelectorViewModel.setSelectingSourceLanguage(true)
                        showLanguageSelector = true
                    }
                )
            }

            // Swap Button
            IconButton(
                onClick = { langSelectorViewModel.swapLanguages() },
                modifier = Modifier.rotate(rotation)
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = stringResource(id = R.string.swap_languages),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Target Language
            AnimatedContent(
                targetState = state.targetLang,
                transitionSpec = {
                    (scaleIn(tween(300)) + fadeIn(tween(300)))
                        .togetherWith(scaleOut(tween(300)) + fadeOut(tween(300)))
                }
            ) { language ->
                LanguageItemView(
                    language = language,
                    showFlag = showFlag,
                    flagSize = flagSize,
                    showNativeNameHint = showNativeNameHint,
                    showArrow = !isFromBottomSheet,
                    onClick = {
                        langSelectorViewModel.setSelectingSourceLanguage(false)
                        showLanguageSelector = true
                    }
                )
            }
        }
    }

    if (showLanguageSelector && !isFromBottomSheet) {
        LangSelectorBottomSheet(
            viewModel = langSelectorViewModel,
            onDismiss = { showLanguageSelector = false }
        )
    }
}