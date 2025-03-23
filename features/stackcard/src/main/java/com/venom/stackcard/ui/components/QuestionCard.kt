package com.venom.stackcard.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.venom.ui.components.buttons.BookmarkFilledButton
import com.venom.ui.components.buttons.SpeechFilledButton
import com.venom.ui.components.common.DynamicStyledText

@Composable
fun QuestionCard(
    question: String,
    translation: String? = null,
    showTranslation: Boolean = false,
    isBookmarked: Boolean = false,
    isSpeaking: Boolean = false,
    onBookmarkClick: () -> Unit = {},
    onSpeakClick: () -> Unit = {}
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Question content section
            QuestionContent(
                question = question,
                translation = translation,
                showTranslation = showTranslation
            )

            // Action buttons row
            ActionButtons(
                isBookmarked = isBookmarked,
                isSpeaking = isSpeaking,
                onBookmarkClick = onBookmarkClick,
                onSpeakClick = onSpeakClick
            )
        }
    }
}

@Composable
private fun QuestionContent(
    question: String,
    translation: String?,
    showTranslation: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Main question text
        DynamicStyledText(
            text = question,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Optional translation with animation
        if (translation != null) {
            AnimatedVisibility(visible = showTranslation) {
                DynamicStyledText(
                    text = translation,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ActionButtons(
    isBookmarked: Boolean,
    isSpeaking: Boolean,
    onBookmarkClick: () -> Unit,
    onSpeakClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(
            space = 8.dp, alignment = Alignment.End
        ), verticalAlignment = Alignment.CenterVertically
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