package com.venom.quiz.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.theme.QuizColors

@Composable
fun HeartsDisplay(hearts: Int) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(QuizColors.HeartBackground)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val scale = remember { Animatable(1f) }
        LaunchedEffect(hearts) {
            scale.animateTo(
                targetValue = 1.4f,
                animationSpec = tween(150, easing = FastOutSlowInEasing)
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(150, easing = FastOutSlowInEasing)
            )
        }

        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = stringResource(R.string.hearts_description),
            tint = QuizColors.HeartIcon,
            modifier = Modifier
                .size(16.dp)
                .scale(scale.value)
        )

        Text(
            text = hearts.toString(),
            color = QuizColors.HeartIcon,
            fontWeight = FontWeight.Medium
        )
    }
}