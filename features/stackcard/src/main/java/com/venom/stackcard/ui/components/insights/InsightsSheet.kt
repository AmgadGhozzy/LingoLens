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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.util.lerp
import com.venom.domain.model.CefrLevel
import com.venom.domain.model.LanguageOption
import com.venom.domain.model.WordMaster
import com.venom.resources.R
import com.venom.stackcard.ui.viewmodel.CurrentWordProgress
import com.venom.ui.components.buttons.CloseButton
import com.venom.ui.components.common.CustomDragHandle
import com.venom.ui.components.common.adp
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun InsightsSheet(
    isOpen: Boolean,
    word: WordMaster?,
    currentWordProgress: CurrentWordProgress,
    userCefrLevel: CefrLevel,
    activeTab: InsightsTab,
    pinnedLanguage: LanguageOption?,
    showPowerTip: Boolean,
    onClose: () -> Unit,
    onTabChange: (InsightsTab) -> Unit,
    onPinLanguage: (LanguageOption) -> Unit,
    onTogglePowerTip: () -> Unit,
    modifier: Modifier = Modifier
) {
    var savedPageIndex by rememberSaveable { mutableIntStateOf(activeTab.ordinal) }

    val pagerState = rememberPagerState(
        initialPage = savedPageIndex,
        pageCount = { InsightsTab.entries.size }
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collect { page ->
                savedPageIndex = page
                val tab = InsightsTab.entries.getOrNull(page) ?: return@collect
                onTabChange(tab)
            }
    }

    AnimatedVisibility(
        visible = isOpen && word != null,
        enter = fadeIn(tween(250, easing = FastOutSlowInEasing)) +
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(350, easing = FastOutSlowInEasing)
                ),
        exit = fadeOut(tween(200)) +
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(280, easing = FastOutSlowInEasing)
                )
    ) {
        Box(modifier = modifier.fillMaxSize()) {
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
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                SheetContent(
                    word = word,
                    currentWordProgress = currentWordProgress,
                    userCefrLevel = userCefrLevel,
                    pagerState = pagerState,
                    pinnedLanguage = pinnedLanguage,
                    showPowerTip = showPowerTip,
                    onClose = onClose,
                    onPinLanguage = onPinLanguage,
                    onTogglePowerTip = onTogglePowerTip
                )
            }
        }
    }
}

@Composable
private fun SheetContent(
    word: WordMaster?,
    currentWordProgress: CurrentWordProgress,
    userCefrLevel: CefrLevel,
    pagerState: PagerState,
    pinnedLanguage: LanguageOption?,
    showPowerTip: Boolean,
    onClose: () -> Unit,
    onPinLanguage: (LanguageOption) -> Unit,
    onTogglePowerTip: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var dragOffsetY by remember { mutableFloatStateOf(0f) }
    val dismissThreshold = 300f
    val dismissNestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                return if (available.y > 0f && source == NestedScrollSource.UserInput) {
                    val consumed = available.y
                    dragOffsetY = (dragOffsetY + consumed).coerceAtLeast(0f)
                    Offset(x = 0f, y = consumed)
                } else {
                    Offset.Zero
                }
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (available.y < 0f && source == NestedScrollSource.UserInput) {
                    dragOffsetY = (dragOffsetY + available.y).coerceAtLeast(0f)
                    return Offset(x = 0f, y = available.y)
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                return if (dragOffsetY > 0f) {
                    if (dragOffsetY > dismissThreshold) onClose()
                    dragOffsetY = 0f
                    available
                } else {
                    Velocity.Zero
                }
            }
        }
    }

    val pagerScrollEnabled by remember { derivedStateOf { dragOffsetY == 0f } }

    val draggableState = rememberDraggableState { delta ->
        dragOffsetY = (dragOffsetY + delta).coerceAtLeast(0f)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.88f)
            .offset { IntOffset(x = 0, y = dragOffsetY.roundToInt()) }
            .clip(RoundedCornerShape(topStart = 28.adp, topEnd = 28.adp))
            .background(MaterialTheme.colorScheme.surface)
            .nestedScroll(dismissNestedScrollConnection)
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
            CloseButton(onClick = onClose, size = 40.adp)
        }

        val activeTabFromPager by remember {
            derivedStateOf {
                InsightsTab.entries.getOrElse(pagerState.currentPage) { InsightsTab.OVERVIEW }
            }
        }

        InsightsTabs(
            activeTab = activeTabFromPager,
            onTabChange = { tab ->
                coroutineScope.launch {
                    pagerState.animateScrollToPage(
                        page = tab.ordinal,
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    )
                }
            },
            modifier = Modifier.padding(horizontal = 24.adp, vertical = 8.adp)
        )

        word?.let { currentWord ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                beyondViewportPageCount = 1,
                userScrollEnabled = pagerScrollEnabled,
                key = { page -> page }
            ) { page ->
                val pageOffset =
                    (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                val fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = lerp(0.5f, 1f, fraction)
                            scaleX = lerp(0.95f, 1f, fraction)
                            scaleY = lerp(0.95f, 1f, fraction)
                        }
                ) {
                    when (InsightsTab.entries[page]) {
                        InsightsTab.OVERVIEW -> OverviewTab(
                            word = currentWord,
                            showPowerTip = showPowerTip,
                            onTogglePowerTip = onTogglePowerTip,
                            wordProgress = currentWordProgress
                        )

                        InsightsTab.RELATIONS -> RelationsTab(word = currentWord)
                        InsightsTab.USAGE -> UsageTab(
                            word = currentWord,
                            userCefrLevel = userCefrLevel
                        )

                        InsightsTab.LANGUAGES -> LanguagesTab(
                            word = currentWord,
                            pinnedLanguage = pinnedLanguage,
                            onPinLanguage = onPinLanguage
                        )
                    }
                }
            }
        }
    }
}