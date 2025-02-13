package com.venom.settings.presentation.components.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.venom.resources.R

/**
 * Base dialog component that provides a consistent design pattern for all dialogs in the application.
 *
 * @param title The title of the dialog
 * @param onDismiss Callback when dialog is dismissed
 * @param confirmButton Optional confirm button content
 * @param showConfirmButton Whether to show the confirm button
 * @param onConfirm Callback for confirm button click
 * @param confirmText Text for the confirm button (if shown)
 * @param dismissText Text for the dismiss button
 * @param icon Optional icon to show in the dialog
 * @param properties Dialog window properties
 * @param content Content of the dialog
 */
@Composable
fun BaseDialog(
    title: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    confirmButton: @Composable (() -> Unit)? = null,
    showConfirmButton: Boolean = true,
    onConfirm: (() -> Unit)? = null,
    confirmText: String = stringResource(id = R.string.dialog_confirm),
    dismissText: String = stringResource(id = R.string.dialog_cancel),
    icon: ImageVector? = null,
    properties: DialogProperties = DialogProperties(
        dismissOnBackPress = true,
        dismissOnClickOutside = true
    ),
    content: @Composable BoxScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = properties
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.7f),
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(top = 24.dp)
            ) {
                // Header section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Content section
                Box(
                    modifier = Modifier
                        .weight(1f, false)
                        .fillMaxWidth(), content = { content() }
                )

                // Buttons section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text(text = dismissText)
                    }

                    if (showConfirmButton) {
                        Spacer(modifier = Modifier.width(8.dp))
                        if (confirmButton != null) {
                            confirmButton()
                        } else if (onConfirm != null) {
                            TextButton(
                                onClick = onConfirm
                            ) {
                                Text(text = confirmText)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Radio option item for dialogs
 */
@Composable
fun DialogRadioOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .padding(horizontal = 24.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Scrollable content container for dialogs
 */
@Composable
fun DialogScrollableContent(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp)
    ) {
        item { content() }
    }
}