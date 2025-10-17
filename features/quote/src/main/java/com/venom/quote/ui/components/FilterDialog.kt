package com.venom.quote.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Tag
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.venom.domain.model.Author
import com.venom.domain.model.Tag
import com.venom.resources.R
import com.venom.ui.theme.QuoteColors
import kotlinx.coroutines.delay

data class FilterOptions(
    val authors: Set<String> = emptySet(),
    val tags: Set<String> = emptySet(),
    val favoritesOnly: Boolean = false
) {
    fun hasActiveFilters() = authors.isNotEmpty() || tags.isNotEmpty() || favoritesOnly
    fun getFilterCount() = authors.size + tags.size + if (favoritesOnly) 1 else 0

    fun toggleAuthor(author: String) = copy(
        authors = if (author in authors) authors - author else authors + author
    )

    fun toggleTag(tag: String) = copy(
        tags = if (tag in tags) tags - tag else tags + tag
    )

    fun clear() = FilterOptions()
}

@Composable
fun FilterDialog(
    visible: Boolean,
    currentFilters: FilterOptions,
    authors: List<Author> = emptyList(),
    tags: List<Tag> = emptyList(),
    onDismiss: () -> Unit,
    onApplyFilters: (FilterOptions) -> Unit
) {
    var localFilters by remember(currentFilters) { mutableStateOf(currentFilters) }
    var isVisible by remember(visible) { mutableStateOf(false) }
    val density = LocalDensity.current

    LaunchedEffect(visible) {
        if (visible) {
            delay(50)
            isVisible = true
        } else {
            isVisible = false
        }
    }

    if (visible) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        ),
                        initialOffsetY = { with(density) { 40.dp.roundToPx() } }
                    ) + fadeIn(animationSpec = tween(300)),
                    exit = slideOutVertically(
                        animationSpec = tween(250),
                        targetOffsetY = { with(density) { 40.dp.roundToPx() } }
                    ) + fadeOut(animationSpec = tween(250))
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .fillMaxHeight(0.85f)
                            .clickable(enabled = false) { },
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = QuoteColors.surfacePrimary()
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp)
                        ) {
                            // Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(id = R.string.filter_quotes),
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp
                                    ),
                                    color = QuoteColors.textPrimary()
                                )

                                IconButton(
                                    onClick = onDismiss,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            QuoteColors.surfaceSecondary(),
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = stringResource(id = R.string.action_close),
                                        tint = QuoteColors.textSecondary(),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Content
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(24.dp)
                            ) {
                                // Favorites Toggle
                                item {
                                    FilterSection(title = stringResource(id = R.string.quick_filters)) {
                                        FilterCheckbox(
                                            text = stringResource(id = R.string.favorites_only),
                                            checked = localFilters.favoritesOnly,
                                            onCheckedChange = { localFilters = localFilters.copy(favoritesOnly = it) },
                                            color = QuoteColors.accentPink()
                                        )
                                    }
                                }

                                // Authors Section
                                if (authors.isNotEmpty()) {
                                    item {
                                        FilterSection(title = stringResource(id = R.string.authors)) {
                                            LazyColumn(
                                                modifier = Modifier.heightIn(max = 200.dp),
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                items(authors, key = { it.authorId }) { author ->
                                                    FilterCheckbox(
                                                        text = stringResource(
                                                            id = R.string.author_with_count,
                                                            author.authorName,
                                                            author.quotesCount
                                                        ),
                                                        checked = author.authorName in localFilters.authors,
                                                        onCheckedChange = { checked ->
                                                            localFilters = localFilters.copy(
                                                                authors = if (checked) {
                                                                    localFilters.authors + author.authorName
                                                                } else {
                                                                    localFilters.authors - author.authorName
                                                                }
                                                            )
                                                        },
                                                        color = QuoteColors.primary()
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // Tags Section
                                if (tags.isNotEmpty()) {
                                    item {
                                        FilterSection(title = stringResource(id = R.string.tags)) {
                                            LazyColumn(
                                                modifier = Modifier.heightIn(max = 240.dp),
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                items(tags.chunked(2)) { rowTags ->
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        rowTags.forEach { tag ->
                                                            Box(modifier = Modifier.weight(1f)) {
                                                                FilterCheckbox(
                                                                    text = stringResource(
                                                                        id = R.string.tag_with_count,
                                                                        tag.tagName,
                                                                        tag.count
                                                                    ),
                                                                    checked = tag.tagName in localFilters.tags,
                                                                    onCheckedChange = { checked ->
                                                                        localFilters = localFilters.copy(
                                                                            tags = if (checked) {
                                                                                localFilters.tags + tag.tagName
                                                                            } else {
                                                                                localFilters.tags - tag.tagName
                                                                            }
                                                                        )
                                                                    },
                                                                    color = QuoteColors.secondary(),
                                                                    compact = true
                                                                )
                                                            }
                                                        }
                                                        if (rowTags.size == 1) {
                                                            Spacer(modifier = Modifier.weight(1f))
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Action Buttons
                            val hasFilters = localFilters.hasActiveFilters()
                            val filterCount = localFilters.getFilterCount()

                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Button(
                                    onClick = {
                                        onApplyFilters(localFilters)
                                        onDismiss()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Transparent
                                    ),
                                    contentPadding = PaddingValues(0.dp),
                                    elevation = null
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.horizontalGradient(
                                                    colors = listOf(
                                                        QuoteColors.primary(),
                                                        QuoteColors.secondary()
                                                    )
                                                ),
                                                RoundedCornerShape(16.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = stringResource(id = R.string.apply_filters),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White
                                            )

                                            if (hasFilters) {
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .background(
                                                            Color.White.copy(alpha = 0.2f),
                                                            CircleShape
                                                        )
                                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = filterCount.toString(),
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                AnimatedVisibility(
                                    visible = hasFilters,
                                    enter = expandVertically() + fadeIn(),
                                    exit = shrinkVertically() + fadeOut()
                                ) {
                                    OutlinedButton(
                                        onClick = { localFilters = FilterOptions() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = QuoteColors.textSecondary()
                                        ),
                                        border = BorderStroke(1.dp, QuoteColors.border())
                                    ) {
                                        Text(
                                            text = stringResource(id = R.string.clear_all_filters),
                                            fontSize = 14.sp,
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
}

@Composable
private fun FilterSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            ),
            color = QuoteColors.textPrimary(),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        content()
    }
}

@Composable
private fun FilterCheckbox(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    color: Color,
    compact: Boolean = false
) {
    val size = if (compact) 16.dp else 20.dp
    val fontSize = if (compact) 12.sp else 14.sp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onCheckedChange(!checked) }
            .background(if (checked) color.copy(alpha = 0.08f) else Color.Transparent)
            .padding(vertical = if (compact) 8.dp else 10.dp, horizontal = 8.dp)
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(if (compact) 4.dp else 6.dp))
                .background(if (checked) color else Color.Transparent)
                .border(
                    2.dp,
                    if (checked) color else QuoteColors.border(),
                    RoundedCornerShape(if (compact) 4.dp else 6.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = checked,
                enter = scaleIn(spring(Spring.DampingRatioMediumBouncy)) + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(size * 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = if (checked) FontWeight.SemiBold else FontWeight.Normal,
            color = if (checked) color else QuoteColors.textPrimary(),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ActiveFiltersRow(
    filterOptions: FilterOptions,
    onRemoveFilter: (filterType: String, value: String) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!filterOptions.hasActiveFilters()) return

    Column(modifier = modifier.animateContentSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(
                    id = R.string.active_filters_count,
                    filterOptions.getFilterCount()
                ),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = QuoteColors.textSecondary()
            )

            if (filterOptions.getFilterCount() > 1) {
                TextButton(
                    onClick = onClearAll,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.clear_all),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = QuoteColors.textTertiary()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            if (filterOptions.favoritesOnly) {
                item(key = "favorites") {
                    FilterChip(
                        label = stringResource(id = R.string.favorites),
                        color = QuoteColors.accentPink(),
                        icon = Icons.Outlined.FavoriteBorder,
                        onRemove = { onRemoveFilter("favorites", "Favorites") }
                    )
                }
            }

            items(
                items = filterOptions.authors.toList(),
                key = { "author_$it" }
            ) { author ->
                FilterChip(
                    label = author,
                    color = QuoteColors.primary(),
                    icon = Icons.Outlined.Person,
                    onRemove = { onRemoveFilter("author", author) }
                )
            }

            items(
                items = filterOptions.tags.toList(),
                key = { "tag_$it" }
            ) { tag ->
                FilterChip(
                    label = tag,
                    color = QuoteColors.secondary(),
                    icon = Icons.Outlined.Tag,
                    onRemove = { onRemoveFilter("tag", tag) }
                )
            }
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    color: Color,
    icon: ImageVector? = null,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = Modifier.animateContentSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(14.dp)
                )
            }

            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = color,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(id = R.string.remove_filter, label),
                    tint = color,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FilterDialogPreview() {
    MaterialTheme {
        FilterDialog(
            visible = true,
            currentFilters = FilterOptions(
                authors = setOf("Marcus Aurelius"),
                tags = setOf("wisdom", "philosophy"),
                favoritesOnly = false
            ),
            authors = listOf(
                Author(authorId = 1, authorName = "Marcus Aurelius", quotesCount = 45),
                Author(authorId = 2, authorName = "The Buddha", quotesCount = 32),
                Author(authorId = 3, authorName = "Steve Jobs", quotesCount = 28),
                Author(authorId = 4, authorName = "Thomas Edison", quotesCount = 19)
            ),
            tags = listOf(
                Tag(1, "wisdom", 156),
                Tag(2, "motivation", 134),
                Tag(3, "success", 89),
                Tag(4, "inspiration", 67),
                Tag(5, "life", 198),
                Tag(6, "happiness", 76)
            ),
            onDismiss = {},
            onApplyFilters = {}
        )
    }
}