package com.venom.stackcard.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.venom.domain.model.QuizTestState
import com.venom.domain.model.WordLevels
import com.venom.resources.R
import com.venom.ui.components.other.ConfettiView
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun QuizCompleted(
    testState: QuizTestState.Completed,
    onComplete: (Boolean, WordLevels?) -> Unit,
    onRetry: (WordLevels) -> Unit,
    onLearnClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showElements by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        showElements = true
        showConfetti = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = if (testState.passed) {
                        listOf(
                            Color(0xFF10B981).copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.surface
                        )
                    } else {
                        listOf(
                            Color(0xFFEF4444).copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.surface
                        )
                    }
                )
            )
    ) {
        if (showConfetti && testState.passed) {
            ConfettiView(modifier = Modifier.fillMaxSize())
        }

        AnimatedBackground(
            isPassed = testState.passed,
            isVisible = showElements
        )

        AnimatedVisibility(
            visible = showElements,
            enter = fadeIn(tween(600)) + scaleIn(tween(600))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                IconSection(isPassed = testState.passed)

                Spacer(modifier = Modifier.height(16.dp))

                TitleSection(
                    isPassed = testState.passed,
                    hasNextLevel = testState.nextLevel != null
                )

                Spacer(modifier = Modifier.height(48.dp))

                ScoreCard(
                    score = testState.levelPercentage,
                    totalQuestions = testState.totalQuestions,
                    isPassed = testState.passed
                )

                Spacer(modifier = Modifier.height(40.dp))

                ActionButtons(
                    testState = testState,
                    onComplete = onComplete,
                    onLearnClick = onLearnClick,
                    onRetry = { onRetry(testState.level) }
                )

                if (testState.nextLevel != null && testState.passed) {
                    Spacer(modifier = Modifier.height(32.dp))
                    NextLevelBadge(300)
                }
            }
        }

        AnimatedVisibility(
            visible = showConfetti && testState.passed,
            enter = fadeIn(animationSpec = tween(800)),
            exit = fadeOut(animationSpec = tween(400)),
            modifier = Modifier
                .fillMaxSize()
                .zIndex(2f)
        ) {
            LottieAnimation(
                composition = rememberLottieComposition(
                    spec = LottieCompositionSpec.RawRes(R.raw.confetti)
                ).value,
                iterations = 2,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun AnimatedBackground(
    isPassed: Boolean,
    isVisible: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(20000, easing = LinearEasing))
    )

    if (isVisible) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(-1f)
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val baseRadius = size.minDimension * 0.3f

            repeat(8) { index ->
                val angle = (index * 45f) * PI / 180f
                val radius = baseRadius + (index * 20f)
                val x = center.x + cos(angle + rotation * PI / 180f) * radius
                val y = center.y + sin(angle + rotation * PI / 180f) * radius

                drawCircle(
                    color = if (isPassed) Color(0xFF10B981) else Color(0xFFEF4444),
                    radius = 3f,
                    center = Offset(x.toFloat(), y.toFloat()),
                    alpha = 0.08f
                )
            }
        }
    }
}

@Composable
private fun IconSection(isPassed: Boolean) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(tween(2000, easing = EaseInOutSine), RepeatMode.Reverse)
    )

    val glow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(148.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width * 0.35f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = if (isPassed) {
                        listOf(
                            Color(0xFF10B981).copy(alpha = glow * 0.2f),
                            Color.Transparent
                        )
                    } else {
                        listOf(
                            Color(0xFFEF4444).copy(alpha = glow * 0.2f),
                            Color.Transparent
                        )
                    },
                    radius = radius * 2
                ),
                radius = radius * 2,
                center = center
            )
        }

        Icon(
            imageVector = if (isPassed) Icons.Rounded.CheckCircle else Icons.Rounded.Cancel,
            contentDescription = null,
            modifier = Modifier.size(96.dp).scale(pulseScale),
            tint = if (isPassed) Color(0xFF10B981) else Color(0xFFEF4444)
        )
    }
}

@Composable
private fun TitleSection(
    isPassed: Boolean,
    hasNextLevel: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isPassed) stringResource(R.string.congratulations) else stringResource(R.string.try_again),
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            ),
            color = if (isPassed) Color(0xFF10B981) else Color(0xFFEF4444),
            textAlign = TextAlign.Center
        )

        if (isPassed && hasNextLevel) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.level_completed),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ScoreCard(
    score: Float,
    totalQuestions: Int,
    isPassed: Boolean
) {
    val animatedScore by animateFloatAsState(
        targetValue = score,
        animationSpec = tween(1000)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            1.dp,
            if (isPassed) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFFEF4444).copy(alpha = 0.2f)
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = stringResource(R.string.score),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "${(animatedScore * 100).toInt()}",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 42.sp
                    ),
                    color = if (isPassed) Color(0xFF10B981) else Color(0xFFEF4444)
                )
                Text(
                    text = stringResource(R.string.percentage),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (isPassed) Color(0xFF10B981) else Color(0xFFEF4444),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val correctAnswers = (score * totalQuestions).toInt()
            Text(
                text = "$correctAnswers of $totalQuestions ${stringResource(R.string.correct_answers)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            LinearProgressIndicator(
                progress = { animatedScore },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (isPassed) Color(0xFF10B981) else Color(0xFFEF4444),
                trackColor = if (isPassed) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFEF4444).copy(alpha = 0.15f)
            )
        }
    }
}

@Composable
private fun ActionButtons(
    testState: QuizTestState.Completed,
    onComplete: (Boolean, WordLevels?) -> Unit,
    onLearnClick: () -> Unit,
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (testState.passed && testState.nextLevel != null) {
            PrimaryButton(
                text = stringResource(R.string.next_level),
                icon = Icons.AutoMirrored.Rounded.ArrowForward,
                onClick = { onComplete(true, testState.nextLevel) },
                isPassed = true
            )
        }

        if (!testState.passed) {
            PrimaryButton(
                text = stringResource(R.string.learn),
                icon = Icons.AutoMirrored.Rounded.MenuBook,
                onClick = onLearnClick,
                isPassed = false
            )
        }

        SecondaryButton(
            text = stringResource(R.string.retry),
            icon = Icons.Rounded.Refresh,
            onClick = onRetry,
            isPassed = testState.passed
        )
    }
}

@Composable
private fun PrimaryButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isPassed: Boolean
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(if (isPassed) 0xFF10B981 else 0xFFEF4444)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color.White
            )
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
private fun SecondaryButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isPassed: Boolean
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(
            1.5.dp,
            if (isPassed) Color(0xFF10B981).copy(alpha = 0.6f) else Color(0xFFEF4444).copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = if (isPassed) Color(0xFF10B981) else Color(0xFFEF4444)
            )
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = if (isPassed) Color(0xFF10B981) else Color(0xFFEF4444)
            )
        }
    }
}

@Composable
private fun NextLevelBadge(delay: Int) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delay.toLong())
        visible = true
    }

    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(tween(2000, easing = EaseInOutSine), RepeatMode.Reverse)
    )
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2500, easing = LinearEasing), RepeatMode.Restart)
    )

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(initialScale = 0.8f) + fadeIn(tween(800))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .graphicsLayer(
                    scaleX = pulseScale,
                    scaleY = pulseScale,
                    transformOrigin = TransformOrigin.Center
                )
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Color(0xFF10B981).copy(alpha = 0.1f)
                )
                .drawBehind {
                    val shimmerWidth = size.width * 0.3f
                    val shimmerX = (shimmerOffset + 1f) * (size.width + shimmerWidth) - shimmerWidth

                    drawRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFF10B981).copy(alpha = 0.2f),
                                Color.Transparent
                            ),
                            startX = shimmerX,
                            endX = shimmerX + shimmerWidth
                        ),
                        size = size
                    )
                }
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.LockOpen,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color(0xFF10B981)
            )
            Text(
                text = stringResource(R.string.next_level_unlocked),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                ),
                color = Color(0xFF10B981)
            )
            Icon(
                imageVector = Icons.Rounded.Stars,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color(0xFF10B981)
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
fun PreviewQuizCompleted() {
    QuizCompleted(
        testState = QuizTestState.Completed(
            levelPercentage = 0.94f,
            totalQuestions = 15,
            passed = true,
            nextLevel = WordLevels.values().firstOrNull(),
            level = WordLevels.values().first()
        ),
        onComplete = { _, _ -> },
        onRetry = { },
        onLearnClick = {}
    )
}