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
import com.venom.phrase.ui.screen.PhrasesDialog
import com.venom.resources.R
import com.venom.textsnap.ui.viewmodel.OcrViewModel
import com.venom.ui.components.bars.TopBar
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
    val navController = rememberNavController()
    var selectedScreen by remember { mutableStateOf<Screen>(Screen.Translation) }
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route
    var showBookmarkHistory by remember { mutableStateOf(false) }

    // Remove LaunchedEffect since initialization is done in Application class

    BackHandler(enabled = showBookmarkHistory) {
        showBookmarkHistory = false
        navController.navigate(selectedScreen.route) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    if (showBookmarkHistory) {
        if (selectedScreen != Screen.Phrases) {
            BookmarkHistoryScreen(
                onBackClick = { showBookmarkHistory = false },
                contentType = selectedScreen.toContentType()
            )
        } else if (currentDestination == Screen.Phrases.route) {
            PhrasesDialog(categoryId = -1, onDismiss = { showBookmarkHistory = false })
        }
        return
    }

    Scaffold(topBar = {
        if (currentDestination != Screen.Ocr.route) LingoLensTopBar(navController,
            onBookmarkClick = { showBookmarkHistory = true })
        else TopBar(title = stringResource(R.string.ocr_title),
            onLeadingIconClick = { navController.navigateUp() },
            leadingIcon = R.drawable.icon_back,
            actions = {
                TopBarActions(onBookmarkClick = { showBookmarkHistory = true })
            })
    }, bottomBar = {
        if (currentDestination != Screen.Ocr.route) LingoLensBottomBar(
            navController, selectedScreen
        ) { screen ->
            selectedScreen = screen
        }
    }) { padding ->
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
