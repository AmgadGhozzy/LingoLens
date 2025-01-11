package com.venom.phrase.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.venom.phrase.data.mapper.getTranslation
import com.venom.phrase.data.model.Phrase
import com.venom.ui.components.common.DynamicStyledText

@Composable
fun ExpandedContent(
    visible: Boolean,
    phrase: Phrase,
    targetLang: String,
    onCopyClick: () -> Unit,
    onShareClick: () -> Unit
) {
    AnimatedVisibility(
        visible = visible, enter = fadeIn() + expandVertically(
            expandFrom = Alignment.Top, animationSpec = tween(durationMillis = 300)
        ), exit = fadeOut() + shrinkVertically(
            shrinkTowards = Alignment.Top, animationSpec = tween(durationMillis = 200)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f)
            )

            // Translation
            DynamicStyledText(
                text = phrase.getTranslation(targetLang), modifier = Modifier.fillMaxWidth()
            )

            ActionButtons(
                onCopyClick = onCopyClick,
                onShareClick = onShareClick,
            )
        }
    }
}