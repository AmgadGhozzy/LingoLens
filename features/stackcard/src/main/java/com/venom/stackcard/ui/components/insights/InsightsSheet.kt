package com.venom.stackcard.ui.components.insights

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.util.lerp
import com.venom.data.mock.MockWordData
import com.venom.domain.model.AppTheme
import com.venom.domain.model.LanguageOption
import com.venom.domain.model.WordMaster
import com.venom.resources.R
import com.venom.stackcard.ui.viewmodel.CurrentWordProgress
import com.venom.ui.components.buttons.CustomFilledIconButton
import com.venom.ui.components.common.CustomDragHandle
import com.venom.ui.components.common.adp
import com.venom.ui.theme.LingoLensTheme
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

/**
 * Bottom sheet modal for displaying word insights with enhanced UX.
 *
 * Features:
 * - Pull-down to dismiss gesture
 * - Horizontal swipe between tabs with smooth page transitions
 * - Smooth fade and scale animations during page swipes
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
    currentWordProgress: CurrentWordProgress,
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
    val pagerState = rememberPagerState(
        initialPage = activeTab.ordinal,
        pageCount = { InsightsTab.entries.size }
    )

    // Sync pager to activeTab
    LaunchedEffect(activeTab) {
        if (pagerState.currentPage != activeTab.ordinal) {
            pagerState.animateScrollToPage(
                page = activeTab.ordinal,
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            )
        }
    }

    // Pager swipe -> Tab
    val currentTabFromPager by remember {
        derivedStateOf { InsightsTab.entries.getOrNull(pagerState.currentPage) ?: activeTab }
    }
    LaunchedEffect(currentTabFromPager) {
        if (currentTabFromPager != activeTab) {
            onTabChange(currentTabFromPager)
        }
    }

    AnimatedVisibility(
        visible = isOpen && word != null,
        enter = fadeIn(tween(250, easing = FastOutSlowInEasing)) + slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(350, easing = FastOutSlowInEasing)
        ),
        exit = fadeOut(tween(200)) + slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(280, easing = FastOutSlowInEasing)
        )
    ) {
        Box(modifier = modifier.fillMaxSize()) {
            // Scrim / backdrop
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.3f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClose
                    )
            )

            // Sheet
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                SheetContent(
                    word = word,
                    currentWordProgress = currentWordProgress,
                    pagerState = pagerState,
                    activeTab = activeTab,
                    pinnedLanguage = pinnedLanguage,
                    showPowerTip = showPowerTip,
                    onClose = onClose,
                    onTabChange = onTabChange,
                    onPinLanguage = onPinLanguage,
                    onTogglePowerTip = onTogglePowerTip,
                    onSpeak = onSpeak
                )
            }
        }
    }
}

@Composable
private fun SheetContent(
    word: WordMaster?,
    currentWordProgress: CurrentWordProgress,
    pagerState: PagerState,
    activeTab: InsightsTab,
    pinnedLanguage: LanguageOption?,
    showPowerTip: Boolean,
    onClose: () -> Unit,
    onTabChange: (InsightsTab) -> Unit,
    onPinLanguage: (LanguageOption) -> Unit,
    onTogglePowerTip: () -> Unit,
    onSpeak: (text: String) -> Unit
) {
    var dragOffsetY by remember { mutableFloatStateOf(0f) }
    val dismissThreshold = 300f

    val alpha by animateFloatAsState(
        targetValue = 1f - (dragOffsetY / (dismissThreshold * 1.5f)).coerceIn(0f, 0.5f),
        animationSpec = tween(200)
    )

    val draggableState = rememberDraggableState { delta ->
        dragOffsetY = (dragOffsetY + delta).coerceAtLeast(0f)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.88f)
            .offset { IntOffset(0, dragOffsetY.roundToInt()) }
            .clip(RoundedCornerShape(topStart = 28.adp, topEnd = 28.adp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = alpha))
    ) {
        // Drag handle
        CustomDragHandle(
            modifier = Modifier
                .fillMaxWidth()
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Vertical,
                    onDragStopped = {
                        if (dragOffsetY > dismissThreshold) onClose()
                        dragOffsetY = 0f
                    }
                )
        )

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.adp, vertical = 4.adp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.mastery_insights),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            CustomFilledIconButton(
                icon = Icons.Rounded.Close,
                onClick = onClose,
                contentDescription = stringResource(R.string.action_close),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                size = 40.adp
            )
        }

        // Tabs
        InsightsTabs(
            activeTab = activeTab,
            onTabChange = onTabChange,
            modifier = Modifier.padding(horizontal = 24.adp, vertical = 8.adp)
        )

        // HorizontalPager
        word?.let { currentWord ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                beyondViewportPageCount = 1,
                userScrollEnabled = true,
                key = { it }
            ) { page ->

                // Smooth fade/scale effect
                val pageOffset =
                    (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                val alpha = lerp(0.5f, 1f, 1f - pageOffset.absoluteValue.coerceIn(0f, 1f))
                val scale = lerp(0.95f, 1f, 1f - pageOffset.absoluteValue.coerceIn(0f, 1f))

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            this.alpha = alpha
                            scaleX = scale
                            scaleY = scale
                        }
                ) {
                    when (InsightsTab.entries[page]) {
                        InsightsTab.OVERVIEW -> OverviewTab(
                            word = currentWord,
                            showPowerTip = showPowerTip,
                            onTogglePowerTip = onTogglePowerTip,
                            wordProgress = currentWordProgress
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

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun InsightsSheetPreview() {
    LingoLensTheme(appTheme = AppTheme.DARK) {
        InsightsSheet(
            isOpen = true,
            word = MockWordData.journeyWord,
            currentWordProgress = CurrentWordProgress(
                repetitions = 5,
                nextReviewText = "Tomorrow",
                masteryProgress = 0.65f
            ),
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

@Preview(showBackground = true, backgroundColor = 0xFFF1F5F9)
@Composable
private fun InsightsSheetPreviewLight() {
    LingoLensTheme(appTheme = AppTheme.LIGHT) {
        InsightsSheet(
            isOpen = true,
            word = MockWordData.journeyWord,
            currentWordProgress = CurrentWordProgress(
                repetitions = 5,
                nextReviewText = "Tomorrow",
                masteryProgress = 0.65f
            ),
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