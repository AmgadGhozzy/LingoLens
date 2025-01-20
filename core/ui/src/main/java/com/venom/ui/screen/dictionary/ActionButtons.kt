package com.venom.ui.screen.dictionary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.components.buttons.CopyButton
import com.venom.ui.components.buttons.CustomButton

@Composable
fun ActionButtons(
    text: String,
    onSpeak: (String) -> Unit,
    onCopy: (String) -> Unit,
    onShare: ((String) -> Unit),
    isSpeaking: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomButton(
            onClick = { onSpeak(text) },
            icon = if (isSpeaking) R.drawable.icon_record else R.drawable.icon_sound,
            contentDescription = stringResource(R.string.action_speak)
        )
        Spacer(modifier = Modifier.width(8.dp))
        CustomButton(
            onClick = { onShare(text) },
            icon = R.drawable.icon_share,
            contentDescription = stringResource(R.string.action_share)
        )
        Spacer(modifier = Modifier.width(8.dp))
        CopyButton(onClick = { onCopy(text) })
    }
}