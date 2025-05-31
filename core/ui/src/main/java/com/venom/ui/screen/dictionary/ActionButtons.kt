package com.venom.ui.screen.dictionary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.components.buttons.CopyButton
import com.venom.ui.components.buttons.CustomFilledIconButton
import com.venom.ui.components.buttons.SpeechFilledButton

@Composable
fun ActionButtons(
    modifier: Modifier = Modifier,
    text: String,
    onSpeak: (String) -> Unit,
    onCopy: (String) -> Unit,
    onShare: ((String) -> Unit),
    isSpeaking: Boolean = false,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {

        CopyButton(onClick = { onCopy(text) })
        Spacer(modifier = Modifier.width(8.dp))
        CustomFilledIconButton(
            icon = R.drawable.icon_share,
            onClick = { onShare(text) },
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                contentColor = MaterialTheme.colorScheme.primary
            ),
            contentDescription = stringResource(R.string.action_share)
        )
        Spacer(modifier = Modifier.width(8.dp))
        SpeechFilledButton(
            onSpeakClick = { onSpeak(text) },
            isSpeaking = isSpeaking
        )
    }
}