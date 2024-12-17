package com.venom.lingopro.ui.components.dialogs

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlin.math.roundToInt

/**
 * A [Dialog] that can be moved around by dragging it with a finger.
 *
 * @param onDismissRequest will be called when the user clicks outside the dialog or presses the back button.
 * @param properties the properties for the dialog. The default properties are:
 * - [DialogProperties.usePlatformDefaultWidth] is false.
 * - [DialogProperties.decorFitsSystemWindows] is false.
 * - [DialogProperties.dismissOnBackPress] is true.
 * - [DialogProperties.dismissOnClickOutside] is false.
 * @param modifier the modifier for the dialog.
 * @param content the content of the dialog.
 */
@Composable
fun DraggableDialog(
    onDismissRequest: () -> Unit, properties: DialogProperties = DialogProperties(
        usePlatformDefaultWidth = false,
        decorFitsSystemWindows = false,
        dismissOnBackPress = true,
        dismissOnClickOutside = false
    ), modifier: Modifier = Modifier, content: @Composable () -> Unit
) {
    var offset by remember { mutableStateOf(Offset.Zero) }

    Dialog(
        onDismissRequest = onDismissRequest, properties = properties
    ) {
        Box(modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 180.dp)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offset += dragAmount
                }
            }
            .offset {
                IntOffset(
                    x = offset.x.roundToInt(), y = offset.y.roundToInt()
                )
            }) {
            content()
        }
    }
}
