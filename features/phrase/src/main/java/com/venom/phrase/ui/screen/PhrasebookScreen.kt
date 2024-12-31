package com.venom.phrase.ui.screen

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.FlightTakeoff
import androidx.compose.material.icons.rounded.Hotel
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.phrase.data.model.Category
import com.venom.phrase.data.model.Phrase
import com.venom.phrase.ui.components.getCategoryIcon
import com.venom.phrase.ui.viewmodel.PhraseUiState
import com.venom.phrase.ui.viewmodel.PhraseViewModel
import com.venom.ui.components.bars.LanguageBar
import com.venom.ui.components.inputs.CustomSearchBar
import com.venom.ui.viewmodel.LangSelectorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhrasebookScreen(
    viewModel: PhraseViewModel = hiltViewModel(),
    langSelectorViewModel: LangSelectorViewModel = hiltViewModel(),
    onNavigateToCategory: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val langState by langSelectorViewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(langState.sourceLang, langState.targetLang) {
        viewModel.updateLanguages(langState.sourceLang, langState.targetLang)
    }

    PhrasebookContent(
        state = state,
        searchQuery = searchQuery,
        onSearchQueryChange = { searchQuery = it },
        langSelectorViewModel = langSelectorViewModel,
        onNavigateToCategory = onNavigateToCategory,
        scrollBehavior = scrollBehavior
    )
}




