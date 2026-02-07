package com.venom.ui.components.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.venom.ui.components.common.adp
import com.venom.ui.components.common.asp

@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(28.adp)

    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.adp)
            .shadow(
                elevation = 8.adp,
                shape = shape,
                ambientColor = colors.first().copy(0.3f),
                spotColor = colors.last().copy(0.4f)
            )
            .clip(shape)
            .background(
                brush = Brush.horizontalGradient(colors)
            ),
        color = Color.Transparent
    ) {
        Box(
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 18.asp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}