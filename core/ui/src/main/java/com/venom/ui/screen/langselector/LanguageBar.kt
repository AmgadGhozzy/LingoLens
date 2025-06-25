package com.venom.ui.screen.langselector

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.venom.resources.R

/**
 * A customizable top bar for language translation.
 *
 * @param viewModel The view model for language selection.
 * @param showNativeNameHint Whether to show the native name of languages.
 * @param showFlag Whether to show flags for languages.
 * @param modifier Additional modifier for customization.
 * @param flagSize The size of the flag icon.
 * @param containerColor The color of the container.
 * @param contentColor The color of the content.
 */

@Composable
fun LanguageBar(
    viewModel: LangSelectorViewModel,
    isFromBottomSheet: Boolean = false,
    showNativeNameHint: Boolean = false,
    showFlag: Boolean = false,
    modifier: Modifier = Modifier,
    flagSize: Dp = 32.dp,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    val state by viewModel.state.collectAsState()
    var showLanguageSelector by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isFromBottomSheet) 0f else 180f, animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow
        )
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
            // Source Language
            AnimatedContent(
                targetState = state.sourceLang
            ) { language ->
                LanguageItemView(
                    language = language,
                    showFlag = showFlag,
                    flagSize = flagSize,
                    showNativeNameHint = showNativeNameHint,
                    showArrow = !isFromBottomSheet,
                    onClick = {
                        viewModel.setSelectingSourceLanguage(true)
                        showLanguageSelector = true
                    }
                )
            }

            // Swap Button
            IconButton(
                onClick = { viewModel.swapLanguages() }, modifier = Modifier.rotate(rotation)
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_swap),
                    contentDescription = stringResource(R.string.action_swap_languages),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Target Language
            AnimatedContent(
                targetState = state.targetLang
            ) { language ->
                LanguageItemView(
                    language = language,
                    showFlag = showFlag,
                    flagSize = flagSize,
                    showNativeNameHint = showNativeNameHint,
                    showArrow = !isFromBottomSheet,
                    onClick = {
                        viewModel.setSelectingSourceLanguage(false)
                        showLanguageSelector = true
                    }
                )
            }
        }
    }

    if (showLanguageSelector && !isFromBottomSheet) {
        LangSelectorBottomSheet(
            viewModel = viewModel,
            onDismiss = { showLanguageSelector = false }
        )
    }
}