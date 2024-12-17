package com.venom.lingopro.ui.components.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.lingopro.data.model.TranslationEntry
import com.venom.lingopro.ui.components.buttons.CustomIcon

@Composable
fun BookmarkHistoryItemView(
    bookmark: TranslationEntry,
    onBookmarkRemove: (TranslationEntry) -> Unit = {},
    onShareClick: (TranslationEntry) -> Unit = {},
    onCopyClick: (TranslationEntry) -> Unit = {}
) {
    var isBookmarked by remember { mutableStateOf(true) }
    val clipboardManager = LocalClipboardManager.current


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(12.dp)
    ) {
        // Source Language Section
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = bookmark.sourceLang,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = bookmark.targetLang.take(2).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }

        // Source Text
        Text(
            text = bookmark.sourceText,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        HorizontalDivider(
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Target Language Section
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = bookmark.targetLang,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = bookmark.targetLang.take(2).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }

        // Translated Text
        Text(
            text = bookmark.translatedText,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Action Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bookmark Toggle
            CustomIcon(
                icon = if (isBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                contentDescription = "Remove Bookmark",
                onClick = {
                    isBookmarked = !isBookmarked
                    if (!isBookmarked) onBookmarkRemove(bookmark)
                },
                selectedTint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Copy Action
            CustomIcon(
                icon = Icons.Rounded.ContentCopy,
                contentDescription = "Copy Translation",
                onClick = {},
                selectedTint = if (isBookmarked) Color.Green else MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Share Action
            CustomIcon(icon = Icons.Rounded.Share,
                contentDescription = "Share Translation",
                onClick = { onShareClick(bookmark) })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookmarkItemViewPreview() {
    MaterialTheme {
        BookmarkHistoryItemView(
            bookmark = TranslationEntry(
                sourceText = "Hello, how are you?",
                translatedText = "Bonjour, comment allez-vous?",
                sourceLang = "English",
                targetLang = "French"
            )
        )
    }
}