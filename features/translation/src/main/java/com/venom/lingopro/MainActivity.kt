// MainActivity.kt
package com.venom.lingopro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.rememberNavController
import com.venom.lingopro.ui.screens.LingoProNavHost
import com.venom.ui.theme.LingoLensTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LingoLensTheme() {
                updateStatusBarColor(MaterialTheme.colorScheme.background, !isSystemInDarkTheme())
                val navController = rememberNavController()

                LingoProNavHost(navController = navController)
            }
        }
    }

    private fun updateStatusBarColor(color: Color, isDarkTheme: Boolean) {
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = isDarkTheme
        windowInsetsController.isAppearanceLightNavigationBars = isDarkTheme
        window.statusBarColor = color.toArgb()
        window.navigationBarColor = color.toArgb()
    }
}
