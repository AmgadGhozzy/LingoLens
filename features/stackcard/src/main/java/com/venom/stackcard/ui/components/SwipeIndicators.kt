package com.venom.stackcard.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.resources.R
import com.venom.ui.theme.BrandColors
import kotlin.math.abs

@Composable
fun SwipeIndicators(
    offsetX: Float,
    swipeThreshold: Float,
    modifier: Modifier = Modifier
) {
    val progress = (abs(offsetX) / swipeThreshold).coerceIn(0f, 1f)
    val shift by animateFloatAsState(
        targetValue = (offsetX / swipeThreshold * 50f).coerceIn(-50f, 50f),
        animationSpec = spring(stiffness = 200f)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = with(LocalDensity.current) { shift.dp }),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Indicator(
                text = stringResource(R.string.known),
                icon = "✓",
                if (offsetX > 0) progress else 0f,
                color = BrandColors.Green500,
                rotation = -8f + (progress * 8f)
            )

            Indicator(
                text = stringResource(R.string.hard),
                icon = "✗",
                if (offsetX < 0) progress else 0f,
                color = BrandColors.Red500,
                rotation = 8f - (progress * 8f)
            )
        }
    }
}

@Composable
private fun Indicator(
    text: String,
    icon: String,
    alpha: Float,
    color: Color,
    rotation: Float
) {
    Card(
        modifier = Modifier
            .alpha(alpha)
            .graphicsLayer {
                rotationZ = rotation
                scaleX = 0.9f + (alpha * 0.1f)
                scaleY = 0.9f + (alpha * 0.1f)
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = (alpha * 8).dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = icon, fontSize = 20.sp, color = Color.White)
            Text(
                text = text,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}