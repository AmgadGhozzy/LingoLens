package com.venom.stackcard.ui.components

import androidx.compose.animation.core.Spring
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.resources.R
import kotlin.math.abs

@Composable
fun SwipeIndicators(
    offsetX: Float,
    swipeThreshold: Float,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val indicatorAlpha = (abs(offsetX) / swipeThreshold).coerceIn(0f, 1f)

    // Dynamic positioning based on swipe progress
    val indicatorOffset by animateFloatAsState(
        targetValue = (offsetX / swipeThreshold * 40f).coerceIn(-40f, 40f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
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
                .offset(x = with(density) { indicatorOffset.dp }),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Remember indicator (left)
            SwipeIndicator(
                text = stringResource(R.string.i_remember),
                icon = "✓",
                alpha = if (offsetX > 0) indicatorAlpha else 0f,
                backgroundColor = Color(0xFF4CAF50),
                modifier = Modifier.graphicsLayer(
                    rotationZ = -10f + (indicatorAlpha * 10f)
                )
            )

            // Forgot indicator (right)
            SwipeIndicator(
                text = stringResource(R.string.forgot),
                icon = "✗",
                alpha = if (offsetX < 0) indicatorAlpha else 0f,
                backgroundColor = Color(0xFFE53E3E),
                modifier = Modifier.graphicsLayer(
                    rotationZ = 10f - (indicatorAlpha * 10f)
                )
            )
        }
    }
}

@Composable
private fun SwipeIndicator(
    text: String,
    icon: String,
    alpha: Float,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier
            .alpha(alpha)
            .blur(if (alpha < 0.3f) 2.dp else 0.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor.copy(0.9f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = icon,
                fontSize = 18.sp,
                color = Color.White
            )
            Text(
                text = text,
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
