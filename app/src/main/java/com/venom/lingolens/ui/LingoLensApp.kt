package com.venom.lingolens.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.venom.data.model.TranslationProvider
import com.venom.lingolens.navigation.AppState
import com.venom.lingolens.navigation.NavigationGraph
import com.venom.phrase.ui.screen.PhrasesDialog
import com.venom.settings.presentation.screen.AboutBottomSheet
import com.venom.settings.presentation.screen.SettingsBottomSheet
import com.venom.stackcard.ui.screen.WordBookmarksDialog
import com.venom.textsnap.ui.viewmodel.OcrViewModel
import com.venom.ui.navigation.Screen
import com.venom.ui.screen.BookmarkHistoryScreen
import com.venom.ui.screen.ContentType
import com.venom.ui.viewmodel.TranslateViewModel

@Composable
fun LingoLensApp(
    ocrViewModel: OcrViewModel,
    startCamera: () -> Unit,
    imageSelector: () -> Unit,
    fileSelector: () -> Unit,
) {
    val navController = rememberNavController()
    val appState = remember(navController) {
        AppState(navController)
    }.apply {
        currentBackStackEntry = navController.currentBackStackEntryAsState().value
    }

    val translateViewModel: TranslateViewModel = hiltViewModel()

    LingoLensAppContent(
        appState = appState,
        ocrViewModel = ocrViewModel,
        translateViewModel = translateViewModel,
        startCamera = startCamera,
        imageSelector = imageSelector,
        fileSelector = fileSelector
    )
}

@Composable
private fun LingoLensAppContent(
    appState: AppState,
    ocrViewModel: OcrViewModel,
    translateViewModel: TranslateViewModel,
    startCamera: () -> Unit,
    imageSelector: () -> Unit,
    fileSelector: () -> Unit,
) {
    BackHandler(enabled = appState.showBookmarkHistory || appState.showSettings) {
        appState.handleBackPress()
    }

    val translationUiState by translateViewModel.uiState.collectAsStateWithLifecycle()

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
                    onAboutClick = { appState.showAbout = true },

                    showProviderSelector = appState.currentScreen == Screen.Translation,
                    selectedProvider = translationUiState.selectedProvider,
                    availableProviders = if (appState.currentScreen == Screen.Translation) TranslationProvider.ALL else emptyList(),
                    onProviderSelected = translateViewModel::updateProvider
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
                translateViewModel = translateViewModel,
                startCamera = startCamera,
                imageSelector = imageSelector,
                fileSelector = fileSelector
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