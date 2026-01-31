package com.venom.stackcard.ui.components.flashcard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.venom.domain.model.LanguageOption
import com.venom.domain.model.WordMaster
import com.venom.stackcard.ui.components.mastery.CardBack
import com.venom.stackcard.ui.components.mastery.CardFront

/**
 * 3D flip card using alpha + zIndex for smooth visibility transitions.
 *
 * GLITCH-FREE APPROACH:
 * - Both faces always composed (no recomposition at 90°)
 * - Alpha controls GPU visibility (0 or 1, no animation)
 * - zIndex controls touch priority (visible face on top)
 *
 * @param word WordMaster instance with all card data
 * @param isFlipped Boolean state (for external logic)
 * @param animatedRotationY Current Y-axis rotation (0° = front, 180° = back)
 * @param isBookmarked Bookmark state
 * @param isHintRevealed Whether blur hint on front is revealed
 * @param pinnedLanguage Optional language to show on back
 * @param onFlip Callback when card is flipped
 * @param onSpeak TTS callback
 * @param onBookmarkToggle Bookmark toggle callback
 * @param onRevealHint Hint reveal callback
 */
@Composable
fun WordCard(
    word: WordMaster,
    animatedRotationY: Float,
    isBookmarked: Boolean,
    isHintRevealed: Boolean,
    pinnedLanguage: LanguageOption?,
    onSpeak: (text: String) -> Unit,
    onBookmarkToggle: () -> Unit,
    onRevealHint: () -> Unit,
    modifier: Modifier = Modifier
) {
    val normalizedRotation = animatedRotationY % 360f
    val effectiveRotation = if (normalizedRotation < 0) normalizedRotation + 360f else normalizedRotation
    val showFront = effectiveRotation <= 90f || effectiveRotation >= 270f

    Box(modifier = modifier.clip(RoundedCornerShape(32.dp))) {
        // Front face
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(if (showFront) 1f else 0f)
                .graphicsLayer { alpha = if (showFront) 1f else 0f }
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

        // Back face
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(if (showFront) 0f else 1f)
                .graphicsLayer {
                    alpha = if (!showFront) 1f else 0f
                    rotationY = 180f
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