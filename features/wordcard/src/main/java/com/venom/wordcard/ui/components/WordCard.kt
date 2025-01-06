package com.venom.wordcard.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

data class WordCard(
    val id: String,
    val word: String,
    val translation: String? = null,
    val examples: List<String> = emptyList()
)

@Composable
fun WordCard(
    card: WordCard,
    offsetX: Float,
    offsetY: Float,
    rotation: Float,
    scale: Float,
    isFlipped: Boolean,
    rotationY: Float,
    onFlip: () -> Unit,
    onDrag: (androidx.compose.ui.geometry.Offset) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val swipeAlpha = 1f - (abs(offsetX) / 1000f).coerceIn(0f, 0.5f)
    val swipeThreshold = 300f
    val density = LocalDensity.current.density

    Card(modifier = modifier
        .offset {
            IntOffset(
                offsetX.toInt(), offsetY.toInt()
            )
        }
        .scale(scale)
        .rotate(rotation)
        .alpha(swipeAlpha)
        .graphicsLayer(
            rotationY = rotationY, cameraDistance = 12f * density
        )
        .shadow(
            elevation = 8.dp,
            shape = RoundedCornerShape(16.dp),
        )
        .clip(RoundedCornerShape(16.dp))
        .fillMaxWidth()
        .aspectRatio(1.5f)
        .pointerInput(Unit) {
            detectDragGestures(onDragEnd = { onDragEnd() },
                onDrag = { _, dragAmount -> onDrag(dragAmount) })
        }, elevation = CardDefaults.cardElevation(
        defaultElevation = 4.dp, pressedElevation = 8.dp
    )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Front content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        alpha = if (isFlipped) 0f else 1f,
                    )
            ) {
                FrontContent(card)
            }

            // Back content with mirroring effect
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        alpha = if (isFlipped) 1f else 0f, rotationY = if (isFlipped) 180f else 0f
                    )
            ) {
                BackContent(card)
            }

            // Action buttons and indicators
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        rotationY = if (isFlipped) 180f else 0f
                    )
            ) {
                ActionButtons(
                    card = card, onFlip = onFlip
                )

                SwipeIndicators(
                    offsetX = offsetX, swipeThreshold = swipeThreshold, isFlipped = isFlipped
                )
            }
        }
    }
}

@Composable
private fun FrontContent(card: WordCard) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = card.word,
            fontSize = 32.sp,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge
        )
    }
}

@Composable
private fun BackContent(card: WordCard) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        card.translation?.let { translation ->
            Text(
                text = translation,
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        card.examples.take(2).forEach { example ->
            Text(
                text = example,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun ActionButtons(
    card: WordCard, onFlip: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionButton(
                icon = Icons.Rounded.ContentCopy,
                contentDescription = "Copy word ${card.word}",
                backgroundColor = MaterialTheme.colorScheme.primary,
                iconTint = MaterialTheme.colorScheme.onPrimary
            )

            ActionButton(
                icon = Icons.Rounded.Refresh,
                contentDescription = "Flip card for ${card.word}",
                backgroundColor = MaterialTheme.colorScheme.secondary,
                iconTint = MaterialTheme.colorScheme.onSecondary,
                onClick = onFlip
            )

            ActionButton(
                icon = Icons.Rounded.Mic,
                contentDescription = "Speech-to-text for ${card.word}",
                backgroundColor = MaterialTheme.colorScheme.tertiary,
                iconTint = MaterialTheme.colorScheme.onTertiary
            )
        }

        BookmarkButton(card)
    }
}

@Composable
private fun BookmarkButton(card: WordCard) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
    ) {
        IconButton(onClick = { /* Bookmark action */ }, modifier = Modifier.semantics {
            contentDescription = "Bookmark word ${card.word}"
        }) {
            Icon(
                imageVector = Icons.Rounded.BookmarkBorder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    contentDescription: String,
    backgroundColor: Color,
    iconTint: Color,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(backgroundColor.copy(alpha = 0.9f))
            .clickable(onClick = onClick)
            .padding(8.dp), contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun SwipeIndicators(
    offsetX: Float, swipeThreshold: Float, isFlipped: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(rotationY = if (isFlipped) 180f else 0f)
    ) {
        // Left indicator ("I Remember")
        Box(
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            SwipeIndicator(
                text = "I Remember",
                alpha = (-offsetX / swipeThreshold).coerceIn(0f, 1f),
                backgroundColor = MaterialTheme.colorScheme.primary
            )
        }

        // Right indicator ("Forgot")
        Box(
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            SwipeIndicator(
                text = "Forgot",
                alpha = (offsetX / swipeThreshold).coerceIn(0f, 1f),
                backgroundColor = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun SwipeIndicator(
    text: String, alpha: Float, backgroundColor: Color, modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .alpha(alpha)
            .clip(CircleShape)
            .background(backgroundColor.copy(alpha = 0.9f))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
