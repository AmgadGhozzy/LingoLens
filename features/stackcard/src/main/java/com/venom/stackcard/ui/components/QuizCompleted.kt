package com.venom.stackcard.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.venom.domain.model.QuizTestState
import com.venom.domain.model.WordLevels
import com.venom.resources.R
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@Composable
fun QuizCompleted(
    testState: QuizTestState.Completed,
    onComplete: (Boolean, WordLevels?) -> Unit,
    onRetry: (WordLevels) -> Unit,
    modifier: Modifier = Modifier
) {
    var showConfetti by remember { mutableStateOf(false) }

    LaunchedEffect(testState.passed) {
        showConfetti = testState.passed
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Konfetti view with lower z-index to prevent blocking
        if (showConfetti) {
            KonfettiView(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(0f),
                parties = listOf(
                    Party(
                        speed = 0f,
                        maxSpeed = 30f,
                        damping = 0.9f,
                        spread = 360,
                        colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                        position = Position.Relative(0.5, 0.3),
                        emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100)
                    )
                )
            )
        }

        // Main content with higher z-index
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .animateContentSize()
                .zIndex(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Result Icon
            AnimatedVisibility(
                visible = true,
                enter = scaleIn(initialScale = 0.5f) + fadeIn()
            ) {
                Icon(
                    imageVector = if (testState.passed) Icons.Rounded.CheckCircle
                    else Icons.Rounded.Info,
                    contentDescription = if (testState.passed) "Success Icon"
                    else "Info Icon",
                    modifier = Modifier
                        .size(80.dp)
                        .scale(
                            animateFloatAsState(
                                targetValue = if (testState.passed) 1.2f else 1f,
                                label = "iconScale"
                            ).value
                        ),
                    tint = if (testState.passed) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Result Text
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically { it / 2 } + fadeIn()
            ) {
                Text(
                    text = if (testState.passed) "Congratulations!" else "Keep Learning!",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Score Card
            ScoreCard(
                score = testState.levelPercentage,
                totalQuestions = testState.totalQuestions,
                isPassed = testState.passed,
                showAnimation = showConfetti
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons
            ActionButtons(
                testState = testState,
                onComplete = onComplete,
                onRetry = { onRetry(testState.level) }
            )

            // Next Level Text
            if (testState.passed && testState.nextLevel != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Next Level Unlocked!",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Lottie Confetti Animation
        AnimatedVisibility(
            visible = showConfetti,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .fillMaxSize()
                .zIndex(2f)
        ) {
            LottieAnimation(
                composition = rememberLottieComposition(
                    spec = LottieCompositionSpec.RawRes(R.raw.confetti)
                ).value,
                iterations = 3,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun ScoreCard(
    score: Float,
    totalQuestions: Int,
    isPassed: Boolean,
    showAnimation: Boolean
) {
    Card(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .scale(
                animateFloatAsState(
                    targetValue = if (showAnimation) 1.05f else 1f,
                    label = "scoreScale"
                ).value
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Final Score",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${(score * 100).toInt()}%",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            // Calculate correct answers based on score percentage
            val correctAnswers = (score * totalQuestions).toInt()

            Text(
                text = "$correctAnswers/$totalQuestions correct",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (isPassed) {
                LinearProgressIndicator(
                    progress = { score },
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer
                )
            }
        }
    }
}

@Composable
private fun ActionButtons(
    testState: QuizTestState.Completed,
    onComplete: (Boolean, WordLevels?) -> Unit,
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (testState.passed && testState.nextLevel != null) {
            Button(
                onClick = { onComplete(true, testState.nextLevel) },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Continue to Learning",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        OutlinedButton(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(56.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Try Again",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewQuizCompleted() {
    QuizCompleted(
        testState = QuizTestState.Completed(
            levelPercentage = 0.9f,
            totalQuestions = 10,
            passed = true,
            nextLevel = WordLevels.values().firstOrNull(),
            level = WordLevels.values().first()
        ),
        onComplete = { _, _ -> },
        onRetry = { }
    )
}