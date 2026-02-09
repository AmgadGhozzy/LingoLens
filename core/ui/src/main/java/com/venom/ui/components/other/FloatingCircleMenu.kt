package com.venom.ui.components.other

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.venom.ui.components.common.adp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class FloatingMenuItem(
    val icon: Any,
    val color: Color,
    val description: String? = null,
    val onClick: () -> Unit
)

@Composable
fun FloatingCircleMenu(
    items: List<FloatingMenuItem>,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var clickedIndex by remember { mutableIntStateOf(-1) }
    var waveProgress by remember { mutableFloatStateOf(0f) }

    val rotation by updateTransition(isExpanded).animateFloat(
        transitionSpec = { spring(dampingRatio = 0.6f, stiffness = 150f) }
    ) { if (it) 45f else 0f }

    val waveAnim by animateFloatAsState(
        targetValue = waveProgress,
        animationSpec = tween(800)
    )

    LaunchedEffect(clickedIndex) {
        if (clickedIndex >= 0) {
            waveProgress = 1f
            delay(800)
            isExpanded = false
            delay(200)
            clickedIndex = -1
            waveProgress = 0f
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(56.adp)
                .shadow(
                    elevation = 16.adp,
                    shape = CircleShape,
                    ambientColor = Color.Black.copy(0.15f)
                )
                .background(MaterialTheme.colorScheme.primaryContainer.copy(0.5f), CircleShape)
                .clickable { if (clickedIndex < 0) isExpanded = !isExpanded },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.Add,
                contentDescription = null,
                modifier = Modifier
                    .size(24.adp)
                    .rotate(rotation),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        items.forEachIndexed { index, item ->
            FloatingMenuItemComponent(
                item = item,
                index = index,
                totalItems = items.size,
                visible = isExpanded && clickedIndex < 0,
                isClicked = clickedIndex == index,
                waveProgress = if (clickedIndex == index) waveAnim else 0f,
                onItemClick = {
                    clickedIndex = index
                    item.onClick()
                }
            )
        }
    }
}

@Composable
private fun FloatingMenuItemComponent(
    item: FloatingMenuItem,
    index: Int,
    totalItems: Int,
    visible: Boolean,
    isClicked: Boolean,
    waveProgress: Float,
    onItemClick: () -> Unit
) {
    val angle = (-90f + (index - (totalItems - 1) / 2f) * (120f / totalItems)) * (PI / 180f)
    val radius = 100.adp
    val waveBaseRad1 = 60.adp
    val waveBaseRad2 = 100.adp
    val strokeWidth1 = 4.adp
    val strokeWidth2 = 3.adp
    val sparkleDistance = 60.adp
    val sparkleRadius = 3.adp

    val offsetX = radius * cos(angle).toFloat()
    val offsetY = radius * sin(angle).toFloat()

    Box(
        modifier = Modifier.offset(offsetX, offsetY),
        contentAlignment = Alignment.Center
    ) {
        if (isClicked && waveProgress > 0f) {
            repeat(2) { wave ->
                Canvas(modifier = Modifier.size(12.adp)) {
                    val baseRad = if (wave == 0) waveBaseRad1 else waveBaseRad2
                    val waveRadius = baseRad.toPx() * waveProgress
                    val alpha = (1f - waveProgress) * (0.4f - wave * 0.1f)
                    val sWidth = if (wave == 0) strokeWidth1 else strokeWidth2
                    drawCircle(
                        color = item.color.copy(alpha),
                        radius = waveRadius,
                        style = Stroke(width = sWidth.toPx())
                    )
                }
            }

            Canvas(
                modifier = Modifier.size(24.adp)
            ) {
                repeat(6) { i ->
                    val sparkleAngle = (i * 45f) * (PI / 180f)
                    val distance = sparkleDistance.toPx() * waveProgress
                    val x = center.x + cos(sparkleAngle).toFloat() * distance
                    val y = center.y + sin(sparkleAngle).toFloat() * distance

                    drawCircle(
                        color = item.color.copy((1f - waveProgress) * 0.6f),
                        radius = sparkleRadius.toPx(),
                        center = Offset(x, y)
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(400, index * 80)) + scaleIn(
                tween(400, index * 80),
                initialScale = 0.3f
            ),
            exit = scaleOut(tween(200)) + fadeOut(tween(200))
        ) {
            Box(
                modifier = Modifier
                    .size(48.adp)
                    .shadow(
                        elevation = 12.adp,
                        shape = CircleShape,
                        spotColor = Color.Black.copy(0.3f),
                        ambientColor = Color.Black.copy(0.2f)
                    )
                    .background(MaterialTheme.colorScheme.surfaceContainerLow, CircleShape)
                    .clickable { onItemClick() },
                contentAlignment = Alignment.Center
            ) {
                when (val icon = item.icon) {
                    is ImageVector -> Icon(
                        imageVector = icon,
                        contentDescription = item.description,
                        tint = item.color,
                        modifier = Modifier.size(24.adp)
                    )

                    is Int -> Icon(
                        painter = painterResource(icon),
                        contentDescription = item.description,
                        tint = item.color,
                        modifier = Modifier.size(24.adp)
                    )

                    else -> {}
                }
            }
        }
    }
}
