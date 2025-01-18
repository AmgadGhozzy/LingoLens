package com.venom.phrase.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        DynamicStyledText(
            text = phrase.getTranslation(sourceLang),
            minFontSize = 20,
            maxFontSize = 28,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 100.dp)
        )

        ExpandCardActions(
            isBookmarked = isBookmarked,
            onBookmarkClick = onBookmarkClick,
            onSpeakClick = onSpeakClick,
            modifier = Modifier.align(Alignment.TopEnd)
        )
    }
}