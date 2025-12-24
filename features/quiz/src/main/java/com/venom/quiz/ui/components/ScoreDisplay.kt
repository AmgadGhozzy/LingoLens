package com.venom.quiz.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.theme.QuizColors
import com.venom.ui.theme.QuizColors.OnScoreBackground

@Composable
fun ScoreDisplay(score: Int) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(QuizColors.ScoreBackground)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Star,
            contentDescription = stringResource(R.string.score),
            tint = OnScoreBackground,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = score.toString(),
            color = OnScoreBackground,
            fontWeight = FontWeight.Medium
        )
    }
}
