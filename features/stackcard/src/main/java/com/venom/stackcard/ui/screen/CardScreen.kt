package com.venom.stackcard.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.domain.model.WordLevels
import com.venom.resources.R
import com.venom.stackcard.ui.components.EmptyStateCard
import com.venom.stackcard.ui.components.OnboardingOverlay
import com.venom.stackcard.ui.viewmodel.CardItem
import com.venom.stackcard.ui.viewmodel.CardSwiperEvent
import com.venom.stackcard.ui.viewmodel.CardSwiperEvent.SetCardType
import com.venom.stackcard.ui.viewmodel.CardSwiperViewModel
import com.venom.stackcard.ui.viewmodel.CardType
import com.venom.ui.components.other.FloatingCircleMenu
import com.venom.ui.components.other.FloatingMenuItem
import com.venom.ui.components.other.FloatingOrbs
import com.venom.ui.components.sections.CustomTabs
import com.venom.ui.components.sections.TabItem
import com.venom.ui.theme.ThemeColors.BitcoinColor
import com.venom.ui.theme.ThemeColors.PurplePrimary
import com.venom.ui.theme.ThemeColors.TONColor
import com.venom.ui.theme.ThemeColors.USDTColor
import com.venom.ui.viewmodel.TTSViewModel
import com.venom.utils.Extensions.copyToClipboard
import com.venom.utils.Extensions.shareText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardScreen(
    wordViewModel: CardSwiperViewModel = hiltViewModel(),
    phraseViewModel: CardSwiperViewModel = hiltViewModel(),
    ttsViewModel: TTSViewModel = hiltViewModel(),
    onNavigateToSentence: (String) -> Unit = {},
    initialLevel: WordLevels? = null
) {
    val context = LocalContext.current

    // Actions for copy and speak functionalities
    val copyAction: (String) -> Unit = { text -> context.copyToClipboard(text) }
    val shareAction: (String) -> Unit = { text -> context.shareText(text) }
    val speakAction: (String) -> Unit = { text -> ttsViewModel.speak(text) }

    var selectedTab by remember { mutableIntStateOf(0) }

    // Determine the current view model based on the selected tab
    val currentViewModel = if (selectedTab == 0) wordViewModel else phraseViewModel

    val state by currentViewModel.state.collectAsState()

    // Get the current top card safely - always the first card in visibleCards
    val currentCard = state.visibleCards.firstOrNull()

    // Actions for card events using the current view model
    val onBookmarkWord: (CardItem) -> Unit =
        { currentViewModel.onEvent(CardSwiperEvent.Bookmark(it)) }
    val onRememberWord: (CardItem) -> Unit =
        { currentViewModel.onEvent(CardSwiperEvent.Remember(it)) }
    val onForgotWord: (CardItem) -> Unit = { currentViewModel.onEvent(CardSwiperEvent.Forgot(it)) }

    // Set initial CardType for each ViewModel
    LaunchedEffect(selectedTab) {
        initialLevel?.let { wordViewModel.setLevel(initialLevel) }
        if (selectedTab == 0) wordViewModel.onEvent(SetCardType(CardType.WORD))
        else wordViewModel.onEvent(SetCardType(CardType.PHRASE))
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        FloatingOrbs()

        // Tabs for switching between card types
        CustomTabs(
            tabs = listOf(
                TabItem(R.string.words_title, R.drawable.icon_cards1),
                TabItem(R.string.phrase_title, R.drawable.icon_dialog)
            ), selectedTab = selectedTab, onTabSelected = { newTab ->
                selectedTab = newTab
                currentViewModel.onEvent(SetCardType(if (newTab == 0) CardType.WORD else CardType.PHRASE))
            }, modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 12.dp)
        )

        CardSwiperStack(
            modifier = Modifier.padding(bottom = 64.dp),
            viewModel = currentViewModel,
            onRememberCard = onRememberWord,
            onForgotCard = onForgotWord,
            onBookmarkCard = onBookmarkWord,
            onSpeak = speakAction,
            onCopy = copyAction,
        )

        // Only show FloatingCircleMenu if there's a current card
        currentCard?.let { card ->
            FloatingCircleMenu(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(124.dp),
                items = listOf(
                    FloatingMenuItem(
                        icon = Icons.Rounded.Bookmark,
                        color = USDTColor,
                        onClick = { onBookmarkWord(card) },
                    ),
                    FloatingMenuItem(
                        icon = Icons.AutoMirrored.Rounded.MenuBook,
                        color = PurplePrimary,
                        onClick = { onNavigateToSentence(card.englishEn) },
                    ),
                    FloatingMenuItem(
                        icon = Icons.AutoMirrored.Rounded.VolumeUp,
                        color = BitcoinColor,
                        onClick = { speakAction(card.englishEn) },
                    ),
                    FloatingMenuItem(
                        icon = Icons.Rounded.Share,
                        color = TONColor,
                        onClick = { shareAction(card.englishEn) },
                    )
                )
            )
        }

        if (state.visibleCards.isEmpty() && !state.isLoading) {
            EmptyStateCard(
                onRefresh = {
                    wordViewModel.onEvent(CardSwiperEvent.LoadNextBatch)
                },
                modifier = Modifier.align(Alignment.Center)
            )
        }

        var showOnboarding by remember { mutableStateOf(false) }
        // First-time user onboarding
        if (showOnboarding) {
            OnboardingOverlay(
                onDismiss = { showOnboarding = false },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}