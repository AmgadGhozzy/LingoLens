package com.venom.ui.components.menus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ExpandableFAB(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onFileClick: () -> Unit,
    contentDescription: String = "Add",
    icons: List<Pair<ImageVector, String>> = defaultIcons,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    secondaryColor: Color = MaterialTheme.colorScheme.surface
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        if (isExpanded) 135f else 0f,
        spring(dampingRatio = 0.6f)
    )

    val scale by animateFloatAsState(
        if (isExpanded) 1.1f else 1f,
        spring(dampingRatio = 0.6f)
    )

    Box(contentAlignment = Alignment.BottomCenter) {
        AnimatedVisibility(
            isExpanded,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(bottom = 88.dp)
            ) {
                icons.reversed().forEachIndexed { i, (icon, desc) ->
                    val index = icons.size - 1 - i
                    FABOption(icon, desc, secondaryColor) {
                        when (index) {
                            0 -> onCameraClick()
                            1 -> onGalleryClick()
                            2 -> onFileClick()
                        }
                        isExpanded = false
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { isExpanded = !isExpanded },
            containerColor = primaryColor,
            modifier = Modifier
                .size(64.dp)
                .scale(scale)
        ) {
            Icon(
                Icons.Rounded.Add,
                contentDescription,
                modifier = Modifier
                    .size(32.dp)
                    .rotate(rotation)
            )
        }
    }
}

@Composable
private fun FABOption(
    icon: ImageVector,
    desc: String,
    color: Color,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = color,
        modifier = Modifier
            .size(48.dp)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape.copy(
                    topStart = CornerSize(16.dp),
                    topEnd = CornerSize(16.dp),
                    bottomStart = CornerSize(16.dp),
                    bottomEnd = CornerSize(16.dp)
                ),
                ambientColor = Color.Black.copy(0.1f),
                spotColor = Color.Black.copy(0.1f)
            )
    ) {
        Icon(icon, desc, modifier = Modifier.size(24.dp))
    }
}

private val defaultIcons = listOf(
    Icons.Rounded.CameraAlt to "Camera",
    Icons.Rounded.PhotoLibrary to "Gallery",
    Icons.Rounded.Folder to "Files"
)