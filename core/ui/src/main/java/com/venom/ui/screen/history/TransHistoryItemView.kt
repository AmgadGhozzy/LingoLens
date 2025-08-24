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
import com.venom.data.model.LANGUAGES_LIST
import com.venom.data.model.TranslationEntity
import com.venom.resources.R
import com.venom.ui.components.buttons.ExpandIndicator

@Composable
fun TransHistoryItemView(
    entry: TranslationEntity,
    onEntryRemove: (TranslationEntity) -> Unit,
    onToggleBookmark: (TranslationEntity) -> Unit,
    onShareClick: (TranslationEntity) -> Unit,
    onCopyClick: (TranslationEntity) -> Unit,
    onItemClick: (TranslationEntity) -> Unit,
    isExpanded: Boolean, // New parameter: whether this item is expanded
    onExpandChange: (Boolean) -> Unit, // New parameter: callback when expansion changes
    modifier: Modifier = Modifier
) {
    HistoryItemView(
        entry = entry,
        onEntryRemove = { onEntryRemove(entry) },
        onToggleBookmark = { onToggleBookmark(entry) },
        onShareClick = { onShareClick(entry) },
        onCopyClick = { onCopyClick(entry) },
        onItemClick = { onItemClick(entry) },
        isExpanded = isExpanded,
        onExpandChange = onExpandChange,
        modifier = modifier
    ) { itemExpanded, onExpandClick ->
        TranslationContent(
            entry = entry,
            isExpanded = itemExpanded,
            onExpandClick = onExpandClick
        )
    }
}

@Composable
private fun TranslationContent(
    entry: TranslationEntity,
    onExpandClick: () -> Unit,
    isExpanded: Boolean,
    modifier: Modifier = Modifier
) {

    val sourceLanguage = LANGUAGES_LIST.find { it.code == entry.sourceLang }
    val targetLanguage = LANGUAGES_LIST.find { it.code == entry.targetLang }

    Column(modifier = modifier) {
        LangHistorySection(
            languageName = sourceLanguage?.englishName ?: entry.sourceLang,
            languageCode = entry.sourceLang,
            text = entry.sourceText,
            isExpanded = isExpanded,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        TranslationArrow()

        Spacer(modifier = Modifier.height(16.dp))

        LangHistorySection(
            languageName = targetLanguage?.englishName ?: entry.targetLang,
            languageCode = entry.targetLang,
            text = entry.translatedText,
            isExpanded = isExpanded,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
        )

        if (entry.synonyms.isNotEmpty()) {
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
                SynonymsSection(synonyms = entry.synonyms)
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
            color = MaterialTheme.colorScheme.primaryContainer.copy(0.4f),
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
        color = MaterialTheme.colorScheme.tertiaryContainer.copy(0.3f),
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