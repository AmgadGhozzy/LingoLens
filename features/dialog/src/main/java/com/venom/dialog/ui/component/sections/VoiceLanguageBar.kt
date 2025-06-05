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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.data.model.LanguageItem
import com.venom.dialog.ui.component.MicButton
import com.venom.resources.R
import com.venom.ui.screen.langselector.LangSelectorBottomSheet
import com.venom.ui.screen.langselector.LangSelectorViewModel
import com.venom.ui.screen.langselector.LanguageItemView

data class VoiceBarState(
    val isSourceListening: Boolean = false,
    val isTargetListening: Boolean = false
)

@Composable
fun VoiceLanguageBar(
    viewModel: LangSelectorViewModel = hiltViewModel(),
    isFromBottomSheet: Boolean = false,
    voiceBarState: VoiceBarState = VoiceBarState(),
    onSourceMicClick: () -> Unit = {},
    onTargetMicClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    flagSize: Dp = 32.dp
) {
    var showLanguageSelector by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsState()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
            .shadow(6.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
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
            LanguageSection(
                isListening = voiceBarState.isSourceListening,
                onMicClick = onSourceMicClick,
                micButtonColor = MaterialTheme.colorScheme.primary,
                micIconColor = MaterialTheme.colorScheme.onPrimary,
                language = state.sourceLang,
                flagSize = flagSize,
                onLanguageClick = {
                    viewModel.setSelectingSourceLanguage(true)
                    showLanguageSelector = true
                }
            )

            SwapLanguagesButton(
                isFromBottomSheet = isFromBottomSheet,
                onClick = viewModel::swapLanguages
            )

            LanguageSection(
                isListening = voiceBarState.isTargetListening,
                onMicClick = onTargetMicClick,
                micButtonColor = MaterialTheme.colorScheme.primaryContainer,
                micIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                language = state.targetLang,
                flagSize = flagSize,
                onLanguageClick = {
                    viewModel.setSelectingSourceLanguage(false)
                    showLanguageSelector = true
                }
            )
        }
    }

    if (showLanguageSelector && !isFromBottomSheet) {
        LangSelectorBottomSheet(
            viewModel = viewModel,
            onDismiss = { showLanguageSelector = false }
        )
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
    onLanguageClick: () -> Unit
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
                .border(0.5.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(28.dp))
                .padding(horizontal = 8.dp)
        )
    }
}

@Composable
private fun SwapLanguagesButton(
    isFromBottomSheet: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFromBottomSheet) 0f else 180f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    IconButton(
        onClick = onClick,
        modifier = modifier.rotate(rotation)
    ) {
        Icon(
            painter = painterResource(R.drawable.icon_swap),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
    }
}