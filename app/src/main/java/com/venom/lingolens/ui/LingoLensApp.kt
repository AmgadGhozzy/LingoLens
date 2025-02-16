package com.venom.lingolens.ui

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.venom.lingolens.navigation.LingoLensBottomBar
import com.venom.lingolens.navigation.LingoLensTopBar
import com.venom.lingolens.navigation.NavigationGraph
import com.venom.lingolens.navigation.navigateToStart
import com.venom.phrase.ui.screen.PhrasesDialog
import com.venom.settings.presentation.screen.SettingsBottomSheet
import com.venom.stackcard.ui.screen.WordBookmarksDialog
import com.venom.textsnap.ui.viewmodel.OcrViewModel
import com.venom.ui.navigation.Screen
import com.venom.ui.screen.BookmarkHistoryScreen
import com.venom.ui.screen.ContentType

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun LingoLensApp(
    ocrViewModel: OcrViewModel,
    startCamera: () -> Unit,
    imageSelector: () -> Unit
) {
    val appState = rememberLingoLensAppState()

    LingoLensAppContent(
        appState = appState,
        ocrViewModel = ocrViewModel,
        startCamera = startCamera,
        imageSelector = imageSelector
    )
}

@Composable
private fun rememberLingoLensAppState(
    navController: NavHostController = rememberNavController()
) = remember(navController) {
    LingoLensAppState(navController)
}.apply {
    this.currentBackStackEntry = navController.currentBackStackEntryAsState().value
}


private class LingoLensAppState(
    val navController: NavHostController
) {
    var currentBackStackEntry: NavBackStackEntry? by mutableStateOf(null)
    var showBookmarkHistory by mutableStateOf(false)
    var showSettings by mutableStateOf(false)

    val currentRoute: String?
        get() = currentBackStackEntry?.destination?.route

    val currentScreen: Screen
        get() = Screen.fromRoute(currentRoute ?: Screen.Translation.route)

    val shouldShowTopBar: Boolean
        get() = currentRoute != Screen.Quiz.LevelTest.route

    val shouldShowBottomBar: Boolean
        get() = currentRoute != Screen.Ocr.route && currentRoute != Screen.Quiz.LevelTest.route

    fun navigateToScreen(screen: Screen) {
        navController.navigateToStart(screen)
    }

    fun handleBackPress() {
        when {
            showSettings -> showSettings = false
            showBookmarkHistory -> {
                showBookmarkHistory = false
                navigateToScreen(currentScreen)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
private fun LingoLensAppContent(
    appState: LingoLensAppState,
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
                    appState.navigateToScreen(
                        Screen.fromRoute(appState.currentRoute ?: Screen.Translation.route)
                    )
                }
            )
            return
        }

        appState.showSettings -> {
            SettingsBottomSheet(
                onDismiss = { appState.showSettings = false }
            )
        }
    }

    Scaffold(
        topBar = {
            if (appState.shouldShowTopBar)
                LingoLensTopBar(
                    currentScreen = appState.currentScreen,
                    onNavigateBack = {
                        appState.navController.popBackStack()
                    },
                    onBookmarkClick = { appState.showBookmarkHistory = true },
                    onSettingsClick = { appState.showSettings = true }
                )
        },
        bottomBar = {
            if (appState.shouldShowBottomBar)
                LingoLensBottomBar(
                    navController = appState.navController,
                    currentScreen = appState.currentScreen,
                    onScreenSelected = { screen ->
                        appState.navigateToScreen(screen)
                    }
                )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                NavigationGraph(
                    ocrViewModel = ocrViewModel,
                    navController = appState.navController,
                    startCamera = startCamera,
                    imageSelector = imageSelector
                )
            }
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
