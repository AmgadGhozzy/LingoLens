package com.venom.lingolens.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.venom.ui.screen.BookmarkHistoryScreen
import com.venom.lingopro.ui.screens.TranslationScreen
import com.venom.phrase.ui.screen.CategoryScreen
import com.venom.phrase.ui.screen.PhraseScreen
import com.venom.textsnap.ui.screens.OcrScreen
import com.venom.textsnap.ui.viewmodel.OcrViewModel
import com.venom.ui.screen.ContentType

@Composable
fun NavigationGraph(
    ocrViewModel: OcrViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startCamera: () -> Unit,
    imageSelector: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Translation.route,
        modifier = modifier
    ) {
        composable(Screen.Translation.route) {
            TranslationScreen(
                onDismiss = {},
            )
        }

        composable(Screen.Ocr.route) {
            OcrScreen(
                viewModel = ocrViewModel,
                onCameraClick = startCamera,
                onFileClick = { },
                onGalleryClick = { imageSelector() },
            )
        }

        composable(Screen.Phrases.route) {
            CategoryScreen()
        }

        composable(Screen.History.route) {
            BookmarkHistoryScreen(
                onBackClick = { navController.popBackStack() },
                contentType = ContentType.TRANSLATION
            )
        }
    }
}