package com.venom.stackcard.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.domain.model.WordLevels
import com.venom.resources.R
import com.venom.stackcard.ui.components.LevelCard
import com.venom.stackcard.ui.viewmodel.QuizViewModel
import kotlinx.coroutines.delay

@Composable
fun MainLevelScreen(
    viewModel: QuizViewModel = hiltViewModel(),
    onNavigateToTest: (WordLevels) -> Unit,
    onNavigateToLearn: (WordLevels) -> Unit
) {
    val state by viewModel.state.collectAsState()
    var contentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        viewModel.refreshProgress()
        contentVisible = true
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        AnimatedVisibility(
            visible = contentVisible,
            enter = fadeIn()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header section with icon
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.title_language_learning),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Description text
                Text(
                    text = stringResource(R.string.description_master_vocab),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(WordLevels.values()) { index, level ->
                        var cardVisible by remember { mutableStateOf(false) }

                        LaunchedEffect(key1 = Unit) {
                            delay(100L * index)
                            cardVisible = true
                        }

                        AnimatedVisibility(
                            visible = cardVisible,
                            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 5 })
                        ) {
                            LevelCard(
                                level = level,
                                isUnlocked = state.unlockedLevels.contains(level.id),
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
}