package com.venom.ui.screen.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.data.model.TranslationEntry
import com.venom.domain.model.IHistoryEntry

@Composable
fun HistoryItemView(
    entry: IHistoryEntry,
    onEntryRemove: () -> Unit,
    onToggleBookmark: () -> Unit,
    onShareClick: () -> Unit,
    onCopyClick: () -> Unit,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (Boolean, () -> Unit) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val expandedScale by animateFloatAsState(
        targetValue = if (isExpanded) 1.02f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium)
    )

    LaunchedEffect(isExpanded) {
        if (isExpanded) onItemClick()
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .scale(expandedScale)
            .clip(RoundedCornerShape(24.dp))
            .clickable { isExpanded = !isExpanded }
            .semantics {
                contentDescription =
                    "${if (entry.isBookmarked) "Bookmarked" else "Not bookmarked"} history item"
                onClick(label = "Toggle expanded state") { isExpanded = !isExpanded; true }
            },
        shape = RoundedCornerShape(24.dp),
        tonalElevation = if (isExpanded) 12.dp else 6.dp,
        shadowElevation = if (isExpanded) 16.dp else 8.dp,
        color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.95f)
    ) {
        Box(
            modifier = Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    )
            ) {
                HistoryItemHeader(
                    timestamp = entry.timestamp,
                    isBookmarked = entry.isBookmarked,
                    onToggleBookmark = onToggleBookmark
                )

                Spacer(modifier = Modifier.height(16.dp))

                content(isExpanded) { isExpanded = !isExpanded }

                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn(tween(300)) + expandVertically(tween(300)),
                    exit = fadeOut(tween(200)) + shrinkVertically(tween(200))
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(20.dp))
                        BookMarkActionBar(
                            onShareClick = onShareClick,
                            onCopyClick = onCopyClick,
                            onDeleteClick = onEntryRemove
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryItemViewPreview() {
    MaterialTheme {
        HistoryItemView(entry = TranslationEntry(
            sourceText = "Hello, world!",
            translatedText = "Привет, мир!",
            sourceLangName = "English",
            targetLangName = "Russian",
            sourceLangCode = "en",
            targetLangCode = "ru",
        ),
            onEntryRemove = {},
            onToggleBookmark = {},
            onShareClick = {},
            onCopyClick = {},
            onItemClick = {}) { isExpanded, onToggle ->
            Text(
                text = if (isExpanded) "Click to collapse" else "Click to expand",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}