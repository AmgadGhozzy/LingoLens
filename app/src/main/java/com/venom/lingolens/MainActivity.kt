package com.venom.lingolens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.venom.lingopro.ui.screens.TranslationScreen
import com.venom.resources.R
import com.venom.textsnap.ui.screens.OcrScreen
import com.venom.ui.components.bars.TopBar
import com.venom.ui.components.buttons.CustomIcon
import com.venom.ui.theme.LingoLensTheme
import dagger.hilt.android.AndroidEntryPoint

sealed class Screen(val route: String) {
    object Translation : Screen("translation")
    object Ocr : Screen("ocr")
    object Bookmarks : Screen("bookmarks")
}

@Composable
fun LingoLensApp() {
    val navController = rememberNavController()
    var selectedScreen: Screen by remember { mutableStateOf(Screen.Translation) }

    Scaffold(topBar = {
        TopBar(title = "LingoLens", onNavigationClick = {
            navController.navigate(Screen.Translation.route) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }
        }, actions = {
            CustomIcon(icon = R.drawable.icon_history,
                contentDescription = stringResource(R.string.history_title),
                onClick = { })
            CustomIcon(icon = R.drawable.icon_bookmark_filled,
                contentDescription = stringResource(R.string.bookmarks_title),
                onClick = { })
        })
    }, bottomBar = {
        NavigationBar {
            NavigationBarItem(icon = {
                Icon(
                    Icons.Default.Bookmark, contentDescription = "Bookmarks"
                )
            },
                label = { Text("Bookmarks") },
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
                    Icons.Default.Camera, contentDescription = "OCR"
                )
            }, label = { Text("OCR") }, selected = selectedScreen == Screen.Ocr, onClick = {
                selectedScreen = Screen.Ocr
                navController.navigate(Screen.Ocr.route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            })

            NavigationBarItem(icon = {
                Icon(
                    Icons.Default.Translate, contentDescription = "Translation"
                )
            },
                label = { Text("Translate") },
                selected = selectedScreen == Screen.Translation,
                onClick = {
                    selectedScreen = Screen.Translation
                    navController.navigate(Screen.Translation.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                })

        }
    }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Translation.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Translation.route) {
                TranslationScreen(onNavigateToBookmarks = {
                    navController.navigate(Screen.Bookmarks.route)
                }, onNavigateToHistory = { }, onDismiss = {})
            }

            composable(Screen.Ocr.route) {
                OcrScreen(onFileClick = { },
                    onCameraClick = { },
                    onGalleryClick = { },
                    onNavigateBack = { navController.navigateUp() })
            }

            composable(Screen.Bookmarks.route) {}
        }
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LingoLensTheme {
                LingoLensApp()
            }
        }
//        val intent = Intent(this, com.venom.textsnap.MainActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        startActivity(intent)
    }
}