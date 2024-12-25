package com.venom.ui.components.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.venom.ui.components.common.ImageCropper

@Composable
fun ImageCropperDialog(
    onDismissRequest: () -> Unit,
    imageBitmap: ImageBitmap,
    onImageCropped: (ImageBitmap) -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false,)
    ) {
        ImageCropper(
            imageBitmap = imageBitmap,
            onImageCropped = onImageCropped,
        )
    }
}