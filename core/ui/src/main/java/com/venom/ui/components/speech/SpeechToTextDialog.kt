package com.venom.ui.components.speech

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import com.venom.domain.model.SpeechState
import com.venom.ui.components.common.RequestMicrophonePermission
import com.venom.ui.components.dialogs.PermissionDeniedDialog
import com.venom.utils.Extensions.openAppSettings
import kotlinx.coroutines.flow.StateFlow

@Composable
fun SpeechToTextDialog(
    state: StateFlow<SpeechState>,
    onDismiss: () -> Unit,
    onStop: () -> Unit,
    onStart: () -> Unit,
    onPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    var dialogVisible by remember { mutableStateOf(false) }
    var permissionDenied by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Move RequestMicrophonePermission outside LaunchedEffect
    RequestMicrophonePermission { isGranted ->
        if (isGranted) {
            dialogVisible = true
            permissionDenied = false
            onStart()
        } else {
            permissionDenied = true
            onDismiss()
        }
    }

    if (permissionDenied) {
        PermissionDeniedDialog(
            onDismiss = onDismiss,
            onSettings = { context.openAppSettings() }
        )
        return
    }

    if (dialogVisible) {
        Dialog(
            onDismissRequest = {
                dialogVisible = false
                onDismiss()
            }
        ) {
            SpeechDialogContent(
                speechState = state,
                modifier = modifier,
                onDismiss = {
                    dialogVisible = false
                    onDismiss()
                },
                onStop = onStop,
                onStart = onStart,
                onPause = onPause
            )
        }
    }
}