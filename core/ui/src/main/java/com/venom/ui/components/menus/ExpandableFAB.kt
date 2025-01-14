package com.venom.ui.components.menus

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.resources.R

@Composable
fun ExpandableFAB(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onFileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    // Rotation animation for the main FAB icon
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 45f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "FAB Rotation"
    )

    Box(contentAlignment = Alignment.BottomCenter, modifier = modifier.wrapContentSize()) {
        // Smaller FABs with animations
        AnimatedVisibility(visible = isExpanded,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it - 68 }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-68).dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FABOption(icon = Icons.Rounded.CameraAlt,
                    contentDescription = stringResource(R.string.action_camera),
                    onClick = { onCameraClick(); isExpanded = false })
                FABOption(icon = Icons.Rounded.PhotoLibrary,
                    contentDescription = stringResource(R.string.action_gallery),
                    onClick = { onGalleryClick(); isExpanded = false })
                FABOption(icon = Icons.Rounded.Folder,
                    contentDescription = stringResource(R.string.action_files),
                    onClick = { onFileClick(); isExpanded = false })
            }
        }

        // Main Expandable FAB
        FloatingActionButton(
            onClick = { isExpanded = !isExpanded },
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.scale(if (isExpanded) 1.1f else 1f)
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
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