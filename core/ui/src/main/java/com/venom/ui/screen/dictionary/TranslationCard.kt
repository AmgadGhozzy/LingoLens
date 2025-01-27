package com.venom.ui.screen.dictionary

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.venom.data.model.TranslationResponse
import com.venom.ui.components.common.DynamicStyledText
import com.venom.ui.components.common.ExpandableCard

@Composable
fun TranslationCard(
    translationResponse: TranslationResponse,
    onSpeak: (String) -> Unit,
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sentence = translationResponse.sentences?.firstOrNull() ?: return

    ExpandableCard(
        title = sentence.trans,
        onSpeak = onSpeak,
        onCopy = onCopy,
        modifier = modifier,
        expandedContent = {
            sentence.translit?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            DynamicStyledText(
                text = sentence.orig,
                maxFontSize = 18,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    )
}
