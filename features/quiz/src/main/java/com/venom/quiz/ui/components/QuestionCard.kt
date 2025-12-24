package com.venom.quiz.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.domain.model.QuizMode
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomFilledIconButton
import com.venom.ui.components.other.FloatingCircleMenu
import com.venom.ui.components.other.FloatingMenuItem
import com.venom.ui.theme.QuizColors
import com.venom.ui.theme.ThemeColors.BitcoinColor
import com.venom.ui.theme.ThemeColors.PurplePrimary
import com.venom.ui.theme.ThemeColors.TONColor
import com.venom.ui.theme.ThemeColors.USDTColor

@Composable
fun QuestionCard(
    question: String,
    translation: String,
    showTranslation: Boolean,
    mode: QuizMode? = null,
    onBookmark: () -> Unit = {},
    onLockInDictionary: () -> Unit = {},
    onSpeak: () -> Unit = {},
    onShare: () -> Unit = {}
) {
    val isVocabularyMode = mode is QuizMode.Word

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            QuizColors.QuestionCardGradientStart,
                            QuizColors.QuestionCardGradientEnd
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = QuizColors.QuestionCardBorder,
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(
                    horizontal = 24.dp,
                    vertical = if (isVocabularyMode) 32.dp else 24.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (isVocabularyMode) {
                Text(
                    text = question,
                    fontSize = 32.sp,
                    lineHeight = 40.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = QuizColors.QuestionText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                )
            } else {
                var contentHeightPx by remember { mutableIntStateOf(200) }

                QuestionWebView(
                    html = question,
                    contentHeightPx = contentHeightPx,
                    onContentHeightPxChanged = { newHeight -> contentHeightPx = newHeight },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            AnimatedVisibility(
                visible = showTranslation,
                enter = fadeIn(tween(300)) + expandVertically() + scaleIn(tween(300)),
                exit = fadeOut(tween(200)) + shrinkVertically() + scaleOut(tween(200))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(QuizColors.TranslationBackground)
                        .padding(18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = translation,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        color = QuizColors.TranslationText
                    )
                }
            }

            if (isVocabularyMode) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomFilledIconButton(
                        icon = R.drawable.icon_copy,
                        contentDescription = "Copy $question",
                        color = BitcoinColor,
                        onClick = {}
                    )

                    Spacer(modifier = Modifier.width(12.dp))

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

                    Spacer(modifier = Modifier.width(12.dp))

                    CustomFilledIconButton(
                        icon = R.drawable.icon_sound,
                        contentDescription = stringResource(R.string.speech_to_text, question),
                        color = TONColor,
                        onClick = onSpeak
                    )
                }
            } else {
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