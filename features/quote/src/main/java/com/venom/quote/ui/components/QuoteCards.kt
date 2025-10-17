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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.domain.model.Quote
import com.venom.resources.R
import com.venom.ui.components.common.ExpandableTranslation
import com.venom.ui.components.other.GlassCard
import com.venom.ui.theme.QuoteColors
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

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)),
        shape = RoundedCornerShape(20.dp),
        solidBackground = if (index % 2 == 0)
            MaterialTheme.colorScheme.surfaceContainer.copy(0.5f)
        else
            MaterialTheme.colorScheme.surface.copy(0.5f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .run {
                    if (isSpecialCard) {
                        background(
                            brush = QuoteColors.dailyQuoteGradient()
                        )
                    } else this
                }
        ) {
            // Decorative circles for special cards
            if (isSpecialCard) {
                DecorativeElements()
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = if (isSpecialCard) 20.dp else 18.dp,
                        end = if (isSpecialCard) 20.dp else 18.dp,
                        bottom = if (isSpecialCard) 20.dp else 18.dp,
                        top = if (isSpecialCard) 20.dp else 0.dp
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
                    quote = quote,
                    searchQuery = searchQuery,
                    isSpecialCard = isSpecialCard
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
                    modifier = Modifier.padding(bottom = 8.dp)
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
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Tags
                QuoteTags(
                    tags = quote.tags,
                    onTagClick = onTagClick,
                    isSpecialCard = isSpecialCard
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
            .size(100.dp)
            .offset(x = (-30).dp, y = (-30).dp)
            .background(decorativeColor.copy(alpha = 0.05f), CircleShape)
    )

    Box(
        modifier = Modifier
            .size(70.dp)
            .offset(x = 250.dp, y = (-15).dp)
            .background(decorativeColor.copy(alpha = 0.08f), CircleShape)
    )
}

@Composable
private fun DailyQuoteHeader() {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(QuoteColors.accentGold().copy(alpha = 0.1f), CircleShape)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.icon_sparkles),
                contentDescription = stringResource(id = R.string.daily_quote),
                tint = QuoteColors.accentGold(),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = stringResource(id = R.string.daily_quote),
            color = QuoteColors.accentGold(),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun QuoteMark(isSpecialCard: Boolean) {
    Text(
        text = "“",
        color = when {
            isSpecialCard -> Color.White.copy(alpha = 0.2f)
            else -> QuoteColors.accentBlue().copy(alpha = 0.6f)
        },
        fontSize = if (isSpecialCard) 52.sp else 48.sp,
        fontWeight = FontWeight.Light,
        modifier = Modifier.offset(x = (-4).dp, y = 16.dp)
    )
}

@Composable
private fun QuoteContent(
    quote: Quote,
    searchQuery: String,
    isSpecialCard: Boolean
) {
    val annotatedString = buildAnnotatedString {
        if (searchQuery.isNotEmpty() && quote.quoteContent.contains(
                searchQuery,
                ignoreCase = true
            )
        ) {
            val startIndex = quote.quoteContent.indexOf(searchQuery, ignoreCase = true)
            val endIndex = startIndex + searchQuery.length

            append(quote.quoteContent.substring(0, startIndex))
            withStyle(SpanStyle(background = QuoteColors.accentGold().copy(alpha = 0.3f))) {
                append(quote.quoteContent.substring(startIndex, endIndex))
            }
            append(quote.quoteContent.substring(endIndex))
        } else {
            append(quote.quoteContent)
        }
    }

    Text(
        text = annotatedString,
        color = when {
            isSpecialCard -> Color.White
            else -> QuoteColors.textPrimary()
        },
        fontSize = 18.sp,
        lineHeight = 26.sp,
        fontWeight = FontWeight.Medium,
        fontStyle = if (isSpecialCard) FontStyle.Italic else FontStyle.Normal,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
private fun QuoteAuthor(
    authorName: String,
    onAuthorClick: () -> Unit,
    isSpecialCard: Boolean
) {
    Text(
        text = stringResource(id = R.string.quote_author_prefix, authorName),
        style = MaterialTheme.typography.bodyMedium.copy(
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Normal
        ),
        color = when {
            isSpecialCard -> Color.White.copy(alpha = 0.9f)
            else -> QuoteColors.textSecondary()
        },
        fontSize = 14.sp,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable { onAuthorClick() }
            .padding(4.dp)
            .padding(bottom = 12.dp)
    )
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
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        // Primary Actions (Left side)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
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
                selectedColor = if (isSpecialCard) QuoteColors.accentGold() else QuoteColors.success()
            )

            ActionButton(
                icon = Icons.Default.Share,
                onClick = { onShare(quote) },
                isSpecialCard = isSpecialCard
            )
        }

        // Secondary Actions (Right side)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            ActionButton(
                icon = if (isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                onClick = onToggleFavorite,
                isSelected = isFavorited,
                selectedColor = QuoteColors.accentPink(),
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
    selectedColor: Color = QuoteColors.accentBlue(),
    isSpecialCard: Boolean = false,
    isPrimary: Boolean = false,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = tween(200)
    )

    val backgroundColor = when {
        isPrimary && isSpecialCard -> QuoteColors.accentGold().copy(alpha = 0.2f)
        isPrimary -> Color(0x1A3B82F6)
        isSpecialCard -> Color.White.copy(alpha = 0.15f)
        else -> if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.05f) else Color.Black.copy(
            alpha = 0.05f
        )
    }

    val iconColor = when {
        isSelected -> selectedColor
        isPrimary && isSpecialCard -> QuoteColors.accentGold()
        isPrimary -> QuoteColors.accentBlue()
        isSpecialCard -> Color.White
        else -> QuoteColors.textSecondary()
    }

    Box(
        modifier = modifier
            .scale(scale)
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(20.dp)
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
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        modifier = modifier.fillMaxWidth()
    ) {
        items(tags) { tag ->
            TagChip(
                tag = tag,
                onClick = { onTagClick(tag) },
                isSpecialCard = isSpecialCard
            )
        }
    }
}

@Composable
private fun TagChip(
    tag: String,
    onClick: () -> Unit,
    isSpecialCard: Boolean = false
) {
    val backgroundColor = when {
        isSpecialCard -> Color.White.copy(alpha = 0.2f)
        else -> QuoteColors.tagBackground()
    }

    val textColor = when {
        isSpecialCard -> Color.White.copy(alpha = 0.8f)
        else -> QuoteColors.tagText()
    }

    Text(
        text = "#${tag.lowercase()}",
        color = textColor,
        fontSize = 10.sp,
        fontWeight = FontWeight.Normal,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}

@Composable
fun SelectableItemCard(
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    selectedColor: Color = QuoteColors.primary(),
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .clip(RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = QuoteColors.surfacePrimary()
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        ),
        border = if (isSelected) {
            BorderStroke(2.dp, selectedColor)
        } else {
            BorderStroke(
                1.dp,
                QuoteColors.border()
            )
        }
    ) {
        content()
    }
}
