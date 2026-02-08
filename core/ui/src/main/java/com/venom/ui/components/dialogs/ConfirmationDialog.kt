package com.venom.ui.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.venom.resources.R
import com.venom.ui.components.common.adp

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
            shape = MaterialTheme.shapes.medium, tonalElevation = 8.adp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.adp)
            ) {
                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.adp)
                )

                // Message
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.adp)
                )

                // Buttons Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.adp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = cancelButtonText)
                    }
                    Spacer(modifier = Modifier.width(8.adp))
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
    ConfirmationDialog(
        title = "Confirm Action",
        message = "Are you sure you want to perform this action?",
        confirmButtonText = "Yes",
        cancelButtonText = "No",
        onConfirm = { },
        onDismiss = { })
}
