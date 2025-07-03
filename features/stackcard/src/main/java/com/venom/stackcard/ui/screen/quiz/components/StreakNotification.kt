package com.venom.stackcard.ui.screen.quiz.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.venom.stackcard.ui.screen.quiz.theme.ThemeColors

@Composable
fun StreakNotification(
    message: String,
    color: Color = ThemeColors.Secondary,
    contentColor: Color = ThemeColors.OnSecondary
) {
    Surface(
        color = color,
        contentColor = contentColor,
        shape = RoundedCornerShape(28.dp),
        shadowElevation = 12.dp,
        tonalElevation = 8.dp
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
