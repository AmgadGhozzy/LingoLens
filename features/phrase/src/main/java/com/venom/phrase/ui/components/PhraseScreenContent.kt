package com.venom.phrase.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.venom.phrase.data.mapper.getTranslation
import com.venom.phrase.data.model.Phrase
import com.venom.phrase.ui.viewmodel.PhraseUiState
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomButton
import com.venom.ui.components.common.EmptyState
import com.venom.ui.components.inputs.CustomSearchBar
import com.venom.ui.components.other.FloatingOrbs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhraseScreenContent(
    state: PhraseUiState,
    onSearchQueryChanged: (String) -> Unit,
    onBookmarkClick: (Phrase) -> Unit,
    isSpeaking: Boolean,
    onSpeakClick: (String, String) -> Unit,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    onDismiss: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        TopAppBar(title = {
            Text(
                text = state.selectedCategory?.getTranslation(state.sourceLang.code) ?:
                stringResource(R.string.bookmarks_title),
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold
                )
            )
        }, scrollBehavior = scrollBehavior, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ), actions = {
            CustomSearchBar(
                modifier = Modifier.width(200.dp),
                searchQuery = state.searchQuery,
                onSearchQueryChanged = onSearchQueryChanged
            )

            CustomButton(
                onClick = onDismiss,
                icon = R.drawable.icon_back,
                iconSize = 32.dp,
                contentDescription = stringResource(R.string.action_back),
                modifier = Modifier.align(Alignment.CenterVertically),
                showBorder = false
            )
        })
    }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
        ) {
             FloatingOrbs(
            
        )
            if (state.filteredSections.isNotEmpty()) {
                SectionWithPhrasesList(
                    sections = state.filteredSections,
                    sourceLang = state.sourceLang.code,
                    targetLang = state.targetLang.code,
                    onBookmarkClick = onBookmarkClick,
                    isSpeaking = isSpeaking,
                    onSpeakClick = onSpeakClick,
                    onCopy = onCopy,
                    onShare = onShare,
                    contentPadding = padding
                )
            } else if (state.searchQuery.isNotBlank()) {
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
