package com.venom.phrase.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.venom.phrase.data.mapper.getTranslation
import com.venom.phrase.data.model.PhraseEntity
import com.venom.phrase.ui.viewmodel.PhraseUiState
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomButton
import com.venom.ui.components.common.EmptyState
import com.venom.ui.components.common.adp
import com.venom.ui.components.common.asp
import com.venom.ui.components.inputs.CustomSearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhraseScreenContent(
    state: PhraseUiState,
    onSearchQueryChanged: (String) -> Unit,
    onBookmarkClick: (PhraseEntity) -> Unit,
    isSpeakingText: (String) -> Boolean,
    onSpeakClick: (String, String) -> Unit,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    onDismiss: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    var isSearchExpanded by remember { mutableStateOf(false) }
    
    val title = state.selectedCategory?.getTranslation(state.sourceLang.code)
        ?: stringResource(R.string.bookmarks_title)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = Color.Transparent,
        topBar = {
            PremiumTopBar(
                title = title,
                searchQuery = state.searchQuery,
                onSearchQueryChanged = onSearchQueryChanged,
                onDismiss = onDismiss,
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Content
            AnimatedVisibility(
                visible = state.filteredSections.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                SectionWithPhrasesList(
                    sections = state.filteredSections,
                    sourceLang = state.sourceLang.code,
                    targetLang = state.targetLang.code,
                    onBookmarkClick = onBookmarkClick,
                    isSpeakingText = isSpeakingText,
                    onSpeakClick = onSpeakClick,
                    onCopy = onCopy,
                    onShare = onShare,
                    contentPadding = padding
                )
            }

            // Empty state with animation
            AnimatedVisibility(
                visible = state.filteredSections.isEmpty() && state.searchQuery.isNotBlank(),
                enter = fadeIn() + scaleIn(initialScale = 0.9f),
                exit = fadeOut() + scaleOut(targetScale = 0.9f),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                EmptyState(
                    modifier = Modifier.fillMaxSize(),
                    icon = R.drawable.icon_dialog,
                    title = stringResource(R.string.phrase_empty_state_title),
                    subtitle = stringResource(R.string.phrase_empty_state_subtitle)
                )
            }
        }
    }
}

/**
 * Premium top bar with glass effect and integrated search
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PremiumTopBar(
    title: String,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onDismiss: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.asp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        ),
        actions = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.adp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 8.adp)
            ) {
                // Adaptive search bar
                CustomSearchBar(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .padding(end = 4.adp),
                    searchQuery = searchQuery,
                    onSearchQueryChanged = onSearchQueryChanged
                )

                // Back button
                CustomButton(
                    onClick = onDismiss,
                    icon = R.drawable.icon_back,
                    iconSize = 28.adp,
                    contentDescription = stringResource(R.string.action_back),
                    showBorder = false
                )
            }
        }
    )
}
