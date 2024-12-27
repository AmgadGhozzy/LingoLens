package com.venom.ui.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PermDeviceInformation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@Composable
fun PermissionDeniedDialog(
    onDismiss: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        icon = {
            Icon(
                imageVector = Icons.Rounded.PermDeviceInformation,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                text = "Microphone Access Needed",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "The microphone permission is required to enable voice recognition features in this app.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "You can grant this permission in your device settings.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onSettings, colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Open Settings", style = MaterialTheme.typography.labelLarge
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss, colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(
                    text = "Not Now", style = MaterialTheme.typography.labelLarge
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shape = MaterialTheme.shapes.medium,
        properties = DialogProperties(
            dismissOnBackPress = true, dismissOnClickOutside = true
        )
    )
}
