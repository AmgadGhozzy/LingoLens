package com.venom.stackcard.ui.components.insights

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.data.mock.MockWordData
import com.venom.domain.model.AppTheme
import com.venom.domain.model.LanguageOption
import com.venom.domain.model.WordMaster
import com.venom.domain.model.getLanguageOptions
import com.venom.resources.R
import com.venom.stackcard.ui.components.mastery.InteractiveText
import com.venom.ui.theme.BrandColors
import com.venom.ui.theme.LingoLensTheme

/**
 * Languages tab content for Insights sheet.
 *
 * Displays a list of available language translations with:
 * - Dynamic sorting (pinned languages at top)
 *
 * @param word The word to get translations from
 * @param pinnedLanguage Currently pinned language (if any)
 * @param onPinLanguage Callback when a language is pinned/unpinned
 * @param onSpeak Callback for TTS with text and rate
 * @param modifier Modifier for styling
 */
@Composable
fun LanguagesTab(
    word: WordMaster,
    pinnedLanguage: LanguageOption?,
    onPinLanguage: (LanguageOption) -> Unit,
    onSpeak: (text: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Get all available languages and sort with pinned first
    val languages by remember(word, pinnedLanguage) {
        derivedStateOf {
            val allLanguages = getLanguageOptions(word)
            allLanguages.sortedByDescending { language ->
                pinnedLanguage?.langName == language.langName
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 24.dp)
            .padding(bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Info text
        Text(
            text = stringResource(R.string.mastery_pin_language_hint),
            style = MaterialTheme.typography.bodySmall.copy(
                lineHeight = 20.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
        )

        // Language list (sorted with pinned at top)
        languages.forEach { language ->
            if (language.translation != null) {
                LanguageRow(
                    language = language,
                    isPinned = pinnedLanguage?.langName == language.langName,
                    onPin = { onPinLanguage(language) },
                    onSpeak = onSpeak
                )
            }
        }
    }
}

/**
 * Language row with pinned state and glow effect
 */
@Composable
private fun LanguageRow(
    language: LanguageOption,
    isPinned: Boolean,
    onPin: () -> Unit,
    onSpeak: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Animated colors
    val backgroundColor by animateColorAsState(
        targetValue = if (isPinned) {
            BrandColors.Blue600.copy(0.15f)
        } else {
            MaterialTheme.colorScheme.surfaceContainerHigh
        },
        animationSpec = tween(250)
    )

    val borderColor by animateColorAsState(
        targetValue = if (isPinned) {
            BrandColors.Blue500.copy(0.4f)
        } else {
            MaterialTheme.colorScheme.outline.copy(0.2f)
        },
        animationSpec = tween(250)
    )

    val pinButtonBg by animateColorAsState(
        targetValue = if (isPinned) {
            BrandColors.Blue600
        } else {
            MaterialTheme.colorScheme.surfaceContainerHighest
        },
        animationSpec = tween(250)
    )

    val pinIconTint by animateColorAsState(
        targetValue = if (isPinned) {
            Color.White
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(250)
    )

    val pinButtonScale by animateFloatAsState(
        targetValue = if (isPinned) 1.05f else 1f,
        animationSpec = tween(250)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(
                width = if (isPinned) 1.5.dp else 0.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Language content
        InteractiveText(
            text = language.translation ?: "",
            onSpeak = onSpeak,
            modifier = Modifier.weight(1f)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                // Language name
                Text(
                    text = language.langName.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        fontSize = 9.sp
                    ),
                    color = if (isPinned) {
                        BrandColors.Blue600
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                    }
                )

                // Translation
                Text(
                    text = language.translation ?: "",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Pin button with scale animation
        Box(
            modifier = Modifier
                .size(44.dp)
                .scale(pinButtonScale)
                .clip(CircleShape)
                .background(pinButtonBg)
                .border(
                    width = if (isPinned) 1.5.dp else 0.5.dp,
                    color = if (isPinned) {
                        BrandColors.Blue500
                    } else {
                        MaterialTheme.colorScheme.outline.copy(0.2f)
                    },
                    shape = CircleShape
                )
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onPin
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(
                    id = if (isPinned) R.drawable.ic_push_pin_fill else R.drawable.ic_push_pin
                ),
                contentDescription = stringResource(
                    if (isPinned) R.string.mastery_unpin else R.string.mastery_pin
                ),
                modifier = Modifier.size(18.dp),
                tint = pinIconTint
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun LanguagesTabPreview() {
    LingoLensTheme(appTheme = AppTheme.DARK) {
        LanguagesTab(
            word = MockWordData.journeyWord,
            pinnedLanguage = LanguageOption("German", "Reise"),
            onPinLanguage = {},
            onSpeak = { _ -> }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F5F9)
@Composable
private fun LanguagesTabPreviewLight() {
    LingoLensTheme(appTheme = AppTheme.LIGHT) {
        LanguagesTab(
            word = MockWordData.journeyWord,
            pinnedLanguage = LanguageOption("French", "voyage"),
            onPinLanguage = {},
            onSpeak = { _ -> }
        )
    }
}

@Preview(showBackground = true, name = "No Pinned Language")
@Composable
private fun LanguagesTabNoPinPreview() {
    LingoLensTheme(appTheme = AppTheme.DARK) {
        LanguagesTab(
            word = MockWordData.journeyWord,
            pinnedLanguage = null,
            onPinLanguage = {},
            onSpeak = { _ -> }
        )
    }
}