package com.venom.stackcard.ui.components.insights

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.data.mock.MockWordData
import com.venom.domain.model.LanguageOption
import com.venom.domain.model.WordMaster
import com.venom.resources.R
import com.venom.stackcard.ui.viewmodel.InsightsTab
import com.venom.ui.components.buttons.CustomFilledIconButton
import com.venom.ui.theme.LingoLensTheme

/**
 * Bottom sheet modal for displaying word insights.
 *
 * Contains:
 * - Drag handle and header with close button
 * - Tab navigation (Overview, Relations, Usage, Languages)
 * - Scrollable content area for each tab
 *
 * @param isOpen Whether the sheet is visible
 * @param word The word to display insights for
 * @param activeTab Currently selected tab
 * @param pinnedLanguage Currently pinned language (for Languages tab)
 * @param showPowerTip Whether power tip flip is shown (for Overview tab)
 * @param onClose Callback to close the sheet
 * @param onTabChange Callback when tab selection changes
 * @param onPinLanguage Callback when a language is pinned/unpinned
 * @param onTogglePowerTip Callback to toggle power tip visibility
 * @param onSpeak Callback for TTS with text and rate
 * @param modifier Modifier for styling
 */
@Composable
fun InsightsSheet(
    isOpen: Boolean,
    word: WordMaster?,
    activeTab: InsightsTab,
    pinnedLanguage: LanguageOption?,
    showPowerTip: Boolean,
    onClose: () -> Unit,
    onTabChange: (InsightsTab) -> Unit,
    onPinLanguage: (LanguageOption) -> Unit,
    onTogglePowerTip: () -> Unit,
    onSpeak: (text: String) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isOpen && word != null,
        enter = fadeIn(tween(200)) + slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(
                durationMillis = 320,
                easing = FastOutSlowInEasing
            )
        ),
        exit = fadeOut(tween(200)) + slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(
                durationMillis = 320,
                easing = FastOutSlowInEasing
            )
        )
    ) {
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            // Scrim/backdrop
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colorScheme.scrim.copy(alpha = 0.45f)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClose
                    )
            )


            // Sheet content
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // Drag handle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(MaterialTheme.colorScheme.outline.copy(0.5f))
                            .clickable(onClick = onClose)
                    )
                }

                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.mastery_insights),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    CustomFilledIconButton(
                        icon = Icons.Rounded.Close,
                        onClick = onClose,
                        contentDescription = stringResource(R.string.action_close),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        size = 38.dp
                    )
                }

                // Tabs
                InsightsTabs(
                    activeTab = activeTab,
                    onTabChange = onTabChange,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )

                // Tab content
                word?.let { currentWord ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        when (activeTab) {
                            InsightsTab.OVERVIEW -> OverviewTab(
                                word = currentWord,
                                showPowerTip = showPowerTip,
                                onTogglePowerTip = onTogglePowerTip
                            )

                            InsightsTab.RELATIONS -> RelationsTab(
                                word = currentWord,
                                onSpeak = onSpeak
                            )

                            InsightsTab.USAGE -> UsageTab(
                                word = currentWord,
                                onSpeak = onSpeak
                            )

                            InsightsTab.LANGUAGES -> LanguagesTab(
                                word = currentWord,
                                pinnedLanguage = pinnedLanguage,
                                onPinLanguage = onPinLanguage,
                                onSpeak = onSpeak
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF020617)
@Composable
private fun InsightsSheetPreview() {
    LingoLensTheme {
        InsightsSheet(
            isOpen = true,
            word = MockWordData.venomWord,
            activeTab = InsightsTab.OVERVIEW,
            pinnedLanguage = null,
            showPowerTip = false,
            onClose = {},
            onTabChange = {},
            onPinLanguage = {},
            onTogglePowerTip = {},
            onSpeak = { _ -> }
        )
    }
}
