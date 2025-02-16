package com.venom.ui.components.other

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.venom.utils.Extensions.formatTime

@Composable
fun TimerDisplay(
    timeRemaining: Int,
    modifier: Modifier = Modifier
) {
    var isWarning by remember { mutableStateOf(false) }

    LaunchedEffect(timeRemaining) {
        isWarning = timeRemaining <= 10
    }

    Card(
        modifier = modifier
            .scale(if (isWarning) 1.1f else 1f),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isWarning -> Color.Red.copy(alpha = 0.1f)
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            width = 1.dp,
            color = when {
                isWarning -> Color.Red.copy(alpha = 0.5f)
                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            }
        )
    ) {
        Text(
            text = timeRemaining.formatTime(),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.titleMedium,
            color = when {
                isWarning -> Color.Red
                else -> MaterialTheme.colorScheme.primary
            }
        )
    }
}