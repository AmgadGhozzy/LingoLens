package com.venom.stackcard.ui.screen.quiz.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomFilledIconButton
import com.venom.ui.components.other.FloatingCircleMenu
import com.venom.ui.components.other.FloatingMenuItem
import com.venom.ui.theme.ThemeColors.BitcoinColor
import com.venom.ui.theme.ThemeColors.TONColor
import com.venom.ui.theme.ThemeColors.USDTColor
import kotlin.collections.listOf

@Composable
fun QuestionCard(
    question: String,
    translation: String,
    showTranslation: Boolean,
    onBookmark: () -> Unit = {},
    onLockInDictionary: () -> Unit = {},
    onSpeak: () -> Unit = {},
    onShare: () -> Unit = {}
) {

    Box(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(28.dp))
                .fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = question,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 32.sp
                )

                AnimatedVisibility(
                    visible = showTranslation,
                    enter = fadeIn(tween(400)) + expandVertically() + scaleIn(tween(400)),
                    exit = fadeOut(tween(200)) + shrinkVertically() + scaleOut(tween(200))
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = translation,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomFilledIconButton(
                        icon = R.drawable.icon_copy,
                        contentDescription = "Copy $question",
                        color = BitcoinColor,
                        onClick = {}
                    )

                    CustomFilledIconButton(
                        icon = Icons.AutoMirrored.Rounded.MenuBook,
                        contentDescription = "",
                        color = USDTColor,
                        onClick = {}
                    )

                    CustomFilledIconButton(
                        icon = R.drawable.icon_sound,
                        contentDescription = "Speech-to-text for $question",
                        color = TONColor,
                        onClick = onSpeak
                    )
                }
            }
        }

        FloatingCircleMenu(
            items = listOf(
                FloatingMenuItem(
                    icon = Icons.Rounded.Bookmark,
                    color = Color(0xFFFF6B35),
                    description = "Bookmark $question",
                    onClick = onBookmark
                ),
                FloatingMenuItem(
                    icon = Icons.Rounded.Lock,
                    color = Color(0xFF6C5CE7),
                    description = "Lock in dictionary for $question",
                    onClick = onLockInDictionary
                ),
                FloatingMenuItem(
                    icon = Icons.Rounded.VolumeUp,
                    color = Color(0xFF00B4D8),
                    description = "Speech-to-text for $question",
                    onClick = onSpeak
                ),
                FloatingMenuItem(
                    icon = Icons.Rounded.Share,
                    color = Color(0xFF06FFA5),
                    description = "Share $question and its answer",
                    onClick = onShare
                )
            ),
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}
