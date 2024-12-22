package com.venom.ui.components.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.venom.resources.R

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmButtonText: String = stringResource(R.string.dialog_confirm),
    cancelButtonText: String = stringResource(R.string.dialog_cancel),
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium, tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Message
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Buttons Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = cancelButtonText)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = onConfirm) {
                        Text(text = confirmButtonText)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ConfirmationDialogPreview() {
    ConfirmationDialog(title = "Confirm Action",
        message = "Are you sure you want to perform this action?",
        confirmButtonText = "Yes",
        cancelButtonText = "No",
        onConfirm = { },
        onDismiss = { })
}
