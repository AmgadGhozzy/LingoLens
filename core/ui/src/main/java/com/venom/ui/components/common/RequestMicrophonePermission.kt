package com.venom.ui.components.common

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.venom.utils.PermissionUtils

@Composable
fun RequestMicrophonePermission(onPermissionResult: (Boolean) -> Unit) {
    val context = LocalContext.current
    val permissionUtils = remember { PermissionUtils(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        onPermissionResult(isGranted)
    }

    LaunchedEffect(Unit) {
        if (!permissionUtils.isMicrophonePermissionGranted()) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else {
            onPermissionResult(true)
        }
    }
}