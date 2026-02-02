package com.venom.phrase.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.venom.ui.components.buttons.BookmarkFilledButton
import com.venom.ui.components.buttons.SpeechFilledButton
import com.venom.ui.components.common.adp

@Composable
fun ExpandCardActions(
    isSpeaking: Boolean,
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit,
    onSpeakClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.adp),
        modifier = modifier
    ) {
        SpeechFilledButton(
            isSpeaking = isSpeaking, 
            onSpeakClick = onSpeakClick
        )

        BookmarkFilledButton(
            isBookmarked = isBookmarked, 
            onToggleBookmark = onBookmarkClick
        )
    }
}
