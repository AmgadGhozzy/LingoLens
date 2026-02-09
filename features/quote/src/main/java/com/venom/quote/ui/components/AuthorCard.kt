package com.venom.quote.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.venom.domain.model.Author
import com.venom.resources.R
import com.venom.ui.components.buttons.CloseButton
import com.venom.ui.components.common.adp
import com.venom.ui.components.common.asp
import kotlinx.coroutines.delay

@Composable
fun AuthorCard(
    author: Author,
    isSelected: Boolean,
    onClick: () -> Unit,
    onToggleSelection: () -> Unit,
    onViewDetails: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    SelectableItemCard(
        isSelected = isSelected,
        onClick = onClick,
        onLongClick = onToggleSelection
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.adp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(56.adp)
                    .background(
                        colorScheme.primary.copy(0.2f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = author.authorName.first().uppercase(),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                )

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = stringResource(id = R.string.author_selected),
                        tint = colorScheme.primary,
                        modifier = Modifier
                            .size(20.adp)
                            .offset(x = 18.adp, y = (-18).adp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.adp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = author.authorName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.onSurface
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = author.authorBio,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = colorScheme.onSurfaceVariant
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.adp)
                )

                Text(
                    text = stringResource(id = R.string.author_quotes_count, author.quotesCount),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(top = 4.adp)
                )
            }

            IconButton(onClick = onViewDetails) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = stringResource(id = R.string.view_details),
                    tint = colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun AuthorInfoDialog(
    author: Author,
    onClose: () -> Unit,
    onViewAllQuotes: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    var isVisible by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    Dialog(
        onDismissRequest = {
            isVisible = false
            onClose()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black.copy(0.6f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    isVisible = false
                    onClose()
                },
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    initialOffsetY = { it / 3 }
                ) + fadeIn(
                    animationSpec = tween(500, easing = FastOutSlowInEasing)
                ) + scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    initialScale = 0.9f
                ),
                exit = slideOutVertically(
                    animationSpec = tween(300, easing = FastOutLinearInEasing),
                    targetOffsetY = { -it / 3 }
                ) + fadeOut(
                    animationSpec = tween(300)
                ) + scaleOut(
                    animationSpec = tween(300),
                    targetScale = 0.9f
                )
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .wrapContentHeight()
                        .clickable(enabled = false) { },
                    shape = RoundedCornerShape(28.adp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 24.adp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(24.adp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.author_details),
                                color = colorScheme.onSurface,
                                fontSize = 24.asp,
                                fontWeight = FontWeight.Bold
                            )

                            CloseButton(
                                onClick = {
                                    isVisible = false
                                    onClose()
                                },
                                size = 40.adp
                            )
                        }

                        Spacer(modifier = Modifier.height(24.adp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.adp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(
                                                colorScheme.primary,
                                                colorScheme.secondary
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = author.authorName.first().uppercase(),
                                    color = Color.White,
                                    fontSize = 32.asp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(16.adp))

                            Text(
                                text = author.authorName,
                                color = colorScheme.onSurface,
                                fontSize = 20.asp,
                                fontWeight = FontWeight.SemiBold
                            )

                            if (author.authorBio.isNotEmpty()) {
                                Text(
                                    text = author.authorBio,
                                    color = colorScheme.onSurfaceVariant,
                                    fontSize = 14.asp,
                                    modifier = Modifier.padding(top = 4.adp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.adp))

                        if (author.authorDescription.isNotEmpty()) {
                            Card(
                                shape = RoundedCornerShape(16.adp),
                                colors = CardDefaults.cardColors(
                                    containerColor = colorScheme.surfaceContainerHighest
                                )
                            ) {
                                Text(
                                    text = author.authorDescription,
                                    color = colorScheme.onSurfaceVariant,
                                    fontSize = 14.asp,
                                    lineHeight = 20.asp,
                                    modifier = Modifier.padding(16.adp)
                                )
                            }

                            Spacer(modifier = Modifier.height(24.adp))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatQuote,
                                contentDescription = null,
                                modifier = Modifier.size(20.adp),
                                tint = colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.adp))
                            Text(
                                text = stringResource(
                                    id = R.string.quotes_available,
                                    author.quotesCount
                                ),
                                color = colorScheme.primary,
                                fontSize = 16.asp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(24.adp))

                        Button(
                            onClick = {
                                onViewAllQuotes(author.authorName)
                                onClose()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.adp),
                            shape = RoundedCornerShape(16.adp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(0.adp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                colorScheme.primary,
                                                colorScheme.secondary
                                            )
                                        ),
                                        RoundedCornerShape(16.adp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(
                                        id = R.string.view_all_quotes,
                                        author.authorName
                                    ),
                                    fontSize = 16.asp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }

                        if (author.authorLink.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.adp))

                            OutlinedButton(
                                onClick = {
                                    uriHandler.openUri(author.authorLink)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.adp),
                                shape = RoundedCornerShape(16.adp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = colorScheme.onSurfaceVariant
                                ),
                                border = BorderStroke(
                                    1.adp,
                                    colorScheme.outlineVariant
                                )
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Default.OpenInNew,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.adp)
                                    )
                                    Spacer(modifier = Modifier.width(8.adp))
                                    Text(
                                        text = stringResource(id = R.string.learn_more),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthorCardPreview() {
    MaterialTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.adp),
                verticalArrangement = Arrangement.spacedBy(16.adp)
            ) {
                // Regular card
                AuthorCard(
                    author = Author(
                        authorName = "Marcus Aurelius",
                        authorBio = "Roman Emperor and Stoic philosopher",
                        authorDescription = "Marcus Aurelius was a Roman emperor from 161 to 180 and a Stoic philosopher.",
                        authorLink = "https://example.com",
                        quotesCount = 42
                    ),
                    onClick = { },
                    isSelected = false,
                    onToggleSelection = { },
                    onViewDetails = { }
                )

                // Selected card with all features
                AuthorCard(
                    author = Author(
                        authorName = "Aristotle",
                        authorBio = "Ancient Greek philosopher and polymath",
                        authorDescription = "Aristotle was a Greek philosopher and polymath during the Classical period.",
                        authorLink = "https://example.com",
                        quotesCount = 89
                    ),
                    onClick = { },
                    isSelected = true,
                    onToggleSelection = { },
                    onViewDetails = { }
                )
            }
        }
    }
}