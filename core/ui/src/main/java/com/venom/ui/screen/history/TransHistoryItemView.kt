package com.venom.ui.screen.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.venom.data.model.LANGUAGES_LIST
import com.venom.domain.model.TranslationResult
import com.venom.resources.R
import com.venom.ui.components.buttons.ExpandIndicator
import com.venom.ui.components.common.adp
import com.venom.ui.components.common.asp

@Composable
fun TransHistoryItemView(
    entry: TranslationResult,
    onEntryRemove: (TranslationResult) -> Unit,
    onToggleBookmark: (TranslationResult) -> Unit,
    onShareClick: (TranslationResult) -> Unit,
    onCopyClick: (TranslationResult) -> Unit,
    onOpenInNew: (TranslationResult) -> Unit,
    isExpanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    HistoryItemView(
        entry = entry,
        onEntryRemove = { onEntryRemove(entry) },
        onToggleBookmark = { onToggleBookmark(entry) },
        onShareClick = { onShareClick(entry) },
        onCopyClick = { onCopyClick(entry) },
        onOpenInNew = { onOpenInNew(entry) },
        isExpanded = isExpanded,
        onExpandChange = onExpandChange,
        modifier = modifier
    ) { itemExpanded, onExpandClick ->
        TranslationContent(entry, itemExpanded, onExpandClick)
    }
}

@Composable
private fun TranslationContent(
    entry: TranslationResult,
    isExpanded: Boolean,
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sourceLang = LANGUAGES_LIST.find { it.code == entry.sourceLang }
    val targetLang = LANGUAGES_LIST.find { it.code == entry.targetLang }

    Column(modifier = modifier) {
        LangHistorySection(
            languageName = sourceLang?.englishName ?: entry.sourceLang,
            languageCode = entry.sourceLang,
            text = entry.sourceText,
            isExpanded = isExpanded,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(16.adp))
        LangHistorySection(
            languageName = targetLang?.englishName ?: entry.targetLang,
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
        }
        AnimatedVisibility(
            visible = isExpanded && entry.synonyms.isNotEmpty(),
            enter = fadeIn(tween(400)) + expandVertically(tween(400)),
            exit = fadeOut(tween(300)) + shrinkVertically(tween(300))
        ) {
            SynonymsSection(entry.synonyms)
        }
    }
}

@Composable
private fun SynonymsSection(synonyms: List<String>) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(16.adp),
        tonalElevation = 2.adp
    ) {
        Column(Modifier.padding(16.adp)) {
            Text(
                text = stringResource(R.string.synonyms_label),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(bottom = 8.adp)
            )
            Text(
                text = synonyms.take(3).joinToString(" • "),
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.asp),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}