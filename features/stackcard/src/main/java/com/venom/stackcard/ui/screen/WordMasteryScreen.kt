package com.venom.stackcard.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.lingospell.presentation.SpellingGameScreen
import com.venom.stackcard.ui.components.flashcard.CardSwiperStack
import com.venom.stackcard.ui.components.insights.InsightsSheet
import com.venom.stackcard.ui.components.mastery.ActionBar
import com.venom.stackcard.ui.viewmodel.WordMasteryEvent
import com.venom.stackcard.ui.viewmodel.WordMasteryUiState
import com.venom.stackcard.ui.viewmodel.WordMasteryViewModel
import com.venom.ui.components.common.adp
import com.venom.ui.components.common.asp

@Composable
fun WordMasteryScreen(
    isGenerative: Boolean,
    onBack: () -> Unit = {},
    onGoogleSignIn: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: WordMasteryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()


    Box(modifier = modifier.fillMaxSize()) {
        when {
            uiState.visibleCards.isEmpty() && uiState.processedCardsCount == 0 && uiState.error == null -> {
                WelcomeScreen(
                    onBack = onBack,
                    onGoogleSignIn = onGoogleSignIn,
                    isLoading = uiState.isLoading,
                    onStart = { topic ->
                        viewModel.onEvent(
                            WordMasteryEvent.Initialize(
                                isGenerative = isGenerative,
                                topic = topic
                            )
                        )
                    }
                )
            }

            uiState.error != null -> {
                ErrorView(
                    message = uiState.error ?: "Unknown error",
                    onRetry = { viewModel.onEvent(WordMasteryEvent.Initialize(uiState.isGenerativeMode)) }
                )
            }

            uiState.visibleCards.isEmpty() && uiState.processedCardsCount > 0 -> {
                SessionFinishedView(
                    onBackToWelcome = { viewModel.onEvent(WordMasteryEvent.BackToWelcome) },
                    onExit = onBack
                )
            }

            else -> {
                MasteryContent(
                    uiState = uiState,
                    viewModel = viewModel,
                    onEvent = viewModel::onEvent
                )
            }
        }
    }
}

@Composable
private fun MasteryContent(
    uiState: WordMasteryUiState,
    viewModel: WordMasteryViewModel,
    onEvent: (WordMasteryEvent) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.weight(1f).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CardSwiperStack(
                    viewModel = viewModel,
                    onSpeak = {},
                    onRememberWord = {},
                    onForgotWord = {}
                )
            }

            ActionBar(
                onFlip = { onEvent(WordMasteryEvent.FlipCard) },
                onPractice = { onEvent(WordMasteryEvent.StartPractice) },
                onInfoClick = { onEvent(WordMasteryEvent.OpenSheet) },
                modifier = Modifier.padding(horizontal = 20.adp)
            )

            Spacer(modifier = Modifier.height(24.adp))
        }

        InsightsSheet(
            isOpen = uiState.isSheetOpen,
            word = uiState.currentWord,
            activeTab = uiState.activeTab,
            pinnedLanguage = uiState.pinnedLanguage,
            showPowerTip = uiState.showPowerTip,
            onClose = { onEvent(WordMasteryEvent.CloseSheet) },
            onTabChange = { tab -> onEvent(WordMasteryEvent.ChangeTab(tab)) },
            onPinLanguage = { lang -> onEvent(WordMasteryEvent.PinLanguage(lang)) },
            onTogglePowerTip = { onEvent(WordMasteryEvent.TogglePowerTip) },
            onSpeak = {}
        )

        if (uiState.isPracticeMode && uiState.currentWord != null) {
            Dialog(
                onDismissRequest = { onEvent(WordMasteryEvent.PracticeHandled) },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    usePlatformDefaultWidth = false,
                    decorFitsSystemWindows = false
                )
            ) {
                SpellingGameScreen(
                    customWord = uiState.currentWord,
                    onNavigateBack = { onEvent(WordMasteryEvent.PracticeHandled) }
                )
            }
        }
    }
}

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.adp),
            verticalArrangement = Arrangement.spacedBy(12.adp)
        ) {
            Text(
                text = "Something went wrong",
                fontSize = 20.asp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = message,
                fontSize = 14.asp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.adp))
            Button(onClick = onRetry) {
                Text("Try Again")
            }
        }
    }
}