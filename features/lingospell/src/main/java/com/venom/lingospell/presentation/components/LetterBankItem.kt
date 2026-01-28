package com.venom.lingospell.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.domain.model.AppTheme
import com.venom.lingospell.domain.Letter
import com.venom.lingospell.domain.LetterStatus
import com.venom.ui.theme.LingoLensTheme
import com.venom.ui.theme.lingoLens

/**
 * Single letter tile in the letter bank.
 *
 * Color Usage (Semantic):
 * - Background: surfaceTile
 * - Text: textSecondary
 * - Border: outline (subtle)
 * - Shadow: shadowPrimary
 * - Used state placeholder: outlineVariant
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LetterBankItem(
    letter: Letter,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current

    val shadowColor = MaterialTheme.lingoLens.feature.spelling.shadowPrimary

    Box(
        modifier = modifier.aspectRatio(1f / 1.1f),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = letter.status == LetterStatus.AVAILABLE,
            enter = fadeIn() + scaleIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                initialScale = 0.5f
            ),
            exit = fadeOut() + scaleOut(targetScale = 0f)
        ) {
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()

            val baseElevation = 4.dp
            val pressedElevation = 1.dp
            val elevation = if (isPressed) pressedElevation else baseElevation

            val offsetY by animateDpAsState(
                targetValue = if (isPressed) 4.dp else 0.dp,
                animationSpec = spring(stiffness = Spring.StiffnessHigh),
                label = "letterOffsetY"
            )

            Box(
                modifier = Modifier
                    .offset(y = offsetY)
                    .shadow(
                        elevation = elevation,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = shadowColor.copy(0.5f),
                        spotColor = shadowColor
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.lingoLens.feature.spelling.surfaceTile)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(0.5f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .combinedClickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick,
                        onLongClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            onLongClick()
                        }
                    )
                    .aspectRatio(1f / 1.1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letter.char.toString(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Empty footprint when letter is used
        AnimatedVisibility(
            visible = letter.status == LetterStatus.USED,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        MaterialTheme.colorScheme.outlineVariant.copy(0.5f),
                        CircleShape
                    )
            )
        }
    }
}

@Preview(name = "Light - Available")
@Composable
private fun LetterBankItemLightPreview() {
    LingoLensTheme(appTheme = AppTheme.LIGHT) {
        LetterBankItem(
            letter = Letter(id = "1", char = 'A', status = LetterStatus.AVAILABLE),
            onClick = {},
            modifier = Modifier.size(60.dp, 66.dp)
        )
    }
}

@Preview(name = "Dark - Used")
@Composable
private fun LetterBankItemDarkPreview() {
    LingoLensTheme(appTheme = AppTheme.DARK) {
        LetterBankItem(
            letter = Letter(id = "1", char = 'B', status = LetterStatus.USED),
            onClick = {},
            modifier = Modifier.size(60.dp, 66.dp)
        )
    }
}
