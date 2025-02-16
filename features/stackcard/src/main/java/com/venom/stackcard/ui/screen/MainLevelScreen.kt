package com.venom.stackcard.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.domain.model.WordLevels
import com.venom.stackcard.ui.components.LevelCard
import com.venom.stackcard.ui.viewmodel.QuizViewModel

@Composable
fun MainLevelScreen(
    viewModel: QuizViewModel = hiltViewModel(),
    onNavigateToTest: (WordLevels) -> Unit,
    onNavigateToLearn: (WordLevels) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Language Learning",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(WordLevels.values()) { level ->
                LevelCard(
                    level = level,
                    isUnlocked = state.unlockedLevels.contains(level.id),
                    onTestClick = { onNavigateToTest(level) },
                    onLearnClick = { onNavigateToLearn(level) }
                )
            }
        }
    }
}
