package com.venom.stackcard.ui.components.mastery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.domain.model.CefrLevel
import com.venom.domain.model.WordMaster
import com.venom.ui.theme.tokens.CefrColorScheme
import com.venom.ui.theme.tokens.DifficultyTheme

/**
 * Flashcard Header - REFINED VERSION
 *
 * Global color linking:
 * - primarySense chip strictly matches difficulty bar and rank number color
 *
 * @param word Word data for header information
 * @param cefrColors CEFR color scheme for badge
 * @param difficultyTheme Difficulty theme for semantic pill and rank
 * @param isBookmarked Current bookmark state
 * @param onBookmarkToggle Callback when bookmark is toggled
 * @param modifier Modifier for styling
 */
@Composable
fun FlashcardHeader(
    word: WordMaster,
    cefrColors: CefrColorScheme,
    difficultyTheme: DifficultyTheme,
    isBookmarked: Boolean,
    onBookmarkToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // Left: CEFR Badge
        CefrBadge(
            cefrLevel = word.cefrLevel,
            pos = word.pos,
            cefrColors = cefrColors,
            modifier = Modifier.align(Alignment.CenterStart)
        )

        // Center: Semantic Anchor (if exists)
        word.primarySense?.let { sense ->
            SemanticAnchorPill(
                text = sense,
                difficultyTheme = difficultyTheme,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Right: Bookmark
        BookmarkButton(
            isBookmarked = isBookmarked,
            onToggle = onBookmarkToggle,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .then(
                    if (!isBookmarked) {
                        Modifier.alpha(0.4f)
                    } else {
                        Modifier
                    }
                )
        )
    }
}

@Composable
private fun CefrBadge(
    cefrLevel: CefrLevel,
    pos: String,
    cefrColors: CefrColorScheme,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(cefrColors.background)
            .border(1.dp, cefrColors.border, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = cefrLevel.displayName,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Black,
                fontSize = 10.sp
            ),
            color = cefrColors.content
        )
        Box(
            modifier = Modifier
                .size(3.dp)
                .clip(CircleShape)
                .background(cefrColors.content.copy(0.5f))
        )
        Text(
            text = pos.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                fontSize = 10.sp
            ),
            color = cefrColors.content
        )
    }
}

@Composable
private fun SemanticAnchorPill(
    text: String,
    difficultyTheme: DifficultyTheme,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(difficultyTheme.background)
            .border(1.dp, difficultyTheme.border, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontSize = 9.sp
            ),
            color = difficultyTheme.text
        )
    }
}