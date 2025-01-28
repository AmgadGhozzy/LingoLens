package com.venom.lingolens.ui

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.venom.lingolens.navigation.LingoLensBottomBar
import com.venom.lingolens.navigation.LingoLensTopBar
import com.venom.lingolens.navigation.NavigationGraph
import com.venom.lingolens.navigation.TopBarActions
import com.venom.lingolens.navigation.navigateToStart
import com.venom.phrase.ui.screen.PhrasesDialog
import com.venom.resources.R
import com.venom.stackcard.ui.screen.WordBookmarksDialog
import com.venom.textsnap.ui.viewmodel.OcrViewModel
import com.venom.ui.components.bars.TopBar
import com.venom.ui.navigation.Screen
import com.venom.ui.screen.BookmarkHistoryScreen
import com.venom.ui.screen.ContentType

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun LingoLensApp(
    ocrViewModel: OcrViewModel, startCamera: () -> Unit, imageSelector: () -> Unit
) {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    var selectedScreen by remember {
        mutableStateOf(Screen.fromRoute(currentRoute ?: Screen.Translation.route))
    }
    var showBookmarkHistory by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    LaunchedEffect(currentRoute) {
        currentRoute?.let {
            selectedScreen = Screen.fromRoute(it)
        }
    }

    BackHandler(enabled = showBookmarkHistory || showSettings) {
        when {
            showSettings -> showSettings = false
            showBookmarkHistory -> {
                showBookmarkHistory = false
                navController.navigateToStart(selectedScreen)
            }
        }
    }

    if (showBookmarkHistory) {
        when (selectedScreen) {
            Screen.Translation, Screen.Ocr -> {
                BookmarkHistoryScreen(
                    onBackClick = {
                        showBookmarkHistory = false
                        selectedScreen = Screen.fromRoute(currentRoute ?: Screen.Translation.route)
                    },
                    contentType = selectedScreen.toContentType()
                )
            }

            Screen.Phrases -> PhrasesDialog(
                categoryId = -1,
                onDismiss = {
                    showBookmarkHistory = false
                    selectedScreen = Screen.fromRoute(currentRoute ?: Screen.Translation.route)
                }
            )

            Screen.StackCard -> WordBookmarksDialog(
                onDismiss = {
                    showBookmarkHistory = false
                    selectedScreen = Screen.fromRoute(currentRoute ?: Screen.Translation.route)
                }
            )

            else -> {}
        }
        return
    }

    Scaffold(
        topBar = {
            if (currentRoute != Screen.Ocr.route) {
                LingoLensTopBar(
                    onBookmarkClick = { showBookmarkHistory = true },
                    onSettingsClick = { showSettings = true }
                )
            } else {
                TopBar(
                    title = stringResource(R.string.ocr_title),
                    onLeadingIconClick = {
                        selectedScreen = Screen.Translation
                        navController.navigateToStart(Screen.Translation)
                    },
                    leadingIcon = R.drawable.icon_back,
                    actions = {
                        TopBarActions(
                            onBookmarkClick = { showBookmarkHistory = true },
                            onSettingsClick = { showSettings = true }
                        )
                    }
                )
            }
        },
        bottomBar = {
            if (currentRoute != Screen.Ocr.route) {
                LingoLensBottomBar(
                    navController = navController,
                    selectedScreen = selectedScreen,
                    onScreenSelected = { screen ->
                        selectedScreen = screen
                        navController.navigateToStart(screen)
                    }
                )
            }
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
                    navController = navController,
                    startCamera = startCamera,
                    imageSelector = imageSelector
                )
            }

//            BannerAd(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 8.dp)
//            )
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
