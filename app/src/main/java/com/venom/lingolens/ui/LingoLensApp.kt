package com.venom.lingolens.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.rememberNavController
import com.venom.lingolens.navigation.NavigationGraph
import com.venom.lingolens.navigation.Screen
import com.venom.resources.R
import com.venom.textsnap.ui.screens.OcrViewModel
import com.venom.ui.components.bars.TopBar
import com.venom.ui.components.buttons.CustomButton

@Composable
fun LingoLensApp(
    ocrViewModel: OcrViewModel,
    startCamera: () -> Unit, imageSelector: () -> Unit) {
    val navController = rememberNavController()
    var selectedScreen: Screen by remember { mutableStateOf(Screen.Translation) }

    Scaffold(
        topBar = {
            TopBar(
                title = stringResource(id = R.string.app_name),
                onNavigationClick = {
                    navController.navigate(Screen.Translation.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                actions = {
                    CustomButton(
                        icon = R.drawable.icon_history,
                        contentDescription = stringResource(R.string.history_title),
                        onClick = { }
                    )
                    CustomButton(
                        icon = R.drawable.icon_bookmark_filled,
                        contentDescription = stringResource(R.string.bookmarks_title),
                        onClick = { }
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Bookmark, contentDescription = stringResource(R.string.bookmarks_title)) },
                    label = { Text(stringResource(R.string.bookmarks_title)) },
                    selected = selectedScreen == Screen.Bookmarks,
                    onClick = {
                        selectedScreen = Screen.Bookmarks
                        navController.navigate(Screen.Bookmarks.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Camera, contentDescription = stringResource(R.string.ocr_title)) },
                    label = { Text(stringResource(R.string.ocr_title)) },
                    selected = selectedScreen == Screen.Ocr,
                    onClick = {
                        selectedScreen = Screen.Ocr
                        navController.navigate(Screen.Ocr.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Translate, contentDescription = stringResource(R.string.translate_title)) },
                    label = { Text(stringResource(R.string.translate_title)) },
                    selected = selectedScreen == Screen.Translation,
                    onClick = {
                        selectedScreen = Screen.Translation
                        navController.navigate(Screen.Translation.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavigationGraph(
            ocrViewModel = ocrViewModel,
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            startCamera = startCamera,
            imageSelector = imageSelector
        )
    }
}