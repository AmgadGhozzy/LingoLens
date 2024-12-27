package com.venom.lingolens.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.venom.lingolens.navigation.LingoLensBottomBar
import com.venom.lingolens.navigation.LingoLensTopBar
import com.venom.lingolens.navigation.NavigationGraph
import com.venom.lingolens.navigation.Screen
import com.venom.lingolens.navigation.navigateToStart
import com.venom.resources.R
import com.venom.textsnap.ui.viewmodel.OcrViewModel
import com.venom.ui.components.bars.TopBar
import com.venom.ui.components.buttons.CustomButton
import com.venom.ui.screen.BookmarkHistoryScreen
import com.venom.ui.screen.ContentType

@Composable
fun LingoLensApp(
    ocrViewModel: OcrViewModel, startCamera: () -> Unit, imageSelector: () -> Unit
) {
    val navController = rememberNavController()
    var selectedScreen by remember { mutableStateOf<Screen>(Screen.Translation) }
    var showBookmarkHistory by remember { mutableStateOf(false) }

    if (showBookmarkHistory) {
        BookmarkHistoryScreen(
            onBackClick = { showBookmarkHistory = false },
            contentType = selectedScreen.toContentType()
        )
        return
    }

    Scaffold(topBar = {
        LingoLensTopBar(navController, onBookmarkClick = { showBookmarkHistory = true })
    }, bottomBar = {
        LingoLensBottomBar(navController, selectedScreen) { screen ->
            selectedScreen = screen
        }
    }) { padding ->
        NavigationGraph(
            ocrViewModel = ocrViewModel,
            navController = navController,
            modifier = Modifier.padding(padding),
            startCamera = startCamera,
            imageSelector = imageSelector
        )
    }
}

private fun Screen.toContentType() = when (this) {
    Screen.Translation -> ContentType.TRANSLATION
    Screen.Ocr -> ContentType.OCR
    else -> ContentType.TRANSLATION
}