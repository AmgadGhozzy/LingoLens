package com.venom.stackcard.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.components.other.ProgressBar
import com.venom.ui.components.other.TimerDisplay

@Composable
fun QuizHeader(
    currentQuestion: Int,
    totalQuestions: Int,
    hearts: Int,
    timeRemaining: Int,
) {

    var showHeartAnimation by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Favorite,
                    contentDescription = stringResource(R.string.hearts),
                    tint = if (showHeartAnimation) Color.Red else Color.Red.copy(alpha = 0.9f),
                    modifier = Modifier
                        .size(32.dp)
                        .scale(if (showHeartAnimation) 1.2f else 1f)
                )
                Text(
                    text = hearts.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (showHeartAnimation) {
                FloatingHeart()
            }
        }

        ProgressBar(
            progress = currentQuestion.toFloat() / totalQuestions,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "$currentQuestion/$totalQuestions",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        TimerDisplay(timeRemaining = timeRemaining)
    }
}