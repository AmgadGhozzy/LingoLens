package com.venom.quote.ui.screen

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.domain.model.Author
import com.venom.domain.model.Quote
import com.venom.domain.model.Tag
import com.venom.quote.ui.components.ActiveFiltersRow
import com.venom.quote.ui.components.AuthorCard
import com.venom.quote.ui.components.AuthorInfoDialog
import com.venom.quote.ui.components.FilterDialog
import com.venom.quote.ui.components.QuoteCard
import com.venom.quote.ui.components.QuoteTab
import com.venom.quote.ui.components.QuoteTabs
import com.venom.quote.ui.components.SearchBar
import com.venom.quote.ui.components.TopicsGrid
import com.venom.quote.ui.viewmodel.QuoteUiState
import com.venom.quote.ui.viewmodel.QuoteViewModel
import com.venom.resources.R
import com.venom.ui.theme.ThemeColors.Blue
import com.venom.utils.Extensions.copyToClipboard
import com.venom.utils.Extensions.shareText

@Composable
fun QuoteScreen(
    quoteViewModel: QuoteViewModel = hiltViewModel(LocalActivity.current as ComponentActivity)
) {
    val state by quoteViewModel.uiState.collectAsState()
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(QuoteTab.QUOTES) }

    // Handle back press to navigate back to QUOTES tab
    BackHandler(enabled = selectedTab != QuoteTab.QUOTES) {
        selectedTab = QuoteTab.QUOTES
    }

    isSystemInDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Search bar with optimized callbacks
        SearchBar(
            query = state.searchQuery,
            onQueryChange = quoteViewModel::updateSearchQuery,
            onSearch = quoteViewModel::updateSearchQuery,
            suggestions = state.searchSuggestions,
            recentSearches = state.recentSearches,
            onClearRecentSearches = quoteViewModel::clearRecentSearches,
            onFilterClick = { showFilterDialog = true },
            hasActiveFilters = state.filterOptions.hasActiveFilters()
        )

        // Enhanced active filters row
        ActiveFiltersRow(
            filterOptions = state.filterOptions,
            onRemoveFilter = quoteViewModel::removeFilter,
            onClearAll = quoteViewModel::clearAllFilters,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        QuoteTabs(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )

        // Content based on selected tab
        Box(modifier = Modifier.fillMaxSize()) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Blue
                )
            } else {
                QuoteContent(
                    viewModel = quoteViewModel,
                    state = state,
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }
        }


        // Filter dialog with multi-selection support
        FilterDialog(
            visible = showFilterDialog,
            onDismiss = { showFilterDialog = false },
            onApplyFilters = quoteViewModel::applyFilters,
            currentFilters = state.filterOptions,
            authors = state.availableAuthors,
            tags = state.availableTags
        )

        // Author details dialog
        state.selectedAuthor?.let { author ->
            AuthorInfoDialog(
                author = author,
                onClose = { quoteViewModel.setSelectedAuthor(null) },
                onViewAllQuotes = { authorName ->
                    quoteViewModel.loadQuotesByAuthor(authorName)
                    selectedTab = QuoteTab.QUOTES
                    quoteViewModel.setSelectedAuthor(null)
                }
            )
        }
    }
}

@Composable
private fun QuoteContent(
    viewModel: QuoteViewModel,
    state: QuoteUiState,
    selectedTab: QuoteTab,
    onTabSelected: (QuoteTab) -> Unit
) {
    when (selectedTab) {
        QuoteTab.QUOTES -> {
            QuotesTabContent(
                viewModel = viewModel,
                quotes = state.filteredQuotes,
                searchQuery = state.searchQuery,
                dailyQuote = state.dailyQuote,
                hasFilters = state.filterOptions.hasActiveFilters() || state.searchQuery.isNotEmpty()
            )
        }

        QuoteTab.FAVORITES -> {
            QuotesTabContent(
                viewModel = viewModel,
                quotes = state.filteredQuotes.filter { it.isFavorite },
                searchQuery = state.searchQuery,
                dailyQuote = null,
                hasFilters = false
            )
        }

        QuoteTab.AUTHORS -> {
            AuthorsTabContent(
                viewModel = viewModel,
                authors = state.availableAuthors,
                selectedAuthors = state.filterOptions.authors,
                onToggleAuthor = viewModel::toggleAuthorFilter,
                onSelectTab = onTabSelected
            )
        }

        QuoteTab.TOPICS -> {
            TopicsTabContent(
                viewModel = viewModel,
                tags = state.availableTags,
                selectedTags = state.filterOptions.tags,
                onToggleTag = viewModel::toggleTagFilter,
                onSelectTab = onTabSelected
            )
        }
    }
}

@Composable
private fun QuotesTabContent(
    viewModel: QuoteViewModel,
    quotes: List<Quote>,
    searchQuery: String,
    dailyQuote: Quote?,
    hasFilters: Boolean
) {

    val cotext = LocalContext.current
    if (quotes.isEmpty()) {
        EmptyStateContent(
            message = stringResource(if (hasFilters) R.string.no_quotes_filtered else R.string.no_quotes_available),
            description = if (hasFilters) stringResource(R.string.try_adjusting_filters) else null
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Daily quote only when no filters are active
        if (dailyQuote != null && !hasFilters) {
            item("daily_quote") {
                QuoteCard(
                    quote = dailyQuote,
                    index = 0,
                    onToggleFavorite = { viewModel.toggleFavorite(dailyQuote.quoteId) },
                    onAuthorClick = { viewModel.toggleAuthorFilter(dailyQuote.authorName) },
                    isFavorited = dailyQuote.isFavorite,
                    onPlayAudio = { },
                    onShare = { cotext.shareText(dailyQuote.quoteContent) },
                    onCopy = { cotext.copyToClipboard(dailyQuote.quoteContent) },
                    onTagClick = { viewModel.toggleTagFilter(dailyQuote.tags.first()) },
                    showDailyHeader = true,
                    useGradientBackground = true
                )
            }
        }

        itemsIndexed(
            items = quotes,
            key = { index, sentence -> "quote_$index" }
        ) { index, quote ->
            QuoteCard(
                quote = quote,
                index = index + 1,
                searchQuery = searchQuery,
                onToggleFavorite = { viewModel.toggleFavorite(quote.quoteId) },
                onAuthorClick = { viewModel.toggleAuthorFilter(quote.authorName) },
                isFavorited = quote.isFavorite,
                onPlayAudio = { },
                onShare = { cotext.shareText(quote.quoteContent) },
                onCopy = { cotext.copyToClipboard(quote.quoteContent) },
                onTagClick = { viewModel.toggleTagFilter(quote.tags.first()) }
            )
        }
    }
}

@Composable
private fun AuthorsTabContent(
    viewModel: QuoteViewModel,
    authors: List<Author>,
    selectedAuthors: Set<String>,
    onToggleAuthor: (String) -> Unit,
    onSelectTab: (QuoteTab) -> Unit
) {
    if (authors.isEmpty()) {
        EmptyStateContent(
            message = stringResource(R.string.no_authors_found),
            description = stringResource(R.string.authors_coming_soon)
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = authors,
            key = { it.authorId }
        ) { author ->
            AuthorCard(
                author = author,
                isSelected = selectedAuthors.contains(author.authorName),
                onClick = {
                    viewModel.loadQuotesByAuthor(author.authorName)
                    onSelectTab(QuoteTab.QUOTES)
                },
                onToggleSelection = { onToggleAuthor(author.authorName) },
                onViewDetails = { viewModel.setSelectedAuthor(author) }
            )

        }
    }
}

@Composable
private fun TopicsTabContent(
    viewModel: QuoteViewModel,
    tags: List<Tag>,
    selectedTags: Set<String>,
    onToggleTag: (String) -> Unit,
    onSelectTab: (QuoteTab) -> Unit
) {
    if (tags.isEmpty()) {
        EmptyStateContent(
            message = stringResource(R.string.no_topics_found),
            description = stringResource(R.string.topics_coming_soon)
        )
        return
    }

    TopicsGrid(
        tags = tags,
        selectedTags = selectedTags,
        onTagClick = { tagName ->
            viewModel.loadQuotesByTag(tagName)
            onSelectTab(QuoteTab.QUOTES)
        },
        onTagToggle = onToggleTag
    )
}

@Composable
private fun EmptyStateContent(
    message: String,
    description: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.SearchOff,
            contentDescription = stringResource(R.string.search_icon_content_desc),
            modifier = Modifier.size(64.dp),
            tint = Color(0xFF6B7280).copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6B7280),
            textAlign = TextAlign.Center
        )
        if (description != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color(0xFF9CA3AF),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}
