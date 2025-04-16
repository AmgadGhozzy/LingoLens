package com.venom.stackcard.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
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
    onSpeakClick: () -> Unit = {},
    shape: Shape = MaterialTheme.shapes.extraLarge
) {
    // Separate animation specs with proper types
    val floatAnimSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )

    val sizeAnimSpec = spring<IntSize>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )

    // Combined visual feedback for active state
    val activeScale by animateFloatAsState(
        targetValue = if (isSpeaking) 1.01f else 1f,
        animationSpec = floatAnimSpec,
        label = "card scale"
    )

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .graphicsLayer {
                scaleX = activeScale
                scaleY = activeScale
            }
            .animateContentSize(sizeAnimSpec)
            .semantics {
                contentDescription = "Question card: $question" +
                        if (isSpeaking) ", currently speaking" else ""
            },
        shape = shape,
        colors = CardDefaults.elevatedCardColors(),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (isSpeaking) 8.dp else 4.dp,
            pressedElevation = if (isSpeaking) 8.dp else 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Question content section - pass the properly typed animation spec
            QuestionContent(
                question = question,
                translation = translation,
                showTranslation = showTranslation,
                isSpeaking = isSpeaking,
                animationSpec = floatAnimSpec  // This is now correctly typed
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
    showTranslation: Boolean,
    isSpeaking: Boolean,
    animationSpec: AnimationSpec<Float>
) {
    // Subtle pulse animation when speaking
    val textScale by animateFloatAsState(
        targetValue = if (isSpeaking) 1.02f else 1f,
        animationSpec = animationSpec,
        label = "text scale"
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Main question text
        DynamicStyledText(
            text = question,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = textScale
                    scaleY = textScale
                }
        )

        // Optional translation
        if (translation != null) {
            AnimatedVisibility(
                visible = showTranslation,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                DynamicStyledText(
                    text = translation,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
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
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SpeechFilledButton(
            isSpeaking = isSpeaking,
            onSpeakClick = onSpeakClick,
            modifier = Modifier.padding(end = 8.dp)
        )

        BookmarkFilledButton(
            isBookmarked = isBookmarked,
            onToggleBookmark = onBookmarkClick
        )
    }
}