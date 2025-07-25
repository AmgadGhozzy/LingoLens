package com.venom.stackcard.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.venom.ui.components.common.DynamicStyledText
import com.venom.ui.components.common.PulseAnimation
import com.venom.ui.components.other.TextChange

@Composable
fun CardText(
    text: String, translationText: String?, isLoading: Boolean, isFlipped: Boolean
) {
    Spacer(modifier = Modifier.height(32.dp))
    TextChange(text)
    if (isFlipped) {
        when {
            isLoading -> PulseAnimation(size = 32f)
            translationText != null -> {
                DynamicStyledText(text = translationText, textAlign = TextAlign.Center)
            }
        }
    }
}
