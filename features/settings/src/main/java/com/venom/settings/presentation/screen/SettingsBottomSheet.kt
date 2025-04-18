package com.venom.settings.presentation.screen

import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.venom.ui.components.common.CustomBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    onDismiss: () -> Unit,
) {
    CustomBottomSheet(
        onDismiss = onDismiss,
    ) {
        SettingsContent(onDismiss = onDismiss)
    }
}