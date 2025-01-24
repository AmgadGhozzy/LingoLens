package com.venom.ui.components.menus

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ExpandableFAB(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onFileClick: () -> Unit,
    contentDescription: String = "Add",
    icons: List<Pair<ImageVector, String>> = defaultIcons,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    secondaryColor: Color = MaterialTheme.colorScheme.secondaryContainer
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 45f else 0f, animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium
        ), label = "FAB Rotation"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            ) {
                icons.forEachIndexed { index, (icon, description) ->
                    FABOption(icon = icon,
                        contentDescription = description,
                        containerColor = secondaryColor,
                        onClick = {
                            when (index) {
                                0 -> onCameraClick()
                                1 -> onGalleryClick()
                                2 -> onFileClick()
                            }
                            isExpanded = false
                        })
                }
            }
        }

        FloatingActionButton(
            onClick = { isExpanded = !isExpanded },
            containerColor = primaryColor,
            modifier = Modifier
                .size(64.dp)
                .scale(if (isExpanded) 1.1f else 1f)

        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = contentDescription,
                modifier = Modifier
                    .size(32.dp)
                    .rotate(rotationAngle)
            )
        }
    }
}

@Composable
private fun FABOption(
    icon: ImageVector,
    contentDescription: String,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    onClick: () -> Unit
) {
    SmallFloatingActionButton(
        onClick = onClick,
        containerColor = containerColor,
        modifier = Modifier
            .padding(bottom = 8.dp)
            .size(56.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

private val defaultIcons = listOf(
    Icons.Rounded.CameraAlt to "Camera",
    Icons.Rounded.PhotoLibrary to "Gallery",
    Icons.Rounded.Folder to "Files"
)

@Preview(showBackground = true)
@Composable
fun ExpandableFABPreview() {
    ExpandableFAB(onCameraClick = {}, onGalleryClick = {}, onFileClick = {})
}