package com.venom.dialog.ui.component.sections

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.dialog.ui.component.MicButton
import com.venom.domain.model.LanguageItem
import com.venom.resources.R
import com.venom.ui.components.items.LanguageItemView
import com.venom.ui.screen.LangSelectorBottomSheet
import com.venom.ui.viewmodel.LangSelectorViewModel

data class VoiceBarState(
    val isSourceListening: Boolean = false, val isTargetListening: Boolean = false
)

/**
 * A composable that displays a language selection bar with voice recording capabilities
 * for both source and target languages.
 *
 * @param viewModel The view model handling language selection logic
 * @param isFromBottomSheet Whether this component is being displayed in a bottom sheet
 * @param voiceBarState The current state of voice recording
 * @param onSourceMicClick Callback when source language microphone is clicked
 * @param onTargetMicClick Callback when target language microphone is clicked
 * @param modifier Modifier for the component
 * @param flagSize Size of the flag icons
 */
@Composable
fun VoiceLanguageBar(
    viewModel: LangSelectorViewModel = hiltViewModel(),
    isFromBottomSheet: Boolean = false,
    voiceBarState: VoiceBarState = VoiceBarState(),
    onSourceMicClick: () -> Unit = {},
    onTargetMicClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    flagSize: Dp = 32.dp,
) {
    var showLanguageSelector by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
            .shadow(6.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(16.dp)
                .padding(top = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LanguageSection(isListening = voiceBarState.isSourceListening,
                onMicClick = onSourceMicClick,
                micButtonColor = MaterialTheme.colorScheme.primary,
                micIconColor = MaterialTheme.colorScheme.onPrimary,
                language = viewModel.state.collectAsState().value.sourceLang,
                flagSize = flagSize,
                onLanguageClick = {
                    viewModel.setSelectingSourceLanguage(true)
                    showLanguageSelector = true
                })

            SwapLanguagesButton(
                isFromBottomSheet = isFromBottomSheet, onClick = viewModel::swapLanguages
            )

            LanguageSection(isListening = voiceBarState.isTargetListening,
                onMicClick = onTargetMicClick,
                micButtonColor = MaterialTheme.colorScheme.primaryContainer,
                micIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                language = viewModel.state.collectAsState().value.targetLang,
                flagSize = flagSize,
                onLanguageClick = {
                    viewModel.setSelectingSourceLanguage(false)
                    showLanguageSelector = true
                })
        }
    }

    if (showLanguageSelector && !isFromBottomSheet) {
        LangSelectorBottomSheet(viewModel = viewModel, onDismiss = { showLanguageSelector = false })
    }
}

@Composable
private fun LanguageSection(
    isListening: Boolean,
    onMicClick: () -> Unit,
    micButtonColor: Color,
    micIconColor: Color,
    language: LanguageItem,
    flagSize: Dp,
    onLanguageClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.width(IntrinsicSize.Max)
    ) {
        MicButton(
            isListening = isListening,
            onClick = onMicClick,
            containerColor = micButtonColor,
            iconColor = micIconColor,
            modifier = Modifier
                .size(56.dp)
                .shadow(8.dp, CircleShape)
        )

        LanguageItemView(
            language = language,
            flagSize = flagSize,
            onClick = onLanguageClick,
            modifier = Modifier
                .clip(RoundedCornerShape(28.dp))
                .border(
                    0.5.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(28.dp)
                )
                .padding(horizontal = 8.dp)
        )
    }
}

@Composable
private fun SwapLanguagesButton(
    isFromBottomSheet: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFromBottomSheet) 0f else 180f, animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow
        ), label = "Swap Button Rotation"
    )

    IconButton(
        onClick = onClick, modifier = modifier.rotate(rotation)
    ) {
        Icon(
            painter = painterResource(R.drawable.icon_swap),
            contentDescription = stringResource(R.string.action_swap_languages),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun VoiceLanguageBarPreview() {
    MaterialTheme {
        VoiceLanguageBar(voiceBarState = VoiceBarState(
            isSourceListening = true, isTargetListening = false
        ), onSourceMicClick = {}, onTargetMicClick = {})
    }
}
