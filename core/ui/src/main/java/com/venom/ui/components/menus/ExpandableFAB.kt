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
import com.venom.ui.components.common.adp

private val DEFAULT_ICONS = listOf(
    Icons.Rounded.CameraAlt to "Camera",
    Icons.Rounded.PhotoLibrary to "Gallery",
    Icons.Rounded.Folder to "Files"
)

@Composable
fun ExpandableFAB(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onFileClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = "Add",
    icons: List<Pair<ImageVector, String>> = DEFAULT_ICONS,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    secondaryColor: Color = MaterialTheme.colorScheme.surface
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val springSpec = spring<Float>(dampingRatio = 0.6f)
    val rotation by animateFloatAsState(if (expanded) 135f else 0f, springSpec)
    val scale by animateFloatAsState(if (expanded) 1.1f else 1f, springSpec)

    val actions = listOf(onCameraClick, onGalleryClick, onFileClick)

    Box(modifier, contentAlignment = Alignment.BottomCenter) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.adp),
                modifier = Modifier.padding(bottom = 88.adp)
            ) {
                icons.asReversed().forEachIndexed { i, (icon, desc) ->
                    val originalIndex = icons.lastIndex - i
                    MiniFAB(icon, desc, secondaryColor) {
                        actions[originalIndex]()
                        expanded = false
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { expanded = !expanded },
            containerColor = primaryColor,
            modifier = Modifier
                .size(64.adp)
                .scale(scale)
        ) {
            Icon(
                Icons.Rounded.Add,
                contentDescription,
                modifier = Modifier
                    .size(32.adp)
                    .rotate(rotation)
            )
        }
    }
}

@Composable
private fun MiniFAB(
    icon: ImageVector,
    desc: String,
    color: Color,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = color,
        modifier = Modifier
            .size(48.adp)
            .shadow(8.adp, CircleShape)
    ) {
        Icon(icon, desc, modifier = Modifier.size(24.adp))
    }
}