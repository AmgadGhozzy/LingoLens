package com.venom.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.venom.data.model.SentenceResponse
import com.venom.ui.components.bars.ActionButtons
import com.venom.ui.components.buttons.CustomFilledIconButton
import com.venom.ui.components.common.DynamicStyledText
import com.venom.ui.components.onboarding.FloatingOrbs
import com.venom.ui.components.other.TextShimmer
import com.venom.ui.viewmodel.SentenceCardViewModel
import com.venom.ui.viewmodel.SentenceViewModel
import com.venom.ui.viewmodel.TTSViewModel
import com.venom.utils.Extensions.copyToClipboard
import com.venom.utils.Extensions.shareText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SentenceScreen(
    word: String? = null,
    onNavigateBack: () -> Unit = {},
    viewModel: SentenceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchText by remember { mutableStateOf(word ?: "") }

    LaunchedEffect(word) {
        word?.let { viewModel.searchWord(it) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        FloatingOrbs(
            primaryColor = MaterialTheme.colorScheme.primary,
            secondaryColor = MaterialTheme.colorScheme.primaryContainer,
            enableAlphaAnimation = false
        )

        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(onNavigateBack = onNavigateBack)

            word?.let {
                SearchSection(
                    searchText = searchText,
                    onSearchTextChange = { searchText = it },
                    onSearch = { viewModel.searchWord(searchText) }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                when {
                    uiState.isLoading -> repeat(3) {
                        SentenceCardShimmer()
                    }

                    uiState.sentenceResponse != null -> SentenceContent(
                        response = uiState.sentenceResponse!!
                    )

                    else -> WelcomeContent()
                }
            }
        }
    }
}

@Composable
private fun TopBar(onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, start = 20.dp, end = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Sentence Explorer",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Light,
                letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.primary
        )

        CustomFilledIconButton(
            icon = Icons.Rounded.Close,
            onClick = onNavigateBack,
            contentDescription = "Close",
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            size = 40.dp
        )
    }
}

@Composable
private fun SearchSection(
    searchText: String, onSearchTextChange: (String) -> Unit, onSearch: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (searchText.isNotEmpty()) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .scale(scale),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                placeholder = { Text("Search for words...") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = { onSearchTextChange("") }) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            FloatingActionButton(
                onClick = onSearch,
                modifier = Modifier.size(48.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(
                    Icons.AutoMirrored.Rounded.Send,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun WelcomeContent() {
    val bookRotation by rememberInfiniteTransition().animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(modifier = Modifier
            .size(120.dp)
            .graphicsLayer { rotationZ = bookRotation }
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), Color.Transparent
                    )
                ), CircleShape
            ), contentAlignment = Alignment.Center) {
            Icon(
                Icons.AutoMirrored.Rounded.MenuBook,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            "Sentence Explorer",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold
            ), color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Discover beautiful sentences\nwith your favorite words",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Light,
                lineHeight = 24.sp
            ),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun SentenceContent(
    response: SentenceResponse
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item { WordCard(response) }

        item {
            Text(
                "Example Sentences",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ), modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        itemsIndexed(response.sentences) { index, sentence ->
            SentenceCard(
                sentence = sentence, index = index, word = response.word
            )
        }
    }
}

@Composable
private fun WordCard(response: SentenceResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f))
                .padding(24.dp)
        ) {
            Text(
                response.word.uppercase(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "${response.totalSentences} sentences found",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun SentenceCard(
    sentence: String, index: Int, word: String
) {
    val viewModel: SentenceCardViewModel = hiltViewModel(key = sentence.hashCode().toString())
    val ttsViewModel: TTSViewModel = hiltViewModel()
    val ttsState by ttsViewModel.uiState.collectAsState()
    val cardState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val highlightedText = remember(sentence, word) {
        buildAnnotatedString {
            val parts = sentence.split(word, ignoreCase = true)
            parts.forEachIndexed { i, part ->
                append(part)
                if (i < parts.size - 1) {
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xFF0098EA),
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(word)
                    }
                }
            }
        }
    }

    LaunchedEffect(cardState.isExpanded) {
        if (cardState.isExpanded && cardState.translatedText.isEmpty() && !cardState.isLoading) {
            viewModel.translate(sentence)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)),
        shape = RoundedCornerShape(16.dp),
        onClick = { viewModel.updateExpanded(!cardState.isExpanded) },
        colors = CardDefaults.cardColors(
            containerColor = if (index % 2 == 0) MaterialTheme.colorScheme.surfaceContainer
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                SentenceBadge(number = index + 1)
                DynamicStyledText(
                    text = highlightedText,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = if (cardState.isExpanded) Int.MAX_VALUE else 2,
                    modifier = Modifier.weight(1f)
                )
            }

            AnimatedVisibility(
                visible = cardState.isExpanded, enter = fadeIn() + expandVertically(
                    expandFrom = Alignment.Top, animationSpec = tween(durationMillis = 300)
                ), exit = fadeOut() + shrinkVertically(
                    shrinkTowards = Alignment.Top, animationSpec = tween(durationMillis = 200)
                )
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    when {
                        cardState.isLoading -> {
                            Spacer(modifier = Modifier.height(16.dp))
                            TextShimmer()
                        }

                        cardState.translatedText.isNotEmpty() -> {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 16.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f)
                            )
                            DynamicStyledText(
                                text = cardState.translatedText, modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    ActionButtons(
                        text = sentence,
                        onSpeak = ttsViewModel::speak,
                        onCopy = { context.copyToClipboard(sentence) },
                        onShare = { context.shareText(sentence) },
                        isSpeaking = ttsState.isSpeaking
                    )
                }
            }
        }
    }
}

@Composable
private fun SentenceBadge(number: Int) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier
            .padding(top = 11.dp)
            .wrapContentSize()
    ) {
        Text(
            text = "$number:",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            ),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Preview
@Composable
private fun SentenceCardShimmer() {
    Column(
        modifier = Modifier
            .background(Color.Transparent)
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(3) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.2f)
                )
            ) {
                TextShimmer(Modifier.padding(16.dp))
            }
        }
    }
}