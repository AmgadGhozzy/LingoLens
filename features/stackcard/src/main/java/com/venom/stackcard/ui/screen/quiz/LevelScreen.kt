package com.venom.stackcard.ui.screen.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.domain.model.WordLevels
import com.venom.stackcard.ui.screen.quiz.components.LevelCard
import com.venom.stackcard.ui.viewmodel.QuizViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLevelScreen(
    viewModel: QuizViewModel = hiltViewModel(),
    onNavigateToTest: (WordLevels) -> Unit,
    onNavigateToLearn: (WordLevels) -> Unit
) {
    val state by viewModel.state.collectAsState()
    var contentVisible by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(Unit) {
        viewModel.refreshProgress()
        contentVisible = true
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF111827),
                        Color(0xFF1F2937)
                    )
                )
            ),
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
                                color = Color(0xFF3B82F6)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Rounded.School,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = "تعلم اللغة",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Animate subtitle visibility based on collapse fraction
                        AnimatedVisibility(
                            visible = scrollBehavior.state.collapsedFraction < 0.5f,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column {
                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "أتقن المفردات عبر مستويات المهارة المختلفة",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.8f),
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
                        // Words learned stat
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF3B82F6).copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Timer,
                                    contentDescription = null,
                                    tint = Color(0xFF3B82F6),
                                    modifier = Modifier.size(16.dp)
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Text(
                                    text = "2,680",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF3B82F6),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Study time stat
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Star,
                                    contentDescription = null,
                                    tint = Color(0xFF3B82F6),
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
                    scrolledContainerColor = Color(0xFF111827).copy(alpha = 0.95f),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        AnimatedVisibility(
            visible = contentVisible,
            enter = fadeIn(tween(800))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(bottom = 32.dp, top = 16.dp)
            ) {
                itemsIndexed(WordLevels.values()) { index, level ->
                    var cardVisible by remember { mutableStateOf(false) }

                    LaunchedEffect(key1 = Unit) {
                        delay(100L * index)
                        cardVisible = true
                    }

                    AnimatedVisibility(
                        visible = cardVisible,
                        enter = fadeIn(tween(600)) + slideInVertically(
                            initialOffsetY = { it / 3 },
                            animationSpec = tween(600, easing = EaseOutQuart)
                        )
                    ) {
                        LevelCard(
                            level = level,
                            isUnlocked = state.unlockedLevels.contains(level.id),
                            isCurrentLevel = state.currentLevel.id == level.id,
                            progress = state.levelProgress[level.id] ?: 0f,
                            onTestClick = { onNavigateToTest(level) },
                            onLearnClick = { onNavigateToLearn(level) }
                        )
                    }
                }
            }
        }
    }
}
