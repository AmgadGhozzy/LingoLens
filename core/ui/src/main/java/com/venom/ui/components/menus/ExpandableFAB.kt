package com.venom.ui.components.menus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable

fun ExpandableFAB(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onFileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(true) }

    // Rotation animation for the main FAB icon
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 45f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "FAB Rotation"
    )

    Box(
        contentAlignment = Alignment.BottomCenter
    ) {
        // Smaller FABs with animations
        AnimatedVisibility(
            visible = isExpanded,
            enter = slideInVertically(animationSpec = tween(600), initialOffsetY = { it }) + fadeIn(
                animationSpec = tween(600)
            ),
            exit = slideOutVertically(
                animationSpec = tween(600),
                targetOffsetY = { it - 120 }) + fadeOut(animationSpec = tween(600)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-68).dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FABOption(icon = Icons.Default.CameraAlt,
                    contentDescription = "Camera",
                    onClick = { onCameraClick(); isExpanded = false })
                FABOption(icon = Icons.Default.PhotoLibrary,
                    contentDescription = "Gallery",
                    onClick = { onGalleryClick(); isExpanded = false })
                FABOption(icon = Icons.Default.Folder,
                    contentDescription = "Gallery",
                    onClick = { onGalleryClick(); isExpanded = false })
            }
        }

        // Main Expandable FAB
        FloatingActionButton(
            onClick = { isExpanded = !isExpanded },
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.scale(if (isExpanded) 1.1f else 1f)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = if (isExpanded) "Close" else "Expand",
                modifier = Modifier
                    .size(32.dp)
                    .rotate(rotationAngle)
            )
        }
    }
}

@Composable
private fun FABOption(
    icon: ImageVector, contentDescription: String, onClick: () -> Unit
) {
    SmallFloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.size(48.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExpandableFABPreview() {
    ExpandableFAB(onCameraClick = {}, onGalleryClick = {}, onFileClick = {})
}
