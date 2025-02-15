package com.venom.phrase.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.venom.ui.components.buttons.BookmarkFilledButton
import com.venom.ui.components.buttons.SpeechFilledButton

@Composable
fun ExpandCardActions(
    isSpeaking: Boolean,
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit,
    onSpeakClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = modifier) {
        SpeechFilledButton(
            isSpeaking = isSpeaking, onSpeakClick = onSpeakClick
        )

        BookmarkFilledButton(
            isBookmarked = isBookmarked, onToggleBookmark = onBookmarkClick
        )
    }
}
