package com.venom.settings.presentation.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit
) {
    Scaffold(
    ) { paddingValues ->
        SettingContent(onDismiss = onNavigateBack, modifier = modifier.padding(paddingValues))
    }
}
