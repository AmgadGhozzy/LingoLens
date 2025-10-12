package com.venom.ui.components.other

import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Stable
class SnackbarController(
    val hostState: SnackbarHostState,
    private val scope: CoroutineScope,
    private val context: android.content.Context
) {

    fun success(message: String) {
        show(message, duration = SnackbarDuration.Short)
    }

    fun success(@StringRes messageRes: Int) {
        success(context.getString(messageRes))
    }

    fun error(message: String) {
        show(message, duration = SnackbarDuration.Long)
    }

    fun error(@StringRes messageRes: Int) {
        error(context.getString(messageRes))
    }

    fun error(
        message: String,
        @StringRes actionLabelRes: Int,
        action: () -> Unit
    ) {
        show(
            message = message,
            actionLabel = context.getString(actionLabelRes),
            duration = SnackbarDuration.Long,
            action = action
        )
    }

    fun info(message: String) {
        show(message, duration = SnackbarDuration.Short)
    }

    fun info(@StringRes messageRes: Int) {
        info(context.getString(messageRes))
    }

    fun show(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short,
        action: (() -> Unit)? = null
    ) {
        scope.launch {
            val result = hostState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                duration = duration
            )
            if (result == SnackbarResult.ActionPerformed && action != null) {
                action()
            }
        }
    }

    fun dismiss() {
        hostState.currentSnackbarData?.dismiss()
    }
}

@Composable
fun rememberSnackbarController(): SnackbarController {
    val hostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    return remember(hostState, scope, context) {
        SnackbarController(hostState, scope, context)
    }
}

@Composable
fun SnackbarHost(controller: SnackbarController) {
    androidx.compose.material3.SnackbarHost(hostState = controller.hostState)
}