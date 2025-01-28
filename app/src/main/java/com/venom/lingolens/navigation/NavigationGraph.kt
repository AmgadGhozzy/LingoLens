package com.venom.lingolens.navigation

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.venom.dialog.ui.screen.DialogScreen
import com.venom.lingopro.ui.screens.TranslationScreen
import com.venom.phrase.ui.screen.PhrasebookScreen
import com.venom.stackcard.ui.screen.CardScreen
import com.venom.textsnap.ui.screens.OcrScreen
import com.venom.textsnap.ui.viewmodel.OcrViewModel
import com.venom.ui.navigation.Screen

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
                navController = navController,
                initialText = null
            )
        }

        composable(
            route = "${Screen.Translation.route}/{text}",
            arguments = listOf(
                navArgument("text") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { entry ->
            val encodedText = entry.arguments?.getString("text") ?: ""
            val decodedText = Uri.decode(encodedText)
            Log.d("NavigationGraph", "Decoded text: $decodedText")

            TranslationScreen(
                navController = navController,
                initialText = decodedText
            )
        }
        composable(Screen.Ocr.route) {
            OcrScreen(
                viewModel = ocrViewModel,
                navController = navController,
                onCameraClick = startCamera,
                onFileClick = { imageSelector() },
                onGalleryClick = { imageSelector() }
            )
        }

        composable(Screen.Phrases.route) {
            PhrasebookScreen()
        }

        composable(Screen.Dialog.route) {
            DialogScreen()
        }

        composable(Screen.StackCard.route) {
            CardScreen()
        }
    }
}