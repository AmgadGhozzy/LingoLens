package com.venom.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import com.venom.data.remote.respnod.SentenceResponse
import com.venom.resources.R
import com.venom.ui.components.bars.ActionButtons
import com.venom.ui.components.buttons.CloseButton
import com.venom.ui.components.common.DynamicStyledText
import com.venom.ui.components.common.ExpandableTranslation
import com.venom.ui.components.common.adp
import com.venom.ui.components.common.asp
import com.venom.ui.components.other.GlassCard
import com.venom.ui.components.other.GlassThickness
import com.venom.ui.components.other.GradientGlassCard
import com.venom.ui.components.other.TextShimmer
import com.venom.ui.viewmodel.ExpandableCardViewModel
import com.venom.ui.viewmodel.SentenceViewModel
import com.venom.ui.viewmodel.TTSViewModel
import com.venom.utils.Extensions.copyToClipboard
import com.venom.utils.Extensions.shareText

private val SOURCES = listOf(
    "all" to "All Sources",
    "sentencedict" to "SentenceDict",
    "cambridge" to "Cambridge",
    "yourdictionary" to "YourDictionary",
    "collins" to "Collins",
    "merriam-webster" to "Merriam-Webster",
    "vocabulary" to "Vocabulary.com",
    "wordnik" to "Wordnik"
)

private val LIMITS = listOf(10, 20, 30, 50, 100, -1)

@Composable
fun SentenceScreen(
    sentenceViewModel: SentenceViewModel = hiltViewModel(LocalContext.current as ViewModelStoreOwner),
    word: String? = null,
    onNavigateBack: () -> Unit = {}
) {
    val uiState by sentenceViewModel.uiState.collectAsState()
    var searchText by remember { mutableStateOf(word ?: "") }
    var selectedLimit by remember { mutableIntStateOf(20) }
    var selectedSource by remember { mutableStateOf("all") }

    LaunchedEffect(word) {
        word?.let { sentenceViewModel.searchWord(it, limit = selectedLimit) }
    }

    Column(Modifier.fillMaxSize()) {
        TopBar(onNavigateBack)

        if (word != null) {
            SearchSection(
                searchText = searchText,
                onSearchTextChange = { searchText = it },
                selectedLimit = selectedLimit,
                onLimitChange = { selectedLimit = it },
                selectedSource = selectedSource,
                onSourceChange = { selectedSource = it },
                onSearch = {
                    sentenceViewModel.searchWord(searchText, selectedLimit, selectedSource)
                },
                cacheInfo = uiState.cacheInfo,
                onGetCacheInfo = sentenceViewModel::getCacheInfo,
                onClearCache = sentenceViewModel::clearCache
            )
        }

        when {
            uiState.isLoading -> ShimmerList()
            uiState.sentenceResponse != null -> SentenceContent(uiState.sentenceResponse!!)
            else -> WelcomeContent()
        }
    }
}

@Composable
private fun TopBar(onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.adp, start = 20.adp, end = 20.adp, bottom = 8.adp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                stringResource(R.string.sentence_explorer_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                stringResource(R.string.sentence_explorer_description),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
            )
        }
        CloseButton(
            onClick = onNavigateBack,
            contentDescription = "Close",
            size = 44.adp
        )
    }
}

@Composable
private fun SearchSection(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    selectedLimit: Int,
    onLimitChange: (Int) -> Unit,
    selectedSource: String,
    onSourceChange: (String) -> Unit,
    onSearch: () -> Unit,
    cacheInfo: Int,
    onGetCacheInfo: () -> Unit,
    onClearCache: () -> Unit
) {
    var settingsExpanded by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    val settingsRotation by animateFloatAsState(
        if (settingsExpanded) 180f else 0f,
        spring(Spring.DampingRatioMediumBouncy)
    )

    if (showClearDialog) {
        ClearCacheDialog(cacheInfo, onClearCache) { showClearDialog = false }
    }

    GlassCard(
        shape = RoundedCornerShape(24.adp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.adp)
            .animateContentSize(spring(Spring.DampingRatioMediumBouncy))
    ) {
        Column(Modifier.padding(16.adp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = onSearchTextChange,
                    placeholder = {
                        Text(
                            stringResource(R.string.search_for_words),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary)
                    },
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            IconButton(onClick = { onSearchTextChange("") }) {
                                Icon(
                                    Icons.Default.Clear, null,
                                    tint = MaterialTheme.colorScheme.onSurface.copy(0.5f)
                                )
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(20.adp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary.copy(0.1f),
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(0.1f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer.copy(0.2f)
                    )
                )

                Spacer(Modifier.width(10.adp))

                IconButton(
                    onClick = {
                        settingsExpanded = !settingsExpanded
                        if (settingsExpanded) onGetCacheInfo()
                    },
                    modifier = Modifier
                        .size(52.adp)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(0.6f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.Settings, "Settings",
                        modifier = Modifier.rotate(settingsRotation),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(Modifier.width(10.adp))

                FloatingActionButton(
                    onClick = onSearch,
                    modifier = Modifier.size(52.adp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ) {
                    Icon(
                        Icons.AutoMirrored.Rounded.Send, "Search",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            AnimatedVisibility(
                visible = settingsExpanded,
                enter = expandVertically(spring(Spring.DampingRatioMediumBouncy)) + fadeIn(),
                exit = shrinkVertically(spring(Spring.DampingRatioMediumBouncy)) + fadeOut()
            ) {
                Column(Modifier.padding(top = 20.adp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(0.2f))

                    Spacer(Modifier.height(12.adp))

                    ChipSelector(
                        title = "Source",
                        icon = Icons.AutoMirrored.Rounded.MenuBook,
                        items = SOURCES.map { it.first },
                        labels = SOURCES.map { it.second },
                        selected = selectedSource,
                        onSelected = onSourceChange
                    )

                    Spacer(Modifier.height(16.adp))

                    ChipSelector(
                        title = "Result Limit",
                        icon = Icons.Default.FormatListNumbered,
                        items = LIMITS.map { it.toString() },
                        labels = LIMITS.map { if (it == -1) "All" else it.toString() },
                        selected = selectedLimit.toString(),
                        onSelected = { onLimitChange(it.toInt()) }
                    )

                    Spacer(Modifier.height(20.adp))

                    CacheRow(cacheInfo) { showClearDialog = true }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipSelector(
    title: String,
    icon: ImageVector,
    items: List<String>,
    labels: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Column {
        SectionHeader(title, icon)

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.adp),
            verticalArrangement = Arrangement.spacedBy(8.adp)
        ) {
            items.forEachIndexed { i, value ->
                val isSelected = value == selected
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelected(value) },
                    label = {
                        Text(
                            labels[i],
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colors.primaryContainer,
                        selectedLabelColor = colors.onPrimaryContainer,
                        containerColor = colors.surfaceContainerHigh.copy(0.6f),
                        labelColor = colors.onSurface.copy(0.75f)
                    ),
                    border = null,
                    shape = RoundedCornerShape(12.adp),
                    modifier = Modifier.height(36.adp)
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 10.adp)
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.adp))
        Spacer(Modifier.width(10.adp))
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun CacheRow(cacheInfo: Int, onClearCache: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
                Icons.Default.Storage, null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.adp)
            )
            Spacer(Modifier.width(10.adp))
            Column {
                Text(
                    "Cache",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (cacheInfo > 0) {
                    Text(
                        "Cached entries: $cacheInfo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                    )
                }
            }
        }
        IconButton(
            onClick = onClearCache,
            modifier = Modifier
                .size(40.adp)
                .background(MaterialTheme.colorScheme.errorContainer.copy(0.3f), CircleShape)
        ) {
            Icon(
                Icons.Default.DeleteSweep, "Clear cache",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.adp)
            )
        }
    }
}

@Composable
private fun ClearCacheDialog(cacheInfo: Int, onClear: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Warning, null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.adp)
                )
                Spacer(Modifier.width(12.adp))
                Text("Clear Cache")
            }
        },
        text = {
            Column {
                Text("This will remove all cached sentence data. Continue?")
                Spacer(Modifier.height(12.adp))
                Surface(
                    shape = RoundedCornerShape(8.adp),
                    color = MaterialTheme.colorScheme.surfaceContainer.copy(0.5f)
                ) {
                    Text(
                        "Cached entries: $cacheInfo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.7f),
                        modifier = Modifier.padding(12.adp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onClear(); onDismiss() },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) { Text("Clear") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(24.adp)
    )
}

@Composable
private fun ShimmerList() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.adp),
        verticalArrangement = Arrangement.spacedBy(16.adp)
    ) {
        repeat(4) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.adp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(0.3f)
                )
            ) {
                TextShimmer(Modifier.padding(20.adp))
            }
        }
    }
}

@Composable
private fun WelcomeContent() {
    val transition = rememberInfiniteTransition("welcome")
    val bookRotation by transition.animateFloat(
        -5f, 5f,
        infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse)
    )
    val pulse by transition.animateFloat(
        0.95f, 1.05f,
        infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse)
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(140.adp)
                .graphicsLayer {
                    scaleX = pulse; scaleY = pulse; rotationZ = bookRotation
                }
                .background(
                    Brush.radialGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(0.25f),
                            MaterialTheme.colorScheme.primary.copy(0.1f),
                            Color.Transparent
                        )
                    ), CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.AutoMirrored.Rounded.MenuBook, null,
                modifier = Modifier.size(72.adp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(36.adp))

        Text(
            stringResource(R.string.sentence_explorer_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(12.adp))

        Text(
            stringResource(R.string.sentence_explorer_description1),
            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 26.asp),
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
        )
    }
}

@Composable
private fun SentenceContent(response: SentenceResponse) {
    val viewModel: ExpandableCardViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    LazyColumn(contentPadding = PaddingValues(bottom = 80.adp, top = 8.adp)) {
        item { WordHeader(response) }

        item {
            Text(
                stringResource(R.string.example_sentences, response.totalSentences),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.adp, vertical = 8.adp)
            )
        }

        itemsIndexed(response.sentences, key = { i, _ -> "s_$i" }) { index, sentence ->
            val cardId = "s_$index"
            val isExpanded = state.expandedCardId == cardId

            SentenceCard(
                sentence = sentence,
                index = index,
                word = response.word,
                isExpanded = isExpanded,
                onToggle = {
                    if (isExpanded) viewModel.collapse()
                    else viewModel.toggleCard(cardId, sentence)
                },
                translatedText = state.currentTranslation,
                isLoading = state.isLoading,
                error = state.error,
                onRetry = { viewModel.retry(sentence) }
            )
        }
    }
}

@Composable
private fun WordHeader(response: SentenceResponse) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.adp, vertical = 10.adp),
        thickness = GlassThickness.Thick,
        shape = RoundedCornerShape(20.adp)
    ) {
        Column(Modifier.padding(24.adp)) {
            Text(
                response.word.uppercase(),
                style = MaterialTheme.typography.headlineLarge.copy(letterSpacing = 2.asp),
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.adp))
            Text(
                stringResource(R.string.sentences_found, response.totalSentences),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
            )
        }
    }
}

@Composable
private fun SentenceCard(
    sentence: String,
    index: Int,
    word: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    translatedText: String,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit
) {
    val ttsViewModel: TTSViewModel = hiltViewModel(LocalContext.current as ViewModelStoreOwner)
    val ttsState by ttsViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val primary = MaterialTheme.colorScheme.primary

    val actionText =
        if (isExpanded && translatedText.isNotEmpty() && error == null) translatedText else sentence

    val highlighted = remember(sentence, word) {
        buildAnnotatedString {
            sentence.split(word, ignoreCase = true).forEachIndexed { i, part ->
                append(part)
                if (i < sentence.split(word, ignoreCase = true).size - 1) {
                    withStyle(SpanStyle(color = primary, fontWeight = FontWeight.Bold)) {
                        append(word)
                    }
                }
            }
        }
    }

    GradientGlassCard(
        onClick = onToggle,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow))
            .padding(horizontal = 20.adp, vertical = 24.adp),
        thickness = GlassThickness.UltraThick,
        gradientColors = listOf(
            if (index % 2 == 0) MaterialTheme.colorScheme.surfaceContainer
            else MaterialTheme.colorScheme.surfaceContainerLowest,
            MaterialTheme.colorScheme.primary
        ),
        contentPadding = 18.adp,
        gradientAlpha = 0.1f
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.adp),
                verticalAlignment = Alignment.Top
            ) {
                SentenceBadge(index + 1)
                DynamicStyledText(
                    text = highlighted,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                    modifier = Modifier.weight(1f)
                )
            }

            ExpandableTranslation(
                isExpanded = isExpanded,
                translatedText = translatedText,
                isLoading = isLoading,
                error = error,
                onRetry = onRetry
            )

            ActionButtons(
                text = actionText,
                onSpeak = ttsViewModel::toggle,
                onCopy = { context.copyToClipboard(actionText) },
                onShare = { context.shareText(actionText) },
                isSpeaking = ttsState.isSpeaking
            )
        }
    }
}

@Composable
private fun SentenceBadge(number: Int) {
    Surface(
        shape = RoundedCornerShape(10.adp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(0.8f),
        modifier = Modifier.wrapContentSize()
    ) {
        Text(
            "$number",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 10.adp, vertical = 4.adp)
        )
    }
}