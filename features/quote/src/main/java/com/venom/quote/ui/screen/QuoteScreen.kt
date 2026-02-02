package com.venom.quote.ui.screen

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.venom.quote.ui.viewmodel.QuoteViewModel
import com.venom.resources.R
import com.venom.ui.components.common.adp
import com.venom.ui.components.common.asp
import com.venom.utils.Extensions.copyToClipboard
import com.venom.utils.Extensions.shareText

@Composable
fun QuoteScreen(
    quoteViewModel: QuoteViewModel = hiltViewModel(LocalActivity.current as ComponentActivity)
) {
    val state by quoteViewModel.uiState.collectAsState()
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(QuoteTab.QUOTES) }
    val context = LocalContext.current

    // Handle back press to navigate back to QUOTES tab
    BackHandler(enabled = selectedTab != QuoteTab.QUOTES) {
        selectedTab = QuoteTab.QUOTES
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Main content - Single LazyColumn for everything
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 24.adp,
                end = 24.adp,
                top = 164.adp, // Space for floating header
                bottom = 24.adp
            ),
            verticalArrangement = Arrangement.spacedBy(12.adp)
        ) {
            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.adp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
            } else {
                // Render content based on selected tab
                when (selectedTab) {
                    QuoteTab.QUOTES -> {
                        renderQuotesTab(
                            viewModel = quoteViewModel,
                            quotes = state.filteredQuotes,
                            searchQuery = state.searchQuery,
                            dailyQuote = state.dailyQuote,
                            hasFilters = state.filterOptions.hasActiveFilters() || state.searchQuery.isNotEmpty(),
                            context = context
                        )
                    }

                    QuoteTab.FAVORITES -> {
                        renderQuotesTab(
                            viewModel = quoteViewModel,
                            quotes = state.filteredQuotes.filter { it.isFavorite },
                            searchQuery = state.searchQuery,
                            dailyQuote = null,
                            hasFilters = false,
                            context = context
                        )
                    }

                    QuoteTab.AUTHORS -> {
                        renderAuthorsTab(
                            viewModel = quoteViewModel,
                            authors = state.availableAuthors,
                            selectedAuthors = state.filterOptions.authors,
                            onSelectTab = { selectedTab = it }
                        )
                    }

                    QuoteTab.TOPICS -> {
                        renderTopicsTab(
                            viewModel = quoteViewModel,
                            tags = state.availableTags,
                            selectedTags = state.filterOptions.tags,
                            onSelectTab = { selectedTab = it }
                        )
                    }
                }
            }
        }

        // Floating glass header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(24.adp)
        ) {
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

            // Active filters row
            ActiveFiltersRow(
                filterOptions = state.filterOptions,
                onRemoveFilter = quoteViewModel::removeFilter,
                onClearAll = quoteViewModel::clearAllFilters,
                modifier = Modifier.padding(horizontal = 16.adp, vertical = 8.adp)
            )
            QuoteTabs(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }

        // Dialogs
        FilterDialog(
            visible = showFilterDialog,
            onDismiss = { showFilterDialog = false },
            onApplyFilters = quoteViewModel::applyFilters,
            currentFilters = state.filterOptions,
            authors = state.availableAuthors,
            tags = state.availableTags
        )

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

private fun LazyListScope.renderQuotesTab(
    viewModel: QuoteViewModel,
    quotes: List<Quote>,
    searchQuery: String,
    dailyQuote: Quote?,
    hasFilters: Boolean,
    context: android.content.Context
) {
    if (quotes.isEmpty()) {
        item {
            EmptyStateContent(
                message = context.getString(if (hasFilters) R.string.no_quotes_filtered else R.string.no_quotes_available),
                description = if (hasFilters) context.getString(R.string.try_adjusting_filters) else null
            )
        }
        return
    }

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
                onShare = { context.shareText(dailyQuote.quoteContent) },
                onCopy = { context.copyToClipboard(dailyQuote.quoteContent) },
                onTagClick = { viewModel.toggleTagFilter(dailyQuote.tags.first()) },
                showDailyHeader = true,
                useGradientBackground = true
            )
        }
    }

    itemsIndexed(
        items = quotes,
        key = { index, _ -> "quote_$index" }
    ) { index, quote ->
        QuoteCard(
            quote = quote,
            index = index + 1,
            searchQuery = searchQuery,
            onToggleFavorite = { viewModel.toggleFavorite(quote.quoteId) },
            onAuthorClick = { viewModel.toggleAuthorFilter(quote.authorName) },
            isFavorited = quote.isFavorite,
            onPlayAudio = { },
            onShare = { context.shareText(quote.quoteContent) },
            onCopy = { context.copyToClipboard(quote.quoteContent) },
            onTagClick = { viewModel.toggleTagFilter(quote.tags.first()) }
        )
    }
}

// Extension function to render authors in LazyListScope
private fun LazyListScope.renderAuthorsTab(
    viewModel: QuoteViewModel,
    authors: List<Author>,
    selectedAuthors: Set<String>,
    onSelectTab: (QuoteTab) -> Unit
) {
    if (authors.isEmpty()) {
        item {
            EmptyStateContent(
                message = stringResource(R.string.no_authors_found),
                description = stringResource(R.string.authors_coming_soon)
            )
        }
        return
    }

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
            onToggleSelection = { viewModel.toggleAuthorFilter(author.authorName) },
            onViewDetails = { viewModel.setSelectedAuthor(author) }
        )
    }
}

// Extension function to render topics in LazyListScope
private fun LazyListScope.renderTopicsTab(
    viewModel: QuoteViewModel,
    tags: List<Tag>,
    selectedTags: Set<String>,
    onSelectTab: (QuoteTab) -> Unit
) {
    if (tags.isEmpty()) {
        item {
            EmptyStateContent(
                message = stringResource(R.string.no_topics_found),
                description = stringResource(R.string.topics_coming_soon)
            )
        }
        return
    }

    item {
        TopicsGrid(
            tags = tags,
            selectedTags = selectedTags,
            onTagClick = { tagName ->
                viewModel.loadQuotesByTag(tagName)
                onSelectTab(QuoteTab.QUOTES)
            },
            onTagToggle = viewModel::toggleTagFilter
        )
    }
}

@Composable
private fun EmptyStateContent(
    message: String,
    description: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.adp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.SearchOff,
            contentDescription = stringResource(R.string.search_icon_content_desc),
            modifier = Modifier.size(64.adp),
            tint = Color(0xFF6B7280).copy(0.5f)
        )
        Spacer(modifier = Modifier.height(16.adp))
        Text(
            text = message,
            fontSize = 18.asp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6B7280),
            textAlign = TextAlign.Center
        )
        if (description != null) {
            Spacer(modifier = Modifier.height(8.adp))
            Text(
                text = description,
                fontSize = 14.asp,
                color = Color(0xFF9CA3AF),
                textAlign = TextAlign.Center,
                lineHeight = 20.asp
            )
        }
    }
}