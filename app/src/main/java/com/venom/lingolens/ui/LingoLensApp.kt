package com.venom.lingolens.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.venom.lingolens.navigation.AppState
import com.venom.lingolens.navigation.LingoLensBottomBar
import com.venom.lingolens.navigation.LingoLensTopBar
import com.venom.lingolens.navigation.NavigationGraph
import com.venom.phrase.ui.screen.PhrasesDialog
import com.venom.settings.presentation.screen.AboutBottomSheet
import com.venom.settings.presentation.screen.SettingsBottomSheet
import com.venom.stackcard.ui.screen.WordBookmarksDialog
import com.venom.textsnap.ui.viewmodel.OcrViewModel
import com.venom.ui.navigation.Screen
import com.venom.ui.screen.BookmarkHistoryScreen
import com.venom.ui.screen.ContentType

@Composable
fun LingoLensApp(
    ocrViewModel: OcrViewModel,
    startCamera: () -> Unit,
    imageSelector: () -> Unit
) {
    val navController = rememberNavController()
    val appState = remember(navController) {
        AppState(navController)
    }.apply {
        currentBackStackEntry = navController.currentBackStackEntryAsState().value
    }

    LingoLensAppContent(
        appState = appState,
        ocrViewModel = ocrViewModel,
        startCamera = startCamera,
        imageSelector = imageSelector
    )
}

@Composable
private fun LingoLensAppContent(
    appState: AppState,
    ocrViewModel: OcrViewModel,
    startCamera: () -> Unit,
    imageSelector: () -> Unit
) {
    BackHandler(enabled = appState.showBookmarkHistory || appState.showSettings) {
        appState.handleBackPress()
    }

    // Handle Dialogs and Sheets
    when {
        appState.showBookmarkHistory -> {
            BookmarkHistoryDialog(
                currentScreen = appState.currentScreen,
                onDismiss = {
                    appState.showBookmarkHistory = false
                    appState.navigateToScreen(appState.currentScreen)
                }
            )
            return
        }

        appState.showSettings -> {
            SettingsBottomSheet(
                onDismiss = { appState.showSettings = false }
            )
        }

        appState.showAbout -> {
            AboutBottomSheet(
                onDismiss = { appState.showAbout = false }
            )
        }
    }

    Scaffold(
        topBar = {
            if (appState.shouldShowTopBar) {
                LingoLensTopBar(
                    currentScreen = appState.currentScreen,
                    onNavigateBack = { appState.navController.popBackStack() },
                    onBookmarkClick = { appState.showBookmarkHistory = true },
                    onSettingsClick = { appState.showSettings = true },
                    onAboutClick = { appState.showAbout = true }
                )
            }
        },
        bottomBar = {
            if (appState.shouldShowBottomBar) {
                LingoLensBottomBar(
                    navController = appState.navController,
                    currentScreen = appState.currentScreen,
                    onScreenSelected = appState::navigateToScreen
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            NavigationGraph(
                navController = appState.navController,
                ocrViewModel = ocrViewModel,
                startCamera = startCamera,
                imageSelector = imageSelector
            )
        }
    }
}

@Composable
private fun BookmarkHistoryDialog(
    currentScreen: Screen,
    onDismiss: () -> Unit
) {
    when (currentScreen) {
        Screen.Translation, Screen.Ocr -> {
            BookmarkHistoryScreen(
                onBackClick = onDismiss,
                contentType = currentScreen.toContentType()
            )
        }

        Screen.Phrases -> PhrasesDialog(
            categoryId = -1,
            onDismiss = onDismiss
        )

        Screen.StackCard -> WordBookmarksDialog(
            onDismiss = onDismiss
        )

        else -> {}
    }
}

private fun Screen.toContentType() = when (this) {
    Screen.Translation -> ContentType.TRANSLATION
    Screen.Ocr -> ContentType.OCR
    Screen.Phrases -> ContentType.PHRASEBOOK
    Screen.Dialog -> ContentType.DIALOG
    Screen.StackCard -> ContentType.STACKCARD
    else -> ContentType.TRANSLATION
}
