package com.venom.ui.components.items

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
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
import com.venom.ui.components.bars.BookMarkActionButtons
import com.venom.ui.components.bars.HistoryItemHeader
import com.venom.ui.components.buttons.CustomButton
import com.venom.ui.components.sections.LanguageSection
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun TransHistoryItemView(
    entry: TranslationEntry,
    onEntryRemove: (TranslationEntry) -> Unit,
    onToggleBookmark: (TranslationEntry) -> Unit,
    onShareClick: (TranslationEntry) -> Unit,
    onCopyClick: (TranslationEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showCopiedToast by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            HistoryItemHeader(
                timestamp = entry.timestamp,
                isBookmarked = entry.isBookmarked,
                onToggleBookmark = { onToggleBookmark(entry) }
            )

            TranslationContent(
                sourceLang = entry.sourceLang,
                targetLang = entry.targetLang,
                sourceText = entry.sourceText,
                translatedText = entry.translatedText,
                synonyms = entry.synonyms,
                isExpanded = isExpanded,
                onExpandClick = { isExpanded = !isExpanded }
            )

            BookMarkActionButtons(
                onShareClick = { onShareClick(entry) },
                onCopyClick = {
                    onCopyClick(entry)
                    scope.launch {
                        showCopiedToast = true
                        delay(2000)
                        showCopiedToast = false
                    }
                },
                onDeleteClick = { onEntryRemove(entry) },
                showCopiedToast = showCopiedToast
            )
        }
    }
}

@Composable
private fun TranslationContent(
    sourceLang: String,
    targetLang: String,
    sourceText: String,
    translatedText: String,
    synonyms: List<String>?,
    isExpanded: Boolean,
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        LanguageSection(
            languageName = sourceLang,
            text = sourceText,
            isExpanded = isExpanded,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        TranslationArrow()

        Spacer(modifier = Modifier.height(8.dp))

        LanguageSection(
            languageName = targetLang,
            text = translatedText,
            isExpanded = isExpanded,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
        )

        if (!synonyms.isNullOrEmpty()) {
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                SynonymsSection(synonyms = synonyms)
            }
        }

        CustomButton(
            icon = if (isExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
            onClick = onExpandClick,
            contentDescription = stringResource(
                if (isExpanded) R.string.action_collapse
                else R.string.action_expand
            ),
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
private fun TranslationArrow() {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.ArrowDownward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun SynonymsSection(synonyms: List<String>) {
    Column(modifier = Modifier.padding(top = 12.dp)) {
        Text(
            text = stringResource(R.string.synonyms_label),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = synonyms.joinToString(", "),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
