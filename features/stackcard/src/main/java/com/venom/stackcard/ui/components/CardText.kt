package com.venom.stackcard.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.ui.components.common.PulseAnimation

@Composable
fun CardText(
    text: String, translationText: String?, isLoading: Boolean, isFlipped: Boolean
) {
    Spacer(modifier = Modifier.height(32.dp))
    Text(
        text = text,
        fontSize = 32.sp,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.headlineLarge
    )

    if (isFlipped) {
        when {
            isLoading -> PulseAnimation(size = 32f)
            translationText != null -> {
                Text(
                    text = translationText,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}