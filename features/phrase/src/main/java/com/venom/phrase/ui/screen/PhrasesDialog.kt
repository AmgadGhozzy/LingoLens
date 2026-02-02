package com.venom.phrase.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.phrase.ui.viewmodel.PhraseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhrasesDialog(
    viewModel: PhraseViewModel = hiltViewModel(), 
    categoryId: Int, 
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss, 
        properties = DialogProperties(
            usePlatformDefaultWidth = false, 
            decorFitsSystemWindows = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            PhrasesScreen(
                viewModel = viewModel, 
                categoryId = categoryId, 
                onDismiss = onDismiss
            )
        }
    }
}
