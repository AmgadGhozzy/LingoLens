package com.venom.stackcard.ui.screen

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.ui.viewmodel.TTSViewModel
import com.venom.utils.Extensions.copyToClipboard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordBookmarksDialog(
    onDismiss: () -> Unit,
    ttsViewModel: TTSViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    Dialog(
        onDismissRequest = onDismiss, properties = DialogProperties(
            usePlatformDefaultWidth = false, decorFitsSystemWindows = false
        )
    ) {
        BookmarksScreen(
            onCopy = { context.copyToClipboard(it.englishEn) },
            onSpeak = { ttsViewModel.speak(it.englishEn) },
            onBackClick = onDismiss,
        )
    }
}
