package com.venom.stackcard.ui.screen.quiz.components

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
import com.venom.stackcard.ui.screen.quiz.theme.ThemeColors
import com.venom.stackcard.ui.screen.quiz.theme.ThemeColors.HeartBackground
import kotlinx.coroutines.delay

@Composable
fun HeartsDisplay(
    hearts: Int
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(HeartBackground.copy(alpha = 0.2f))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val scale = remember { Animatable(1f) }
        LaunchedEffect(key1 = true) {
            scale.animateTo(
                targetValue = 1.5f,
                animationSpec = tween(200, easing = FastOutSlowInEasing)
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(200, easing = FastOutSlowInEasing)
            )
            delay(200)
            scale.animateTo(
                targetValue = 1.2f,
                animationSpec = tween(200, easing = FastOutSlowInEasing)
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(200, easing = FastOutSlowInEasing)
            )
        }

        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = stringResource(R.string.hearts_description),
            tint = ThemeColors.HeartIcon,
            modifier = Modifier
                .size(16.dp)
                .scale(scale.value)
        )

        Text(
            text = hearts.toString(),
            color = ThemeColors.HeartIcon,
            fontWeight = FontWeight.Medium
        )
    }
}
