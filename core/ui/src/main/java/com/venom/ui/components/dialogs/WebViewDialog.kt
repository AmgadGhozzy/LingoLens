package com.venom.ui.components.dialogs

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.venom.ui.components.common.SettingsScaffold

@Composable
fun WebViewDialog(
    title: Int,
    assetPath: String,
    onDismiss: () -> Unit
) {

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        SettingsScaffold(
            title = title,
            contentPadding = PaddingValues(0.dp),
            onDismiss = onDismiss
        ) {
            item {
                Box(modifier = Modifier.fillMaxSize()) {
                    WebViewContent(assetPath = assetPath)
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun WebViewContent(
    assetPath: String,
) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                loadUrl("file:///android_asset/$assetPath")
            }
        },
        update = { webView ->
            webView.loadUrl("file:///android_asset/$assetPath")
        }
    )
}