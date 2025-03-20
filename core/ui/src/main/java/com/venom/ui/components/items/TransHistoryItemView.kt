package com.venom.ui.components.items

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.venom.data.model.TranslationEntry
import com.venom.resources.R
import com.venom.ui.components.buttons.ExpandIndicator
import com.venom.ui.components.common.HistoryItemView
import com.venom.ui.components.sections.LangHistorySection

@Composable
fun TransHistoryItemView(
    entry: TranslationEntry,
    onEntryRemove: (TranslationEntry) -> Unit,
    onToggleBookmark: (TranslationEntry) -> Unit,
    onShareClick: (TranslationEntry) -> Unit,
    onCopyClick: (TranslationEntry) -> Unit,
    onItemClick: (TranslationEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    HistoryItemView(entry = entry,
        onEntryRemove = { onEntryRemove(entry) },
        onToggleBookmark = { onToggleBookmark(entry) },
        onShareClick = { onShareClick(entry) },
        onCopyClick = { onCopyClick(entry) },
        onItemClick = { onItemClick(entry) }) { isExpanded, onExpandClick ->
        TranslationContent(
            entry = entry,
            isExpanded = isExpanded,
            onExpandClick = onExpandClick,
            modifier = modifier
        )
    }
}

@Composable
private fun TranslationContent(
    entry: TranslationEntry,
    onExpandClick: () -> Unit,
    isExpanded: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Source
        LangHistorySection(
            languageName = entry.sourceLangName,
            languageCode = entry.sourceLangCode,
            text = entry.sourceText,
            isExpanded = isExpanded,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        TranslationArrow()

        // Target
        LangHistorySection(
            languageName = entry.targetLangName,
            languageCode = entry.targetLangCode,
            text = entry.translatedText,
            isExpanded = isExpanded,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
        )

        if (!entry.synonyms.isNullOrEmpty()) {
            AnimatedVisibility(
                visible = isExpanded, enter = fadeIn() + expandVertically(
                    expandFrom = Alignment.Top, animationSpec = tween(durationMillis = 300)
                ), exit = fadeOut() + shrinkVertically(
                    shrinkTowards = Alignment.Top, animationSpec = tween(durationMillis = 200)
                )
            ) {
                SynonymsSection(synonyms = entry.synonyms!!)
            }
            ExpandIndicator(
                expanded = isExpanded,
                onClick = onExpandClick,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun TranslationArrow() {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.background.copy(alpha = 0.5f), CircleShape
                )
                .padding(6.dp)
                .size(18.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowDownward,
                contentDescription = stringResource(R.string.expand_translation),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun SynonymsSection(synonyms: List<String>) {
    Column {
        Text(
            text = stringResource(R.string.synonyms_label),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 12.dp)
        )
        Text(
            text = synonyms.joinToString(", "),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}
