package com.venom.stackcard.ui.screen.quiz.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomFilledIconButton
import com.venom.ui.components.other.FloatingCircleMenu
import com.venom.ui.components.other.FloatingMenuItem
import com.venom.ui.theme.ThemeColors.BitcoinColor
import com.venom.ui.theme.ThemeColors.PurplePrimary
import com.venom.ui.theme.ThemeColors.TONColor
import com.venom.ui.theme.ThemeColors.USDTColor

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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF3B82F6).copy(alpha = 0.2f),  // blue-500
                            Color(0xFF06B6D4).copy(alpha = 0.2f)   // cyan-500
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = question,
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.1f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = translation,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF64B5F6)
                )
            }

            // Action Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

                CustomFilledIconButton(
                    icon = R.drawable.icon_copy,
                    contentDescription = "Copy $question",
                    color = BitcoinColor,
                    onClick = {}
                )

                FloatingCircleMenu(
                    items = listOf(
                        FloatingMenuItem(
                            icon = Icons.Rounded.Bookmark,
                            color = USDTColor,
                            description = stringResource(R.string.bookmark_question, question),
                            onClick = onBookmark
                        ),
                        FloatingMenuItem(
                            icon = Icons.AutoMirrored.Rounded.MenuBook,
                            color = TONColor,
                            description = stringResource(R.string.lock_in_dictionary, question),
                            onClick = onLockInDictionary
                        ),
                        FloatingMenuItem(
                            icon = Icons.AutoMirrored.Rounded.VolumeUp,
                            color = BitcoinColor,
                            description = stringResource(R.string.speech_to_text, question),
                            onClick = onSpeak
                        ),
                        FloatingMenuItem(
                            icon = Icons.Rounded.Share,
                            color = PurplePrimary,
                            description = stringResource(R.string.share_question, question),
                            onClick = onShare
                        )
                    )
                )

                CustomFilledIconButton(
                    icon = R.drawable.icon_sound,
                    contentDescription = stringResource(R.string.speech_to_text, question),
                    color = TONColor,
                    onClick = onSpeak
                )
            }
        }
    }
}
