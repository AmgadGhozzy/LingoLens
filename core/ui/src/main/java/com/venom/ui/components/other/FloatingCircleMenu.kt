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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.ui.theme.ThemeColors.BitcoinColor
import com.venom.ui.theme.ThemeColors.PurplePrimary
import com.venom.ui.theme.ThemeColors.TONColor
import com.venom.ui.theme.ThemeColors.USDTColor
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class FloatingMenuItem(
    val icon: ImageVector,
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
                .size(56.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = CircleShape,
                    ambientColor = Color.Black.copy(alpha = 0.15f)
                )
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                .clickable { if (clickedIndex < 0) isExpanded = !isExpanded },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.Add,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
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
    val radius = 100.dp
    val density = LocalDensity.current

    val offsetX = with(density) { (cos(angle) * radius.value).dp }
    val offsetY = with(density) { (sin(angle) * radius.value).dp }

    Box(
        modifier = Modifier.offset(offsetX, offsetY),
        contentAlignment = Alignment.Center
    ) {
        if (isClicked && waveProgress > 0f) {
            repeat(2) { wave ->
                Canvas(modifier = Modifier.size(12.dp)) {
                    val waveRadius = (60 + wave * 40).dp.toPx() * waveProgress
                    val alpha = (1f - waveProgress) * (0.4f - wave * 0.1f)
                    drawCircle(
                        color = item.color.copy(alpha = alpha),
                        radius = waveRadius,
                        style = Stroke(width = (4 - wave).dp.toPx())
                    )
                }
            }

            Canvas(
                modifier = Modifier.size(24.dp)
            ) {
                repeat(6) { i ->
                    val sparkleAngle = (i * 45f) * (PI / 180f)
                    val distance = 60.dp.toPx() * waveProgress
                    val x = center.x + cos(sparkleAngle).toFloat() * distance
                    val y = center.y + sin(sparkleAngle).toFloat() * distance

                    drawCircle(
                        color = item.color.copy(alpha = (1f - waveProgress) * 0.6f),
                        radius = 3.dp.toPx(),
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
                    .size(48.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = CircleShape,
                        spotColor = Color.Black.copy(alpha = 0.3f),
                        ambientColor = Color.Black.copy(alpha = 0.2f)
                    )
                    .background(Color.White, CircleShape)
                    .clickable { onItemClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    item.icon,
                    contentDescription = null,
                    tint = item.color,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun FloatingCircleMenuPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        FloatingCircleMenu(
            items = listOf(
                FloatingMenuItem(Icons.Rounded.Bookmark, USDTColor) {},
                FloatingMenuItem(Icons.AutoMirrored.Rounded.MenuBook, PurplePrimary) {},
                FloatingMenuItem(Icons.AutoMirrored.Rounded.VolumeUp, BitcoinColor) {},
                FloatingMenuItem(Icons.Rounded.Share, TONColor) {}
            )
        )
    }
}