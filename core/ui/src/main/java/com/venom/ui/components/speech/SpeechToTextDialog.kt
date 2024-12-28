package com.venom.ui.components.speech

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.ui.components.common.RequestMicrophonePermission
import com.venom.ui.components.dialogs.PermissionDeniedDialog
import com.venom.ui.viewmodel.STTViewModel
import com.venom.utils.Extensions.openAppSettings

@Composable
fun SpeechToTextDialog(
    sttViewModel: STTViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var dialogVisible by remember { mutableStateOf(false) }
    var permissionDenied by remember { mutableStateOf(false) }
    val context = LocalContext.current

    RequestMicrophonePermission { isGranted ->
        if (isGranted) {
            dialogVisible = true
            permissionDenied = false
            sttViewModel.startRecognition()
        } else {
            permissionDenied = true
            sttViewModel.stopRecognition()
        }
    }

    if (permissionDenied) {
        PermissionDeniedDialog(onDismiss = onDismiss, onSettings = { context.openAppSettings() })
        return
    }

    if (dialogVisible) {
        Dialog(onDismissRequest = {
            dialogVisible = false
            onDismiss()
        }) {
            SpeechDialogContent(sttViewModel = sttViewModel, modifier = modifier, onDismiss = {
                dialogVisible = false
                sttViewModel.stopRecognition()
                onDismiss()
            })
        }
    }
}