package com.venom.quote.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.domain.model.Quote
import com.venom.resources.R
import com.venom.ui.components.common.DynamicStyledText
import com.venom.ui.components.common.ExpandableTranslation
import com.venom.ui.components.common.adp
import com.venom.ui.components.common.asp
import com.venom.ui.components.other.GlassThickness
import com.venom.ui.components.other.GradientGlassCard
import com.venom.ui.theme.lingoLens
import com.venom.ui.viewmodel.ExpandableCardViewModel

@Composable
fun QuoteCard(
    quote: Quote,
    index: Int,
    searchQuery: String = "",
    onToggleFavorite: () -> Unit,
    onPlayAudio: (String) -> Unit,
    onShare: (Quote) -> Unit,
    onCopy: (String) -> Unit,
    onAuthorClick: () -> Unit,
    onTagClick: (String) -> Unit = {},
    isFavorited: Boolean = false,
    showDailyHeader: Boolean = false,
    useGradientBackground: Boolean = false
) {
    val viewModel: ExpandableCardViewModel = hiltViewModel()
    val translationState by viewModel.state.collectAsState()
    val cardId = "quote_$index"
    val isExpanded = translationState.expandedCardId == cardId

    val isSpecialCard = useGradientBackground || showDailyHeader
    val feature = MaterialTheme.lingoLens.feature.quote

    GradientGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)),
        thickness = GlassThickness.Regular,
        shape = RoundedCornerShape(20.adp),
        gradientColors = listOf(
            if (index % 2 == 0) MaterialTheme.colorScheme.surfaceContainerHigh
            else MaterialTheme.colorScheme.surfaceContainerHighest
        ),
        gradientAlpha = 0.5f
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().run {
                    if (isSpecialCard) {
                        background(brush = feature.gradient)
                    } else this
                }) {
            // Decorative circles for special cards
            if (isSpecialCard) {
                DecorativeElements()
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = if (isSpecialCard) 20.adp else 18.adp,
                        end = if (isSpecialCard) 20.adp else 18.adp,
                        bottom = if (isSpecialCard) 20.adp else 18.adp,
                        top = if (isSpecialCard) 20.adp else 0.adp
                    )
            ) {
                // Daily Quote Header
                if (showDailyHeader) {
                    DailyQuoteHeader()
                }

                // Quote mark
                QuoteMark(isSpecialCard = isSpecialCard)

                // Quote content
                QuoteContent(
                    quote = quote, searchQuery = searchQuery, isSpecialCard = isSpecialCard
                )

                // Author
                QuoteAuthor(
                    authorName = quote.authorName,
                    onAuthorClick = onAuthorClick,
                    isSpecialCard = isSpecialCard
                )

                // Translation section
                ExpandableTranslation(
                    isExpanded = isExpanded,
                    translatedText = translationState.currentTranslation,
                    isLoading = translationState.isLoading,
                    error = translationState.error,
                    onRetry = { viewModel.retry(quote.quoteContent) },
                    modifier = Modifier.padding(bottom = 8.adp)
                )

                // Action buttons
                QuoteActionButtons(
                    quote = quote,
                    isFavorited = isFavorited,
                    onToggleFavorite = onToggleFavorite,
                    onPlayAudio = onPlayAudio,
                    onShare = onShare,
                    onCopy = { onCopy(quote.quoteContent) },
                    onToggleTranslation = { viewModel.toggleCard(cardId, quote.quoteContent) },
                    isSpecialCard = isSpecialCard,
                    modifier = Modifier.padding(bottom = 12.adp)
                )

                // Tags
                QuoteTags(
                    tags = quote.tags, onTagClick = onTagClick, isSpecialCard = isSpecialCard
                )
            }
        }
    }
}

@Composable
private fun DecorativeElements() {
    val decorativeColor = if (isSystemInDarkTheme()) Color.White else Color.White

    Box(
        modifier = Modifier
            .size(100.adp)
            .offset(x = (-30).adp, y = (-30).adp)
            .background(decorativeColor.copy(0.05f), CircleShape)
    )

    Box(
        modifier = Modifier
            .size(70.adp)
            .offset(x = 250.adp, y = (-15).adp)
            .background(decorativeColor.copy(0.08f), CircleShape)
    )
}

@Composable
private fun DailyQuoteHeader() {
    val feature = MaterialTheme.lingoLens.feature.quote

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(feature.gold.copy(0.1f), CircleShape)
                .padding(8.adp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.icon_sparkles),
                contentDescription = stringResource(id = R.string.daily_quote),
                tint = feature.gold,
                modifier = Modifier.size(20.adp)
            )
        }

        Spacer(modifier = Modifier.width(10.adp))

        Text(
            text = stringResource(id = R.string.daily_quote),
            color = feature.gold,
            fontSize = 14.asp,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun QuoteMark(isSpecialCard: Boolean) {
    val colorScheme = MaterialTheme.colorScheme

    Text(
        text = "“",
        color = when {
            isSpecialCard -> Color.White.copy(0.2f)
            else -> colorScheme.primary.copy(0.6f)
        },
        fontSize = if (isSpecialCard) 52.asp else 48.asp,
        fontWeight = FontWeight.Light,
        modifier = Modifier.offset(x = (-4).adp, y = 16.adp)
    )
}

@Composable
private fun QuoteContent(
    quote: Quote, searchQuery: String, isSpecialCard: Boolean
) {
    val feature = MaterialTheme.lingoLens.feature.quote

    val annotatedString = buildAnnotatedString {
        if (searchQuery.isNotEmpty() && quote.quoteContent.contains(
                searchQuery, ignoreCase = true
            )
        ) {
            val startIndex = quote.quoteContent.indexOf(searchQuery, ignoreCase = true)
            val endIndex = startIndex + searchQuery.length

            append(quote.run { quoteContent.take(startIndex) })
            withStyle(SpanStyle(background = feature.gold.copy(0.3f))) {
                append(quote.quoteContent.substring(startIndex, endIndex))
            }
            append(quote.quoteContent.substring(endIndex))
        } else {
            append(quote.quoteContent)
        }
    }

    DynamicStyledText(
        text = annotatedString,
        color = when {
            isSpecialCard -> Color.White.copy(0.8f)
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        lineHeight = 26,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(bottom = 16.adp)
    )
}

@Composable
private fun QuoteAuthor(
    authorName: String, onAuthorClick: () -> Unit, isSpecialCard: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme

    Text(
        text = stringResource(id = R.string.quote_author_prefix, authorName),
        style = MaterialTheme.typography.bodyMedium.copy(
            fontStyle = FontStyle.Italic, fontWeight = FontWeight.Normal
        ),
        color = when {
            isSpecialCard -> Color.White.copy(0.9f)
            else -> colorScheme.onSurfaceVariant
        },
        fontSize = 14.asp,
        modifier = Modifier
            .clip(RoundedCornerShape(6.adp))
            .clickable { onAuthorClick() }
            .padding(4.adp)
            .padding(bottom = 12.adp))
}

@Composable
private fun QuoteActionButtons(
    quote: Quote,
    isFavorited: Boolean,
    onToggleFavorite: () -> Unit,
    onPlayAudio: (String) -> Unit,
    onShare: (Quote) -> Unit,
    onCopy: () -> Unit,
    onToggleTranslation: () -> Unit,
    isSpecialCard: Boolean = false,
    copySuccess: Boolean = false,
    modifier: Modifier = Modifier
) {
    val feature = MaterialTheme.lingoLens.feature.quote
    val semantic = MaterialTheme.lingoLens.semantic

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        // Primary Actions (Left side)
        Row(horizontalArrangement = Arrangement.spacedBy(4.adp)) {
            ActionButton(
                icon = Icons.AutoMirrored.Filled.VolumeUp,
                onClick = { onPlayAudio(quote.quoteContent) },
                isSpecialCard = isSpecialCard,
                isPrimary = true
            )

            ActionButton(
                icon = if (copySuccess) Icons.Default.Check else Icons.Default.ContentCopy,
                onClick = onCopy,
                isSpecialCard = isSpecialCard,
                isSelected = copySuccess,
                selectedColor = if (isSpecialCard) feature.gold else semantic.success
            )

            ActionButton(
                icon = Icons.Default.Share,
                onClick = { onShare(quote) },
                isSpecialCard = isSpecialCard
            )
        }

        // Secondary Actions (Right side)
        Row(horizontalArrangement = Arrangement.spacedBy(4.adp)) {
            ActionButton(
                icon = if (isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                onClick = onToggleFavorite,
                isSelected = isFavorited,
                selectedColor = feature.pink,
                isSpecialCard = isSpecialCard
            )

            ActionButton(
                icon = Icons.Default.Translate,
                onClick = onToggleTranslation,
                isSpecialCard = isSpecialCard
            )
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    onClick: () -> Unit,
    isSelected: Boolean = false,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    isSpecialCard: Boolean = false,
    isPrimary: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val feature = MaterialTheme.lingoLens.feature.quote

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f, animationSpec = tween(200)
    )

    val backgroundColor = when {
        isPrimary && isSpecialCard -> feature.gold.copy(0.2f)
        isPrimary -> colorScheme.primary.copy(0.1f)
        isSpecialCard -> Color.White.copy(0.15f)
        else -> if (isSystemInDarkTheme()) Color.White.copy(0.05f) else Color.Black.copy(
            0.05f
        )
    }

    val iconColor = when {
        isSelected -> selectedColor
        isPrimary && isSpecialCard -> feature.gold
        isPrimary -> colorScheme.primary
        isSpecialCard -> Color.White
        else -> colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier
            .scale(scale)
            .size(44.adp)
            .clip(RoundedCornerShape(12.adp))
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(20.adp)
        )
    }
}

@Composable
private fun QuoteTags(
    tags: List<String>,
    onTagClick: (String) -> Unit = {},
    isSpecialCard: Boolean = false,
    modifier: Modifier = Modifier
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(6.adp, Alignment.CenterHorizontally),
        modifier = modifier.fillMaxWidth()
    ) {
        items(tags) { tag ->
            TagChip(
                tag = tag, onClick = { onTagClick(tag) }, isSpecialCard = isSpecialCard
            )
        }
    }
}

@Composable
private fun TagChip(
    tag: String, onClick: () -> Unit, isSpecialCard: Boolean = false
) {
    val feature = MaterialTheme.lingoLens.feature.quote

    val backgroundColor = when {
        isSpecialCard -> Color.White.copy(0.2f)
        else -> feature.tagBackground
    }

    val textColor = when {
        isSpecialCard -> Color.White.copy(0.8f)
        else -> feature.tagText
    }

    Text(
        text = "#${tag.lowercase()}",
        color = textColor,
        fontSize = 10.asp,
        fontWeight = FontWeight.Normal,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier
            .clip(RoundedCornerShape(12.adp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 8.adp, vertical = 3.adp))
}

@Composable
fun SelectableItemCard(
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    content: @Composable () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick, onLongClick = onLongClick
            )
            .clip(RoundedCornerShape(16.adp)),
        shape = RoundedCornerShape(16.adp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.adp else 4.adp
        ),
        border = if (isSelected) {
            BorderStroke(2.adp, selectedColor)
        } else {
            BorderStroke(1.adp, colorScheme.outlineVariant)
        }
    ) {
        content()
    }
}
