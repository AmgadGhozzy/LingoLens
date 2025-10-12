package com.venom.lingopro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.venom.lingopro.ui.screens.TranslationScreen
import com.venom.ui.theme.LingoLensTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuickTrActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val selectedText = extractSelectedText()

        if (selectedText.isNullOrBlank()) {
            Log.w("QuickTrActivity", "No text selected or extracted")
            finish()
            return
        }

        setContent {
            LingoLensTheme {
                TranslationScreen(
                    isDialog = true,
                    onDismiss = { finish() },
                    initialText = selectedText,
                    onOpenInApp = { openInMainApp() }
                )
            }
        }
    }

    // Extract selected text from various intent sources
    private fun extractSelectedText(): String? {
        return intent?.run {
            getStringExtra(Intent.EXTRA_TEXT) ?: getStringExtra(Intent.EXTRA_PROCESS_TEXT)?.trim()
        }?.takeIf { it.isNotBlank() }
    }

    private fun openInMainApp() {
        try {
            val mainActivityIntent = Intent().apply {
                setClassName(packageName, "com.venom.lingolens.MainActivity")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(mainActivityIntent)
            finish()
        } catch (e: Exception) {
            Log.e("QuickTrActivity", "Error opening main app", e)
        }
    }
}
