package com.venom.ui.components.common

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.data.model.TranslationEntry
import com.venom.domain.model.IHistoryEntry
import com.venom.ui.components.bars.BookMarkActionBar
import com.venom.ui.components.bars.HistoryItemHeader

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
    val interactionSource = remember { MutableInteractionSource() }

    val contentDescription = remember(entry) {
        "${if (entry.isBookmarked) "Bookmarked" else "Not bookmarked"} history item from ${entry.timestamp}"
    }

    LaunchedEffect(isExpanded) {
        if (isExpanded) {
            onItemClick()
        }
    }

    ElevatedCard(modifier = modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clip(RoundedCornerShape(12.dp))
        .clickable(
            interactionSource = interactionSource,
            indication = LocalIndication.current,
        ) {
            isExpanded = !isExpanded
        }
        .semantics {
            this.contentDescription = contentDescription
            onClick(label = "Toggle expanded state") { isExpanded = !isExpanded; true }
        }, shape = MaterialTheme.shapes.medium, elevation = CardDefaults.elevatedCardElevation(
        defaultElevation = 4.dp,
        pressedElevation = 6.dp,
        hoveredElevation = 8.dp,
    ), colors = CardDefaults.elevatedCardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface
    )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
        ) {
            HistoryItemHeader(
                timestamp = entry.timestamp,
                isBookmarked = entry.isBookmarked,
                onToggleBookmark = onToggleBookmark
            )

            content(isExpanded) { isExpanded = !isExpanded }

            BookMarkActionBar(
                onShareClick = onShareClick,
                onCopyClick = onCopyClick,
                onDeleteClick = onEntryRemove
            )
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