package com.venom.stackcard.ui.components.flashcard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.venom.domain.model.LanguageOption
import com.venom.domain.model.WordMaster
import com.venom.stackcard.ui.components.mastery.CardBack
import com.venom.stackcard.ui.components.mastery.CardFront

/**
 * Main Word Mastery card with 3D flip animation.
 *
 * Swipe Gesture Engine. All state is hoisted to the parent or ViewModel.
 *
 * Features:
 * - 3D Y-axis rotation flip animation (400ms duration)
 * - Front face shows English word and hints
 * - Back face shows Arabic translation and definitions
 * - Tap anywhere to flip
 *
 * @param word The word data to display
 * @param isFlipped Current flip state (false = front, true = back)
 * @param isBookmarked Current bookmark state
 * @param isHintRevealed Whether the blur hint on front is revealed
 * @param pinnedLanguage Optional language pinned to show on back
 * @param onFlip Callback when card is tapped to flip
 * @param onSpeak Callback for TTS with text and rate
 * @param onBookmarkToggle Callback when bookmark is toggled
 * @param onRevealHint Callback when hint reveal button is tapped
 * @param modifier Modifier for styling
 */
@Composable
fun WordCard(
    word: WordMaster,
    isFlipped: Boolean,
    animatedRotationY: Float,
    isBookmarked: Boolean,
    isHintRevealed: Boolean,
    pinnedLanguage: LanguageOption?,
    onFlip: () -> Unit,
    onSpeak: (text: String) -> Unit,
    onBookmarkToggle: () -> Unit,
    onRevealHint: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(32.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onFlip
            )
    ) {

        // FRONT
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = if (animatedRotationY <= 90f) 1f else 0f
                }
        ) {
            CardFront(
                word = word,
                isBookmarked = isBookmarked,
                isHintRevealed = isHintRevealed,
                onSpeak = onSpeak,
                onBookmarkToggle = onBookmarkToggle,
                onRevealHint = onRevealHint
            )
        }

        // BACK
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = 180f
                    alpha = if (animatedRotationY > 90f) 1f else 0f
                }
        ) {
            CardBack(
                word = word,
                isBookmarked = isBookmarked,
                pinnedLanguage = pinnedLanguage,
                onSpeak = onSpeak,
                onBookmarkToggle = onBookmarkToggle
            )
        }
    }
}
