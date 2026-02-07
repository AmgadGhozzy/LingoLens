package com.venom.stackcard.ui.components.mastery

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

enum class HapticStrength {
    LIGHT,
    MEDIUM,
    STRONG
}

@Composable
fun rememberHapticFeedback(): (HapticStrength) -> Unit {
    val view = LocalView.current

    return remember(view) {
        { strength: HapticStrength ->
            performHaptic(view, strength)
        }
    }
}

private fun performHaptic(view: View, strength: HapticStrength) {
    val feedbackConstant = when (strength) {
        HapticStrength.LIGHT -> HapticFeedbackConstants.CLOCK_TICK
        HapticStrength.MEDIUM -> HapticFeedbackConstants.VIRTUAL_KEY
        HapticStrength.STRONG -> HapticFeedbackConstants.LONG_PRESS
    }
    view.performHapticFeedback(feedbackConstant)
}
