package com.venom.stackcard.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.domain.model.WordLevels
import com.venom.resources.R
import com.venom.stackcard.ui.viewmodel.CardItem
import com.venom.stackcard.ui.viewmodel.CardSwiperEvent
import com.venom.stackcard.ui.viewmodel.CardSwiperEvent.SetCardType
import com.venom.stackcard.ui.viewmodel.CardSwiperViewModel
import com.venom.stackcard.ui.viewmodel.CardType
import com.venom.ui.components.sections.CustomTabs
import com.venom.ui.components.sections.TabItem
import com.venom.ui.viewmodel.TTSViewModel
import com.venom.utils.Extensions.copyToClipboard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardScreen(
    wordViewModel: CardSwiperViewModel = hiltViewModel(),
    phraseViewModel: CardSwiperViewModel = hiltViewModel(),
    ttsViewModel: TTSViewModel = hiltViewModel(),
    initialLevel: WordLevels? = null
) {
    val context = LocalContext.current

    // Actions for copy and speak functionalities
    val copyAction: (String) -> Unit = { text -> context.copyToClipboard(text) }
    val speakAction: (String) -> Unit = { text -> ttsViewModel.speak(text) }

    var selectedTab by remember { mutableIntStateOf(0) }

    // Determine the current view model based on the selected tab
    val currentViewModel = if (selectedTab == 0) wordViewModel else phraseViewModel

    // Actions for card events using the current view model

    val onBookmarkWord: (CardItem) -> Unit =
        { currentViewModel.onEvent(CardSwiperEvent.Bookmark(it)) }
    val onRememberWord: (CardItem) -> Unit =
        { currentViewModel.onEvent(CardSwiperEvent.Remember(it)) }
    val onForgotWord: (CardItem) -> Unit = { currentViewModel.onEvent(CardSwiperEvent.Forgot(it)) }

    // Set initial CardType for each ViewModel
    LaunchedEffect(selectedTab) {
        initialLevel?.let { wordViewModel.setLevel(initialLevel)}
        if (selectedTab == 0) wordViewModel.onEvent(SetCardType(CardType.WORD))
        else wordViewModel.onEvent(SetCardType(CardType.PHRASE))
    }

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
            .padding(horizontal = 12.dp)
    )

    // Main content of the screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 124.dp)
            .padding(48.dp),
        contentAlignment = Alignment.Center
    ) {
        CardSwiperStack(viewModel = currentViewModel,
            onRememberCard = onRememberWord,
            onForgotCard = onForgotWord,
            onBookmarkCard = onBookmarkWord,
            onSpeak = speakAction,
            onCopy = copyAction,
            onUndoLastAction = { })
    }
}