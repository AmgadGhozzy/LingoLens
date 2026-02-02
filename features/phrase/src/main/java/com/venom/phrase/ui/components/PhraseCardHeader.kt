package com.venom.phrase.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.venom.phrase.data.mapper.getTranslation
import com.venom.phrase.data.model.PhraseEntity
import com.venom.ui.components.common.DynamicStyledText
import com.venom.ui.components.common.adp

@Composable
fun PhraseCardHeader(
    phrase: PhraseEntity,
    sourceLang: String,
    isSpeaking: Boolean,
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit,
    onSpeakClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        // Source language text - takes priority with weight
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.adp)
        ) {
            DynamicStyledText(
                text = phrase.getTranslation(sourceLang),
                minFontSize = 22,
                maxLines = 4,
                modifier = Modifier.fillMaxWidth()
            )
        }

        ExpandCardActions(
            isSpeaking = isSpeaking,
            isBookmarked = isBookmarked,
            onBookmarkClick = onBookmarkClick,
            onSpeakClick = onSpeakClick
        )
    }
}