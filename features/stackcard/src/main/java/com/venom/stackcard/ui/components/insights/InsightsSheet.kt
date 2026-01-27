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
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
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
import com.venom.ui.components.common.CustomDragHandle
import com.venom.ui.theme.LingoLensTheme
import kotlinx.coroutines.launch

/**
 * Bottom sheet modal for displaying word insights with enhanced UX.
 *
 * Features:
 * - Pull-down to dismiss gesture
 * - Horizontal swipe between tabs
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
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = activeTab.ordinal,
        pageCount = { InsightsTab.entries.size }
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            if (page != activeTab.ordinal) {
                onTabChange(InsightsTab.entries[page])
            }
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
            // Scrim
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClose
                    )
            )

            // Sheet content
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                SheetContent(
                    word = word,
                    pagerState = pagerState,
                    activeTab = activeTab,
                    pinnedLanguage = pinnedLanguage,
                    showPowerTip = showPowerTip,
                    onClose = onClose,
                    onTabChange = { tab ->
                        scope.launch {
                            pagerState.animateScrollToPage(tab.ordinal)
                        }
                    },
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
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val dismissThreshold = 250f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.88f)
            .graphicsLayer {
                translationY = dragOffset.coerceAtLeast(0f)
                alpha = (1f - dragOffset / dismissThreshold * 0.3f).coerceIn(0.7f, 1f)
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        if (dragOffset > dismissThreshold) {
                            onClose()
                        }
                        dragOffset = 0f
                    },
                    onDragCancel = { dragOffset = 0f },
                    onVerticalDrag = { _, dragAmount ->
                        // Only downward drag
                        if (dragAmount > 0 || dragOffset > 0) {
                            dragOffset = (dragOffset + dragAmount).coerceAtLeast(0f)
                        }
                    }
                )
            }
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Drag handle
        CustomDragHandle()

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp),
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
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.4f),
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.8f)
                ),
                size = 46.dp
            )
        }

        // Tabs
        InsightsTabs(
            activeTab = activeTab,
            onTabChange = onTabChange,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
        )

        // Pager content
        word?.let { currentWord ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                beyondViewportPageCount = 0,
                key = { it }
            ) { page ->
                when (InsightsTab.entries[page]) {
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

@Preview(showBackground = true, backgroundColor = 0xFF020617)
@Composable
private fun InsightsSheetPreview() {
    LingoLensTheme {
        InsightsSheet(
            isOpen = true,
            word = MockWordData.journeyWord,
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