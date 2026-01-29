package com.venom.ui.theme.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.venom.ui.theme.BrandColors as B

@Immutable
data class QuizColors(
    val heart: Color,
    val heartContainer: Color,
    val score: Color,
    val scoreContainer: Color,
    val streak: Color,
    val streakContainer: Color,
    val streakHighlight: Color,
    val streakHighlightContainer: Color,
    val timer: Color,
    val timerContainer: Color,
    val timerWarning: Color,
    val timerWarningContainer: Color,
    val progressStart: Color,
    val progressEnd: Color,
    val progressBackground: Color,
    val buttonGradientStart: Color,
    val buttonGradientEnd: Color
)

val LightQuizColors = QuizColors(
    heart = B.Pink600,
    heartContainer = B.Pink600.copy(0.2f),
    score = B.Blue800,
    scoreContainer = B.Blue500.copy(0.2f),
    streak = B.Amber700,
    streakContainer = B.Amber500.copy(0.12f),
    streakHighlight = B.Amber800,
    streakHighlightContainer = B.Amber600.copy(0.18f),
    timer = B.Slate700,
    timerContainer = B.Black.copy(0.08f),
    timerWarning = B.Red700,
    timerWarningContainer = B.Red700.copy(0.18f),
    progressStart = B.Blue700,
    progressEnd = B.Cyan700,
    progressBackground = B.Black.copy(0.1f),
    buttonGradientStart = B.Blue800,
    buttonGradientEnd = B.Cyan800
)

val DarkQuizColors = QuizColors(
    heart = B.Pink500,
    heartContainer = B.Pink500.copy(0.25f),
    score = B.Blue500,
    scoreContainer = B.Blue500.copy(0.25f),
    streak = B.Amber500,
    streakContainer = B.Amber500.copy(0.18f),
    streakHighlight = B.Amber600,
    streakHighlightContainer = B.Amber600.copy(0.22f),
    timer = B.Slate200,
    timerContainer = B.White.copy(0.12f),
    timerWarning = B.Red400,
    timerWarningContainer = B.Red500.copy(0.22f),
    progressStart = B.Blue500,
    progressEnd = B.Cyan400,
    progressBackground = B.White.copy(0.15f),
    buttonGradientStart = B.Blue500,
    buttonGradientEnd = B.Cyan500
)