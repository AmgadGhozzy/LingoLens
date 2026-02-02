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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.venom.domain.model.Author
import com.venom.domain.model.Tag
import com.venom.resources.R
import com.venom.ui.components.common.adp
import com.venom.ui.components.common.asp
import com.venom.ui.theme.lingoLens
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
    val colorScheme = MaterialTheme.colorScheme
    val feature = MaterialTheme.lingoLens.feature

    LaunchedEffect(visible) {
        if (visible) {
            delay(50)
            isVisible = true
        } else {
            isVisible = false
        }
    }

    if (visible) {
        val animationOffsetPx = with(density) { 40.adp.roundToPx() }
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
                    .background(Color.Black.copy(0.5f))
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
                        initialOffsetY = { animationOffsetPx }
                    ) + fadeIn(animationSpec = tween(300)),
                    exit = slideOutVertically(
                        animationSpec = tween(250),
                        targetOffsetY = { animationOffsetPx }
                    ) + fadeOut(animationSpec = tween(250))
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .fillMaxHeight(0.85f)
                            .clickable(enabled = false) { },
                        shape = RoundedCornerShape(24.adp),
                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 24.adp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.adp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(id = R.string.filter_quotes),
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.asp
                                    ),
                                    color = colorScheme.onSurface
                                )

                                IconButton(
                                    onClick = onDismiss,
                                    modifier = Modifier
                                        .size(40.adp)
                                        .background(
                                            colorScheme.surfaceContainerHighest,
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = stringResource(id = R.string.action_close),
                                        tint = colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.adp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.adp))

                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(24.adp)
                            ) {
                                item {
                                    FilterSection(title = stringResource(id = R.string.quick_filters)) {
                                        FilterCheckbox(
                                            text = stringResource(id = R.string.favorites_only),
                                            checked = localFilters.favoritesOnly,
                                            onCheckedChange = {
                                                localFilters = localFilters.copy(favoritesOnly = it)
                                            },
                                            color = feature.quote.pink
                                        )
                                    }
                                }

                                if (authors.isNotEmpty()) {
                                    item {
                                        FilterSection(title = stringResource(id = R.string.authors)) {
                                            LazyColumn(
                                                modifier = Modifier.heightIn(max = 200.adp),
                                                verticalArrangement = Arrangement.spacedBy(4.adp)
                                            ) {
                                                items(authors, key = { it.authorId }) { author ->
                                                    FilterCheckbox(
                                                        text = stringResource(
                                                            R.string.author_with_count,
                                                            author.authorName,
                                                            author.quotesCount
                                                        ),
                                                        checked = author.authorName in localFilters.authors,
                                                        onCheckedChange = { checked ->
                                                            localFilters = localFilters.copy(
                                                                authors = if (checked) localFilters.authors + author.authorName
                                                                else localFilters.authors - author.authorName
                                                            )
                                                        },
                                                        color = colorScheme.primary
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                if (tags.isNotEmpty()) {
                                    item {
                                        FilterSection(title = stringResource(id = R.string.tags)) {
                                            LazyColumn(
                                                modifier = Modifier.heightIn(max = 240.adp),
                                                verticalArrangement = Arrangement.spacedBy(4.adp)
                                            ) {
                                                items(tags.chunked(2)) { rowTags ->
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.spacedBy(
                                                            8.adp
                                                        )
                                                    ) {
                                                        rowTags.forEach { tag ->
                                                            Box(modifier = Modifier.weight(1f)) {
                                                                FilterCheckbox(
                                                                    text = stringResource(
                                                                        R.string.tag_with_count,
                                                                        tag.tagName,
                                                                        tag.count
                                                                    ),
                                                                    checked = tag.tagName in localFilters.tags,
                                                                    onCheckedChange = { checked ->
                                                                        localFilters =
                                                                            localFilters.copy(
                                                                                tags = if (checked) localFilters.tags + tag.tagName
                                                                                else localFilters.tags - tag.tagName
                                                                            )
                                                                    },
                                                                    color = colorScheme.secondary,
                                                                    compact = true
                                                                )
                                                            }
                                                        }
                                                        if (rowTags.size == 1) Spacer(
                                                            modifier = Modifier.weight(
                                                                1f
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            val hasFilters = localFilters.hasActiveFilters()
                            val filterCount = localFilters.getFilterCount()

                            Column(verticalArrangement = Arrangement.spacedBy(12.adp)) {
                                Button(
                                    onClick = { onApplyFilters(localFilters); onDismiss() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.adp),
                                    shape = RoundedCornerShape(16.adp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                    contentPadding = PaddingValues(0.adp),
                                    elevation = null
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.horizontalGradient(
                                                    listOf(
                                                        colorScheme.primary,
                                                        colorScheme.secondary
                                                    )
                                                ),
                                                RoundedCornerShape(16.adp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                stringResource(R.string.apply_filters),
                                                fontSize = 16.asp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White
                                            )
                                            if (hasFilters) {
                                                Spacer(modifier = Modifier.width(8.adp))
                                                Box(
                                                    modifier = Modifier
                                                        .background(
                                                            Color.White.copy(
                                                                0.2f
                                                            ), CircleShape
                                                        )
                                                        .padding(
                                                            horizontal = 8.adp,
                                                            vertical = 4.adp
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        filterCount.toString(),
                                                        fontSize = 12.asp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                AnimatedVisibility(
                                    hasFilters,
                                    enter = expandVertically() + fadeIn(),
                                    exit = shrinkVertically() + fadeOut()
                                ) {
                                    OutlinedButton(
                                        onClick = { localFilters = FilterOptions() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.adp),
                                        shape = RoundedCornerShape(16.adp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colorScheme.onSurfaceVariant),
                                        border = BorderStroke(1.adp, colorScheme.outlineVariant)
                                    ) {
                                        Text(
                                            stringResource(R.string.clear_all_filters),
                                            fontSize = 14.asp,
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
private fun FilterSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.asp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.adp)
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
    val colorScheme = MaterialTheme.colorScheme
    val size = if (compact) 16.adp else 20.adp
    val fontSize = if (compact) 12.asp else 14.asp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.adp))
            .clickable { onCheckedChange(!checked) }
            .background(if (checked) color.copy(0.08f) else Color.Transparent)
            .padding(vertical = if (compact) 8.adp else 10.adp, horizontal = 8.adp)
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(if (compact) 4.adp else 6.adp))
                .background(if (checked) color else Color.Transparent)
                .border(
                    2.adp,
                    if (checked) color else colorScheme.outlineVariant,
                    RoundedCornerShape(if (compact) 4.adp else 6.adp)
                ),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.animation.AnimatedVisibility(
                checked,
                enter = scaleIn(spring(Spring.DampingRatioMediumBouncy)) + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                Icon(
                    Icons.Default.Check,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(size * 0.6f)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.adp))
        Text(
            text,
            fontSize = fontSize,
            fontWeight = if (checked) FontWeight.SemiBold else FontWeight.Normal,
            color = if (checked) color else colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ActiveFiltersRow(
    filterOptions: FilterOptions,
    onRemoveFilter: (String, String) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!filterOptions.hasActiveFilters()) return
    val colorScheme = MaterialTheme.colorScheme
    val feature = MaterialTheme.lingoLens.feature

    Column(modifier = modifier.animateContentSize()) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text(
                stringResource(R.string.active_filters_count, filterOptions.getFilterCount()),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colorScheme.onSurfaceVariant
            )
            if (filterOptions.getFilterCount() > 1) {
                TextButton(
                    onClearAll,
                    contentPadding = PaddingValues(horizontal = 8.adp, vertical = 4.adp)
                ) {
                    Text(
                        stringResource(R.string.clear_all),
                        fontSize = 12.asp,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.onSurfaceVariant.copy(0.7f)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.adp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.adp),
            contentPadding = PaddingValues(vertical = 4.adp)
        ) {
            if (filterOptions.favoritesOnly) {
                item("favorites") {
                    FilterChip(
                        stringResource(R.string.favorites),
                        feature.quote.pink,
                        Icons.Outlined.FavoriteBorder
                    ) { onRemoveFilter("favorites", "Favorites") }
                }
            }
            items(filterOptions.authors.toList(), key = { "author_$it" }) { author ->
                FilterChip(
                    author,
                    colorScheme.primary,
                    Icons.Outlined.Person
                ) { onRemoveFilter("author", author) }
            }
            items(filterOptions.tags.toList(), key = { "tag_$it" }) { tag ->
                FilterChip(tag, colorScheme.secondary, Icons.Outlined.Tag) {
                    onRemoveFilter(
                        "tag",
                        tag
                    )
                }
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
        shape = RoundedCornerShape(20.adp),
        color = color.copy(0.1f),
        border = BorderStroke(1.adp, color.copy(0.3f)),
        modifier = Modifier.animateContentSize()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.adp, vertical = 8.adp),
            horizontalArrangement = Arrangement.spacedBy(6.adp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let { Icon(it, null, tint = color, modifier = Modifier.size(14.adp)) }
            Text(
                label,
                fontSize = 12.asp,
                fontWeight = FontWeight.Medium,
                color = color,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            IconButton(onRemove, Modifier.size(20.adp)) {
                Icon(
                    Icons.Default.Close,
                    stringResource(R.string.remove_filter, label),
                    tint = color,
                    modifier = Modifier.size(12.adp)
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
                Author(1, "Marcus Aurelius", quotesCount = 45),
                Author(2, "The Buddha", quotesCount = 32)
            ),
            tags = listOf(Tag(1, "wisdom", 156), Tag(2, "motivation", 134)),
            onDismiss = {},
            onApplyFilters = {}
        )
    }
}
