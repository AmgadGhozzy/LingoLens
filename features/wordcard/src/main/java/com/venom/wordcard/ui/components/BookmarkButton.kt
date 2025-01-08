package com.venom.wordcard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.venom.wordcard.data.model.WordEntity

@Composable
fun BookmarkButton(card: WordEntity, onBookmark: () -> Unit) {
    IconButton(
        onClick = onBookmark,
        modifier = Modifier
            .padding(16.dp)
            .size(48.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.9f))
    ) {
        Icon(
            imageVector = Icons.Rounded.BookmarkBorder,
            contentDescription = "Bookmark word ${card.englishWord}",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}
