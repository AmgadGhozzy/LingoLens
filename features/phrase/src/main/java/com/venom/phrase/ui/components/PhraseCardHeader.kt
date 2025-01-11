package com.venom.phrase.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.venom.phrase.data.mapper.getTranslation
import com.venom.phrase.data.model.Phrase
import com.venom.ui.components.common.DynamicStyledText

@Composable
fun PhraseCardHeader(
    phrase: Phrase,
    sourceLang: String,
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit,
    onSpeakClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DynamicStyledText(
            text = phrase.getTranslation(sourceLang)
        )
        ExpandCardActions(
            isBookmarked = isBookmarked,
            onBookmarkClick = onBookmarkClick,
            onSpeakClick = onSpeakClick
        )
    }
}