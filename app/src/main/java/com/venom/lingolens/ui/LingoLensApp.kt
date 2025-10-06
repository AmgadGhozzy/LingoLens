package com.venom.lingolens.ui

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.venom.data.model.TranslationProvider
import com.venom.lingolens.navigation.AppState
import com.venom.lingolens.navigation.NavigationGraph
import com.venom.settings.presentation.screen.AboutBottomSheet
import com.venom.settings.presentation.screen.SettingsBottomSheet
import com.venom.ui.components.other.FloatingOrbs
import com.venom.ui.navigation.Screen
import com.venom.ui.screen.ContentType
import com.venom.ui.viewmodel.TranslateViewModel

@Composable
fun LingoLensApp(
    startCamera: ((Uri?) -> Unit) -> Unit,
    imageSelector: ((Uri?) -> Unit) -> Unit,
    fileSelector: ((Uri?) -> Unit) -> Unit,
) {
    val navController = rememberNavController()
    val appState = remember(navController) {
        AppState(navController)
    }.apply {
        currentBackStackEntry = navController.currentBackStackEntryAsState().value
    }

    LingoLensAppContent(
        appState = appState,
        startCamera = startCamera,
        imageSelector = imageSelector,
        fileSelector = fileSelector
    )
}

@Composable
private fun LingoLensAppContent(
    appState: AppState,
    translateViewModel: TranslateViewModel = hiltViewModel(LocalActivity.current as ComponentActivity),
    startCamera: ((Uri?) -> Unit) -> Unit,
    imageSelector: ((Uri?) -> Unit) -> Unit,
    fileSelector: ((Uri?) -> Unit) -> Unit,
) {
    BackHandler(enabled = appState.showSettings || appState.showAbout) {
        appState.handleBackPress()
    }

    val translationUiState by translateViewModel.uiState.collectAsStateWithLifecycle()

    when {
        appState.showSettings -> {
            SettingsBottomSheet(
                onDismiss = { appState.showSettings = false },
                navController = appState.navController
            )
        }

        appState.showAbout -> {
            AboutBottomSheet(
                onDismiss = { appState.showAbout = false }
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        FloatingOrbs()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(top = if (appState.shouldShowTopBar) 64.dp else 0.dp)
        ) {
            NavigationGraph(
                navController = appState.navController,
                startCamera = startCamera,
                imageSelector = imageSelector,
                fileSelector = fileSelector,
            )
        }

        if (appState.shouldShowTopBar) {
            LingoLensTopBar(
                currentScreen = appState.currentScreen,
                onNavigateBack = { appState.navController.popBackStack() },
                onBookmarkClick = { appState.navigateToHistory(appState.currentScreen.toContentType().name) },
                onSettingsClick = { appState.showSettings = true },
                onAboutClick = { appState.showAbout = true },
                showProviderSelector = appState.currentScreen == Screen.Translation,
                selectedProvider = translationUiState.selectedProvider,
                availableProviders = if (appState.currentScreen == Screen.Translation) TranslationProvider.ALL else emptyList(),
                onProviderSelected = translateViewModel::updateProvider,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
            )
        }

        if (appState.shouldShowBottomBar) {
            LingoLensBottomBar(
                navController = appState.navController,
                currentScreen = appState.currentScreen,
                onScreenSelected = appState::navigateToScreen,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
            )
        }
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