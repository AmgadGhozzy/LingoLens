package com.venom.ui.screen.langselector

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.data.model.LANGUAGES_LIST
import com.venom.data.model.LanguageItem
import com.venom.ui.components.other.GlassCard

@Composable
fun LanguageListItem(
    modifier: Modifier = Modifier,
    language: LanguageItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDownloadClick: (LanguageItem) -> Unit = {},
    onDeleteClick: (LanguageItem) -> Unit = {},
) {

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale),
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        glassAlpha = if (isSelected) 0.25f else 0.15f,
        borderWidth = if (isSelected) 2.dp else 1.dp,
        borderAlpha = if (isSelected) 0.4f else 0.1f,
        solidBackgroundAlpha = if (isSelected) 0.2f else 0.08f
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Flag Container
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = if (isSelected) {
                                    listOf(
                                        MaterialTheme.colorScheme.primary.copy(0.25f),
                                        MaterialTheme.colorScheme.primary.copy(0.1f),
                                        Color.Transparent
                                    )
                                } else {
                                    listOf(
                                        MaterialTheme.colorScheme.surfaceVariant.copy(0.4f),
                                        MaterialTheme.colorScheme.surfaceVariant.copy(0.2f),
                                        Color.Transparent
                                    )
                                }
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = language.flag,
                        fontSize = 28.sp,
                        modifier = Modifier.scale(1.1f)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = language.englishName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    )

                    Text(
                        text = language.nativeName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primary.copy(0.7f)
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    )

                    // Download size info
                    language.downloadSizeMb?.let { size ->
                        if (!language.isDownloaded) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.CloudDownload,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = "${size}MB",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AnimatedVisibility(
                    visible = isSelected,
                    enter = scaleIn(spring(stiffness = Spring.StiffnessHigh)) + fadeIn(),
                    exit = scaleOut(spring(stiffness = Spring.StiffnessHigh)) + fadeOut()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                when {
                    language.isDownloading -> {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 3.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    language.isDownloaded -> {
                        IconButton(
                            onClick = { onDeleteClick(language) },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.errorContainer.copy(0.3f))
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Delete,
                                contentDescription = "Delete offline model",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    language.downloadSizeMb != null -> {
                        IconButton(
                            onClick = { onDownloadClick(language) },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer.copy(0.3f))
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CloudDownload,
                                contentDescription = "Download offline model",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Selected")
@Composable
fun LanguageListItemSelectedPreview() {
    MaterialTheme {
        LanguageListItem(
            language = LANGUAGES_LIST[0],
            isSelected = true,
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Downloadable")
@Composable
fun LanguageListItemDownloadablePreview() {
    MaterialTheme {
        LanguageListItem(
            language = LANGUAGES_LIST[1].copy(downloadSizeMb = 25.5f),
            isSelected = false,
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Downloaded")
@Composable
fun LanguageListItemDownloadedPreview() {
    MaterialTheme {
        LanguageListItem(
            language = LANGUAGES_LIST[2].copy(
                isDownloaded = true,
                downloadSizeMb = 28.8f
            ),
            isSelected = false,
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Downloading")
@Composable
fun LanguageListItemDownloadingPreview() {
    MaterialTheme {
        LanguageListItem(
            language = LANGUAGES_LIST[3].copy(
                isDownloading = true,
                downloadSizeMb = 30.2f
            ),
            isSelected = false,
            onClick = {}
        )
    }
}