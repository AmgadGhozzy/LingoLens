package com.venom.quiz.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.theme.QuizColors

@Composable
fun OptionItem(
    option: String,
    label: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    isAnswered: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected && !isAnswered) 0.98f else 1f,
        animationSpec = spring(dampingRatio = 0.8f)
    )

    val backgroundColor = when {
        isAnswered && isSelected && isCorrect -> Brush.horizontalGradient(
            colors = listOf(
                QuizColors.OptionCorrectGradientStart,
                QuizColors.OptionCorrectGradientEnd
            )
        )
        isAnswered && isSelected && !isCorrect -> Brush.horizontalGradient(
            colors = listOf(
                QuizColors.OptionIncorrectGradientStart,
                QuizColors.OptionIncorrectGradientEnd
            )
        )
        isSelected -> Brush.horizontalGradient(
            colors = listOf(
                QuizColors.OptionSelectedGradientStart,
                QuizColors.OptionSelectedGradientEnd
            )
        )
        else -> Brush.horizontalGradient(
            colors = listOf(
                QuizColors.OptionDefaultGradient,
                QuizColors.OptionDefaultGradient
            )
        )
    }

    val borderColor = when {
        isAnswered && isSelected && isCorrect -> QuizColors.OptionCorrectBorder
        isAnswered && isSelected && !isCorrect -> QuizColors.OptionIncorrectBorder
        isSelected -> QuizColors.OptionSelectedBorder
        else -> QuizColors.OptionDefaultBorder
    }

    val borderWidth = when {
        isAnswered && isSelected -> 2.dp
        isSelected -> 2.dp
        else -> 1.dp
    }

    val labelBackgroundColor = when {
        isAnswered && isSelected && isCorrect -> QuizColors.OptionCorrectBorder
        isAnswered && isSelected && !isCorrect -> QuizColors.OptionIncorrectBorder
        isSelected -> QuizColors.OptionSelectedBorder
        else -> QuizColors.OptionDefaultBorder
    }

    val labelContent = when {
        isAnswered && isSelected && isCorrect -> stringResource(R.string.correct_symbol)
        isAnswered && isSelected && !isCorrect -> stringResource(R.string.incorrect_symbol)
        else -> label
    }

    Box(
        modifier = Modifier
            .fillMaxWidth(0.96f)
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .semantics {
                role = Role.Button
                contentDescription = "Option $label: $option"
            }
            .clickable(enabled = !isAnswered) { onClick() }
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(labelBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = labelContent,
                    color = QuizColors.OptionText,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = option,
                color = QuizColors.OptionText,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}