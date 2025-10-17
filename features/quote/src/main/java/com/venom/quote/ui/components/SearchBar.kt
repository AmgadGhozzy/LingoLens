package com.venom.quote.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowOutward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.venom.resources.R
import com.venom.ui.theme.QuoteColors

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,

    suggestions: List<String> = emptyList(),
    recentSearches: List<String> = emptyList(),
    onClearRecentSearches: () -> Unit = {},
    onRemoveRecentSearch: (String) -> Unit = {},
    onFilterClick: () -> Unit = {},
    hasActiveFilters: Boolean = false,
    placeholder: String? = null
) {
    var isActive by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val showDropdown = isActive && (suggestions.isNotEmpty() || recentSearches.isNotEmpty())

    Column(modifier) {
        Card(
            Modifier
                .fillMaxWidth()
                .zIndex(if (isActive) 10f else 1f),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(if (isActive) 6.dp else 2.dp),
            border = if (isActive) BorderStroke(1.dp, QuoteColors.primary()) else null,
            colors = CardDefaults.cardColors(QuoteColors.surfacePrimary())
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Search,
                    stringResource(R.string.search_hint),
                    Modifier.size(22.dp),
                    tint = if (isActive) QuoteColors.primary() else QuoteColors.textSecondary()
                )

                Spacer(Modifier.width(16.dp))

                BasicTextField(
                    query,
                    onQueryChange,
                    Modifier
                        .weight(1f)
                        .focusRequester(focusRequester)
                        .onFocusChanged { isActive = it.isFocused },
                    textStyle = TextStyle(
                        QuoteColors.textPrimary(),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    cursorBrush = SolidColor(QuoteColors.primary()),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions {
                        if (query.isNotBlank()) {
                            onSearch(query.trim())
                            keyboardController?.hide()
                            focusRequester.freeFocus()
                        }
                    }
                ) { innerTextField ->
                    if (query.isEmpty()) {
                        Text(
                            placeholder ?: stringResource(R.string.search_hint),
                            color = QuoteColors.textTertiary(),
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                }

                Spacer(Modifier.width(12.dp))

                if (query.isNotEmpty()) {
                    IconButton(
                        { onQueryChange("") },
                        Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(QuoteColors.surfaceSecondary())
                    ) {
                        Icon(
                            Icons.Default.Close,
                            stringResource(R.string.action_clear_search),
                            Modifier.size(16.dp),
                            tint = QuoteColors.textSecondary()
                        )
                    }
                } else {
                    Box(
                        Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (hasActiveFilters) QuoteColors.tagBackground()
                                else QuoteColors.surfaceSecondary()
                            )
                            .clickable { onFilterClick() },
                        Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.FilterList,
                            "Filter",
                            Modifier.size(16.dp),
                            tint = if (hasActiveFilters) QuoteColors.secondary()
                            else QuoteColors.textSecondary()
                        )

                        if (hasActiveFilters) {
                            Box(
                                Modifier
                                    .size(6.dp)
                                    .offset(x = 8.dp, y = (-8).dp)
                                    .background(QuoteColors.secondary(), CircleShape)
                                    .align(Alignment.TopEnd)
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        AnimatedVisibility(
            showDropdown,
            enter = fadeIn(tween(200)) + slideInVertically(tween(200)) { -it / 3 },
            exit = fadeOut(tween(150)) + slideOutVertically(tween(150)) { -it / 3 }
        ) {
            Card(
                Modifier
                    .fillMaxWidth()
                    .zIndex(20f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(QuoteColors.surfacePrimary()),
                elevation = CardDefaults.cardElevation(8.dp),
                border = BorderStroke(1.dp, QuoteColors.border())
            ) {
                LazyColumn(Modifier.heightIn(max = 280.dp)) {
                    if (recentSearches.isNotEmpty() && query.isEmpty()) {
                        item {
                            SectionHeader(
                                Icons.Default.History,
                                "RECENT",
                                "Clear",
                                onClearRecentSearches
                            )
                        }

                        itemsIndexed(recentSearches.take(5)) { index, recent ->
                            SuggestionItem(
                                recent,
                                Icons.Default.History,
                                {
                                    onQueryChange(recent)
                                    onSearch(recent)
                                    focusRequester.freeFocus()
                                    keyboardController?.hide()
                                },
                                { onRemoveRecentSearch(recent) },
                                true,
                                index < minOf(
                                    recentSearches.size,
                                    5
                                ) - 1 || suggestions.isNotEmpty()
                            )
                        }
                    }

                    if (suggestions.isNotEmpty()) {
                        item {
                            SectionHeader(
                                Icons.AutoMirrored.Default.TrendingUp,
                                if (query.isEmpty()) "POPULAR" else "SUGGESTIONS",
                                tintColor = QuoteColors.primary()
                            )
                        }

                        itemsIndexed(suggestions.take(6)) { index, suggestion ->
                            SuggestionItem(
                                suggestion,
                                Icons.Default.Search,
                                {
                                    onQueryChange(suggestion)
                                    onSearch(suggestion)
                                    focusRequester.freeFocus()
                                    keyboardController?.hide()
                                },
                                null,
                                false,
                                index < minOf(suggestions.size, 6) - 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    tintColor: androidx.compose.ui.graphics.Color = QuoteColors.textSecondary()
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(20.dp, 16.dp, 20.dp, 8.dp),
        Arrangement.SpaceBetween,
        Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, Modifier.size(14.dp), tint = tintColor)
            Spacer(Modifier.width(8.dp))
            Text(
                title,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = QuoteColors.textSecondary()
            )
        }

        if (actionText != null && onActionClick != null) {
            TextButton(onActionClick, contentPadding = PaddingValues(8.dp, 4.dp)) {
                Text(
                    actionText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = QuoteColors.primary()
                )
            }
        }
    }
}

@Composable
private fun SuggestionItem(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    onRemove: (() -> Unit)?,
    isRecent: Boolean,
    showDivider: Boolean
) {
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(20.dp, 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isRecent) {
                Icon(icon, null, Modifier.size(18.dp), tint = QuoteColors.textTertiary())
            } else {
                Box(
                    Modifier
                        .size(28.dp)
                        .background(QuoteColors.tagBackground(), RoundedCornerShape(7.dp)),
                    Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Search,
                        null,
                        Modifier.size(14.dp),
                        tint = QuoteColors.secondary()
                    )
                }
            }

            Spacer(Modifier.width(14.dp))

            Text(
                text,
                Modifier.weight(1f),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = QuoteColors.textPrimary(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (isRecent && onRemove != null) {
                IconButton({ onRemove() }, Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Close,
                        "Remove",
                        Modifier.size(14.dp),
                        tint = QuoteColors.textTertiary()
                    )
                }
            } else if (!isRecent) {
                Icon(
                    Icons.Outlined.ArrowOutward,
                    null,
                    Modifier.size(16.dp),
                    tint = QuoteColors.textTertiary()
                )
            }
        }

        if (showDivider) {
            HorizontalDivider(
                Modifier.padding(start = 62.dp, end = 20.dp),
                thickness = 0.5.dp,
                color = QuoteColors.border().copy(0.3f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    var query by remember { mutableStateOf("") }
    val suggestions = listOf("Motivation", "Success", "Life", "Happiness", "Wisdom", "Growth")
    val recentSearches = listOf("Inspiration", "Peace", "Creativity", "Love", "Hope")

    Column(Modifier.padding(16.dp)) {
        SearchBar(
            query = query,
            onQueryChange = { query = it },
            suggestions = if (query.isNotEmpty()) suggestions.filter {
                it.contains(
                    query,
                    true
                )
            } else suggestions,
            recentSearches = recentSearches,
            onClearRecentSearches = {},
            onRemoveRecentSearch = {},
            onSearch = {},
            hasActiveFilters = true,
        )
    }
}