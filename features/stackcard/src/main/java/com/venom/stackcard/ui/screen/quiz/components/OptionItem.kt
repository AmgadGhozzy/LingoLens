package com.venom.stackcard.ui.screen.quiz.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.venom.resources.R

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
                Color(0xFF22C55E).copy(alpha = 0.3f),  // green-500
                Color(0xFF10B981).copy(alpha = 0.3f)   // emerald-500
            )
        )

        isAnswered && isSelected && !isCorrect -> Brush.horizontalGradient(
            colors = listOf(
                Color(0xFFEF4444).copy(alpha = 0.3f),  // red-500
                Color(0xFFEC4899).copy(alpha = 0.3f)   // pink-500
            )
        )

        isSelected -> Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF3B82F6).copy(alpha = 0.3f),  // blue-500
                Color(0xFF06B6D4).copy(alpha = 0.3f)   // cyan-500
            )
        )

        else -> Brush.horizontalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.1f),
                Color.White.copy(alpha = 0.1f)
            )
        )
    }

    val borderColor = when {
        isAnswered && isSelected && isCorrect -> Color(0xFF22C55E)  // green-500
        isAnswered && isSelected && !isCorrect -> Color(0xFFEF4444)  // red-500
        isSelected -> Color(0xFF3B82F6)  // blue-500
        else -> Color.White.copy(alpha = 0.2f)
    }

    val labelBackgroundColor = when {
        isAnswered && isSelected && isCorrect -> Color(0xFF22C55E)  // green-500
        isAnswered && isSelected && !isCorrect -> Color(0xFFEF4444)  // red-500
        isSelected -> Color(0xFF3B82F6)  // blue-500
        else -> Color.White.copy(alpha = 0.2f)
    }

    val labelContent = when {
        isAnswered && isSelected && isCorrect -> stringResource(R.string.correct_symbol)
        isAnswered && isSelected && !isCorrect -> stringResource(R.string.incorrect_symbol)
        else -> label
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(enabled = !isAnswered) { onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Option Label
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(labelBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = labelContent,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            // Option Text
            Text(
                text = option,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
