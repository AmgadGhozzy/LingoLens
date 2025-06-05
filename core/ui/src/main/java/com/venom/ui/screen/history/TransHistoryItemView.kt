package com.venom.ui.screen.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.data.model.TranslationEntry
import com.venom.resources.R
import com.venom.ui.components.buttons.ExpandIndicator

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
    HistoryItemView(
        entry = entry,
        onEntryRemove = { onEntryRemove(entry) },
        onToggleBookmark = { onToggleBookmark(entry) },
        onShareClick = { onShareClick(entry) },
        onCopyClick = { onCopyClick(entry) },
        onItemClick = { onItemClick(entry) },
        modifier = modifier
    ) { isExpanded, onExpandClick ->
        TranslationContent(
            entry = entry,
            isExpanded = isExpanded,
            onExpandClick = onExpandClick
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
        LangHistorySection(
            languageName = entry.sourceLangName,
            languageCode = entry.sourceLangCode,
            text = entry.sourceText,
            isExpanded = isExpanded,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        TranslationArrow()

        Spacer(modifier = Modifier.height(16.dp))

        LangHistorySection(
            languageName = entry.targetLangName,
            languageCode = entry.targetLangCode,
            text = entry.translatedText,
            isExpanded = isExpanded,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
        )

        if (!entry.synonyms.isNullOrEmpty()) {
            ExpandIndicator(
                expanded = isExpanded,
                onClick = onExpandClick,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(tween(400)) + expandVertically(tween(400)),
                exit = fadeOut(tween(300)) + shrinkVertically(tween(300))
            ) {
                SynonymsSection(synonyms = entry.synonyms!!)
            }

        }
    }
}

@Composable
private fun TranslationArrow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            shape = CircleShape,
            tonalElevation = 4.dp
        ) {
            Box(
                modifier = Modifier
                    .padding(12.dp)
                    .size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowDownward,
                    contentDescription = stringResource(R.string.expand_translation),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun SynonymsSection(synonyms: List<String>) {
    Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.synonyms_label),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = synonyms.joinToString(" • "),
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
