package com.venom.ui.components.speech

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.venom.resources.R
import com.venom.ui.components.common.PulseAnimation
import com.venom.ui.components.common.adp

@Composable
fun RecognitionBubble(
    text: String,
    isPartial: Boolean
) {
    Surface(
        color = if (isPartial)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(20.adp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.adp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.adp)
        ) {
            if (isPartial) {
                PulseAnimation(
                    modifier = Modifier.size(16.adp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Text(
                text = text.ifEmpty { stringResource(R.string.speech_state_listening) },
                style = MaterialTheme.typography.bodyLarge,
                color = if (isPartial)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}