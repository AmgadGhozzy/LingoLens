package com.venom.settings.presentation.screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.venom.ui.components.common.CustomBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    onDismiss: () -> Unit,
    navController : NavHostController
) {
    CustomBottomSheet(
        onDismiss = onDismiss,
    ) {
        SettingsContent(onDismiss = onDismiss, navController = navController)
    }
}