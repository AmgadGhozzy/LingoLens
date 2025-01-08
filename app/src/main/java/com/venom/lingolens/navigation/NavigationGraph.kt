package com.venom.lingolens.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.venom.dialog.ui.screen.DialogScreen
import com.venom.lingopro.ui.screens.TranslationScreen
import com.venom.phrase.ui.screen.PhrasebookScreen
import com.venom.textsnap.ui.screens.OcrScreen
import com.venom.textsnap.ui.viewmodel.OcrViewModel
import com.venom.wordcard.ui.screen.WordCardScreen

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
        startDestination = Screen.WordCard.route,
        modifier = modifier
    ) {
        composable(Screen.Translation.route) {
            TranslationScreen()
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
            PhrasebookScreen()
        }

        composable(Screen.Dialog.route) {
            DialogScreen()
        }

        composable(Screen.WordCard.route) {
            WordCardScreen()
        }

    }
}