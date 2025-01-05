package com.venom.lingolens.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.venom.dialog.ui.screen.DialogScreen
import com.venom.lingopro.ui.screens.TranslationScreen
import com.venom.phrase.ui.screen.PhrasebookScreen
import com.venom.phrase.ui.screen.PhrasesScreen
import com.venom.textsnap.ui.screens.OcrScreen
import com.venom.textsnap.ui.viewmodel.OcrViewModel

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
            PhrasebookScreen(
                onNavigateToCategory = { categoryId ->
                    navController.navigate(Screen.PhrasesCategory.createRoute(categoryId))
                }
            )
        }

        composable(Screen.PhrasesCategory.route) {
            val categoryId = it.arguments?.getString("categoryId")?.toIntOrNull()
            categoryId?.let { viewModel ->
                PhrasesScreen(
                    categoryId = viewModel
                )
            }
        }

        composable(Screen.Dialog.route) {
            DialogScreen()
        }

    }
}