package com.venom.lingolens.ui

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.venom.lingolens.navigation.NavigationGraph
import com.venom.lingolens.navigation.Screen
import com.venom.ui.screen.BookmarkHistoryScreen
import com.venom.resources.R
import com.venom.textsnap.ui.viewmodel.OcrViewModel
import com.venom.ui.components.bars.TopBar
import com.venom.ui.components.buttons.CustomButton
import com.venom.ui.components.dialogs.CustomCard
import com.venom.ui.screen.ContentType

@Composable
fun LingoLensApp(
    ocrViewModel: OcrViewModel, startCamera: () -> Unit, imageSelector: () -> Unit
) {
    val navController = rememberNavController()
    var selectedScreen: Screen by remember { mutableStateOf(Screen.Translation) }
    var showHistoryDialog by remember { mutableStateOf(false) }
    var showBookmarkDialog by remember { mutableStateOf(false) }


    if (showHistoryDialog) BookmarkHistoryScreen(
        onBackClick = { showHistoryDialog = false }, contentType = ContentType.TRANSLATION
    )
    else if (showBookmarkDialog) BookmarkHistoryScreen(
        onBackClick = { showBookmarkDialog = false }, contentType = ContentType.OCR
    )
    else Scaffold(topBar = {
        TopBar(title = stringResource(id = R.string.app_name), onNavigationClick = {
            navController.navigate(Screen.Translation.route) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }
        }, actions = {
            CustomButton(icon = R.drawable.icon_history,
                contentDescription = stringResource(R.string.history_title),
                onClick = {
                    showHistoryDialog = true
                })
            CustomButton(icon = R.drawable.icon_bookmark_filled,
                contentDescription = stringResource(R.string.bookmarks_title),
                onClick = {
                    showBookmarkDialog = true

                })
            CustomButton(icon = R.drawable.icon_settings,
                contentDescription = stringResource(R.string.bookmarks_title),
                onClick = {

                })
        })
    }, bottomBar = {
        CustomCard {
            NavigationBar(
                modifier = Modifier.height(64.dp),
            ) {
                NavigationBarItem(icon = {
                    Icon(
                        Icons.Default.Bookmark,
                        contentDescription = stringResource(R.string.bookmarks_title)
                    )
                },
                    label = { Text(stringResource(R.string.bookmarks_title)) },
                    alwaysShowLabel = false,
                    selected = selectedScreen == Screen.Bookmarks,
                    onClick = {
                        selectedScreen = Screen.Bookmarks
                        navController.navigate(Screen.Bookmarks.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    })

                NavigationBarItem(icon = {
                    Icon(
                        Icons.Default.Camera,
                        contentDescription = stringResource(R.string.ocr_title)
                    )
                },
                    label = { Text(stringResource(R.string.ocr_title)) },
                    alwaysShowLabel = false,
                    selected = selectedScreen == Screen.Ocr,
                    onClick = {
                        selectedScreen = Screen.Ocr
                        navController.navigate(Screen.Ocr.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    })

                NavigationBarItem(icon = {
                    Icon(
                        Icons.Default.Translate,
                        contentDescription = stringResource(R.string.translate_title)
                    )
                },
                    label = { Text(stringResource(R.string.translate_title)) },
                    alwaysShowLabel = false,
                    selected = selectedScreen == Screen.Translation,
                    onClick = {
                        selectedScreen = Screen.Translation
                        navController.navigate(Screen.Translation.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    })
            }
        }
    }) { innerPadding ->
        NavigationGraph(
            ocrViewModel = ocrViewModel,
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            startCamera = startCamera,
            imageSelector = imageSelector
        )
    }

}