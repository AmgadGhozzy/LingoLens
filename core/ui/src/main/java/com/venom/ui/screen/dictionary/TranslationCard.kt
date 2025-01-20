package com.venom.ui.screen.dictionary

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.venom.data.model.TranslationResponse
import com.venom.resources.R
import com.venom.ui.components.buttons.ExpandIndicator
import com.venom.ui.components.common.DynamicStyledText

@Composable
fun TranslationCard(
    translationResponse: TranslationResponse,
    onSpeak: (String) -> Unit,
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val sentence = translationResponse.sentences?.firstOrNull() ?: return

    ElevatedCard(
        modifier = modifier,
        onClick = { expanded = !expanded },
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Main Translation
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DynamicStyledText(
                    text = sentence.trans,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ActionIconButton(
                        iconRes = R.drawable.icon_sound,
                        contentDescription = R.string.action_speak,
                        onClick = { onSpeak(sentence.trans) }
                    )
                    ActionIconButton(
                        iconRes = R.drawable.icon_copy,
                        contentDescription = R.string.action_copy,
                        onClick = { onCopy(sentence.trans) }
                    )
                }
            }

            // Expanded Content
            AnimatedVisibility(visible = expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    sentence.translit?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                    DynamicStyledText(
                        text = sentence.orig,
                        maxFontSize = 18,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                    )
                }
            }

            ExpandIndicator(
                expanded = expanded,
                onClick = { expanded = !expanded },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun ActionIconButton(
    iconRes: Int, contentDescription: Int, onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f))
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = stringResource(contentDescription),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}