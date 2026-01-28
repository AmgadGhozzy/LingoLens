package com.venom.lingospell.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.venom.domain.model.WordMaster
import com.venom.lingospell.domain.FeedbackState
import com.venom.lingospell.domain.MAX_STREAK
import com.venom.lingospell.presentation.components.HintButton
import com.venom.lingospell.presentation.components.LetterBank
import com.venom.lingospell.presentation.components.StreakBar
import com.venom.lingospell.presentation.components.WordSlots
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomFilledIconButton
import com.venom.ui.theme.lingoLens
import com.venom.ui.viewmodel.TTSViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SpellingGameScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    customWord: WordMaster,
    viewModel: SpellingGameViewModel = hiltViewModel(),
    ttsViewModel: TTSViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(customWord) {
        if (customWord.wordEn != state.currentWord.wordEn) {
            viewModel.setCustomWord(customWord)
        }
    }

    // Handle events side-effects
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is SpellingGameEvent.PlayTts -> { ttsViewModel.speak(event.text, event.languageTag) }
                SpellingGameEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { viewModel.onScreenTapDuringSuccess() }
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StreakBar(
                streak = state.streak,
                isMastered = state.streak >= MAX_STREAK
            )
            CustomFilledIconButton(
                icon = Icons.Rounded.Close,
                onClick = viewModel::onCloseClick, // centralize navigation via ViewModel event
                contentDescription = stringResource(R.string.action_close),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                size = 38.dp
            )
        }

        // Main content area
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Arabic word prompt
            AnimatedContent(
                targetState = state.currentWord.arabicAr,
                transitionSpec = {
                    (fadeIn() + scaleIn(initialScale = 0.95f)) togetherWith
                            (fadeOut() + scaleOut(targetScale = 0.95f))
                }
            ) { arabic ->
                val scale by animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )

                Text(
                    text = arabic,
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .scale(scale)
                        .padding(horizontal = 32.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Feedback / Instructions area
            Box(
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = state.feedback,
                    transitionSpec = {
                        (fadeIn() + scaleIn(initialScale = 0.8f)) togetherWith
                                 (fadeOut() + scaleOut(targetScale = 0.8f))
                    }
                ) { feedback ->
                    when (feedback) {
                        FeedbackState.MASTERED -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.icon_trophy),
                                    contentDescription = null,
                                    modifier = Modifier.size(28.dp),
                                    tint = MaterialTheme.lingoLens.feature.spelling.mastery
                                )
                                Text(
                                    text = "MASTERED!",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.lingoLens.feature.spelling.mastery
                                )
                            }
                        }

                        FeedbackState.SUCCESS -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.icon_circle_check),
                                    contentDescription = null,
                                    modifier = Modifier.size(28.dp),
                                    tint = MaterialTheme.lingoLens.semantic.success
                                )
                                Text(
                                    text = "PERFECT!",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.lingoLens.semantic.success
                                )
                            }
                        }

                        else -> {
                            Text(
                                text = "Translate to English",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Word slots
            WordSlots(
                slots = state.slots,
                onSlotClick = viewModel::onSlotClick,
                shakeTrigger = state.shakeTrigger,
                feedback = state.feedback
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Hint button
            Box(modifier = Modifier.height(48.dp), contentAlignment = Alignment.Center) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = state.showHintButton,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    HintButton(
                        hintLevel = state.hintLevel,
                        onHintClick = viewModel::onHintRequest,
                        enabled = true
                    )
                }
            }
        }

        // Letter bank at bottom
        LetterBank(
            letters = state.bank,
            onLetterClick = viewModel::onLetterClick,
            onClear = viewModel::onClearAll
        )
    }
}
