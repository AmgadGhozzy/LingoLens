package com.venom.phrase.ui.screen


import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.phrase.ui.viewmodel.PhraseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhrasesDialog(
    viewModel: PhraseViewModel = hiltViewModel(), categoryId: Int, onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss, properties = DialogProperties(
            usePlatformDefaultWidth = false, decorFitsSystemWindows = false
        )
    ) {
        PhrasesScreen(
            viewModel = viewModel, categoryId = categoryId
        )
    }
}
