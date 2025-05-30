package com.venom.ui.components.buttons

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.venom.resources.R

@Composable
fun SpeechFilledButton(
    isSpeaking: Boolean,
    onSpeakClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Dp = 48.dp,
    shape: Shape = IconButtonDefaults.filledShape,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val colors = IconButtonDefaults.filledIconButtonColors(
        containerColor = if (isSpeaking)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
        contentColor = if (isSpeaking)
            MaterialTheme.colorScheme.onPrimaryContainer
        else
            MaterialTheme.colorScheme.onSurfaceVariant
    )

    FilledIconButton(
        onClick = onSpeakClick,
        modifier = modifier
            .scale(if (isSpeaking) 1.1f else 1f)
            .size(size),
        enabled = enabled && !isSpeaking,
        shape = shape,
        colors = colors,
        interactionSource = interactionSource
    ) {
        Icon(
            painter = painterResource(R.drawable.icon_sound),
            contentDescription = stringResource(
                if (isSpeaking) R.string.action_speaking
                else R.string.action_speak
            )
        )
    }
}

@Preview
@Composable
private fun SpeechFilledButtonPreview() {
    MaterialTheme {
        SpeechFilledButton(
            onSpeakClick = {},
            isSpeaking = false
        )
    }
}

@Preview
@Composable
private fun SpeechFilledButtonSpeakingPreview() {
    MaterialTheme {
        SpeechFilledButton(
            onSpeakClick = {},
            isSpeaking = true
        )
    }
}