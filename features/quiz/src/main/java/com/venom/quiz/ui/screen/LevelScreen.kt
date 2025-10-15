package com.venom.quiz.ui.screen

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.domain.model.WordLevels
import com.venom.quiz.ui.viewmodel.QuizViewModel
import com.venom.resources.R
import com.venom.ui.theme.ThemeColors.Blue
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLevelScreen(
    viewModel: QuizViewModel = hiltViewModel(LocalActivity.current as ComponentActivity),
    onNavigateToTest: (WordLevels) -> Unit,
    onNavigateToLearn: (WordLevels) -> Unit
) {
    val state by viewModel.state.collectAsState()
    var contentVisible by remember { mutableStateOf(false) }
    var expandedCardIndex by remember { mutableStateOf<Int?>(null) }
    var hasAutoScrolled by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val listState = rememberLazyListState()

    val topBarContentColor = MaterialTheme.colorScheme.onSurface
    val topBarSecondaryColor = MaterialTheme.colorScheme.onSurface.copy(0.8f)

    LaunchedEffect(state.currentLevel) {
        viewModel.refreshProgress()
        contentVisible = true
    }

    // Auto-scroll and expand current level card
    LaunchedEffect(contentVisible, state.currentLevel) {
        if (contentVisible && !hasAutoScrolled) {
            val targetIndex = WordLevels.values().indexOfFirst { it.id == state.currentLevel.id }

            when {
                targetIndex > 3 -> {
                    delay(300)

                    // Collapse the top bar
                    scrollBehavior.state.heightOffset = scrollBehavior.state.heightOffsetLimit

                    // Scroll to current level with smooth animation
                    listState.animateScrollToItem(
                        index = targetIndex,
                        scrollOffset = 100
                    )

                    delay(300)

                    // Expand the current level card
                    expandedCardIndex = targetIndex
                }

                targetIndex in 0..3 -> {
                    delay(800)
                    expandedCardIndex = targetIndex
                }
            }
            hasAutoScrolled = true
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                scrollBehavior = scrollBehavior,
                title = {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = Blue
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Rounded.School,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = stringResource(R.string.learning_language),
                                style = MaterialTheme.typography.headlineMedium,
                                color = topBarContentColor,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Animate subtitle visibility based on collapse fraction
                        AnimatedVisibility(
                            visible = scrollBehavior.state.collapsedFraction < 0.5f,
                            enter = fadeIn(tween(300)) + expandVertically(tween(300)),
                            exit = fadeOut(tween(200)) + shrinkVertically(tween(200))
                        ) {
                            Column {
                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = stringResource(R.string.level_progress),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = topBarSecondaryColor,
                                    fontWeight = FontWeight.Normal,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                },
                actions = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Study time stat
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Blue.copy(0.2f),
                            border = BorderStroke(1.dp, Blue.copy(0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    horizontal = 12.dp,
                                    vertical = 6.dp
                                ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Timer,
                                    contentDescription = null,
                                    tint = Blue,
                                    modifier = Modifier.size(16.dp)
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Text(
                                    text = "2,680",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Blue,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Words learned stat
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF10B981).copy(0.2f),
                            border = BorderStroke(1.dp, Color(0xFF10B981).copy(0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    horizontal = 12.dp,
                                    vertical = 6.dp
                                ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Star,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(16.dp)
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Text(
                                    text = "8h 30m",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                    titleContentColor = topBarContentColor,
                    actionIconContentColor = topBarContentColor
                )
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        AnimatedVisibility(
            visible = contentVisible,
            enter = fadeIn(tween(600))
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(
                    top = 16.dp,
                    bottom = 100.dp
                )
            ) {
                itemsIndexed(WordLevels.values()) { index, level ->
                    val shouldExpand = expandedCardIndex == index

                    AnimatedVisibility(
                        visible = contentVisible,
                        enter = fadeIn(
                            tween(500)
                        ) + slideInVertically(
                            initialOffsetY = { it / 3 },
                            animationSpec = tween(
                                700,
                                delayMillis = index * 100,
                                easing = EaseOutQuart
                            )
                        )
                    ) {
                        LevelCard(
                            level = level,
                            isUnlocked = state.unlockedLevels.contains(level.id),
                            isExpanded = shouldExpand,
                            progress = state.levelProgress[level.id] ?: 0f,
                            onTestClick = { onNavigateToTest(level) },
                            onLearnClick = { onNavigateToLearn(level) },
                            onExpandToggle = {
                                expandedCardIndex =
                                    if (expandedCardIndex == index) null else index
                            }
                        )
                    }
                }
            }
        }
    }
}