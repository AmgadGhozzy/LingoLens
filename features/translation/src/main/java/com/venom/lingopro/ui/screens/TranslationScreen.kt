package com.venom.lingopro.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.venom.lingopro.R
import com.venom.lingopro.domain.model.LanguageItem
import com.venom.lingopro.ui.components.bars.TopBar
import com.venom.lingopro.ui.components.buttons.CustomIcon
import com.venom.lingopro.ui.components.dialogs.CustomCard
import com.venom.lingopro.ui.components.dialogs.DraggableDialog
import com.venom.lingopro.ui.components.dialogs.FullscreenTextDialog
import com.venom.lingopro.ui.components.sections.ThesaurusView
import com.venom.lingopro.ui.components.sections.TranslationSection
import com.venom.lingopro.ui.viewmodel.TTSViewModel
import com.venom.lingopro.ui.viewmodel.TranslateViewModel
import com.venom.lingopro.utils.Constants.LANGUAGES_LIST
import com.venom.lingopro.utils.Extensions.copyToClipboard
import com.venom.lingopro.utils.Extensions.pasteFromClipboard
import com.venom.lingopro.utils.Extensions.shareText

@Composable
fun TranslationScreen(
    viewModel: TranslateViewModel = hiltViewModel(),
    ttsViewModel: TTSViewModel = hiltViewModel(),
    initialText: String? = null,
    isDialog: Boolean = false,
    onNavigateToBookmarks: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val ttsState by ttsViewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var sourceLang by remember { mutableStateOf(LANGUAGES_LIST[0]) }
    var targetLang by remember { mutableStateOf(LANGUAGES_LIST[1]) }

    var sourceText by remember { mutableStateOf(initialText ?: state.sourceText) }


    LaunchedEffect(initialText, isDialog) {
        if (isDialog && !initialText.isNullOrBlank()) {
            viewModel.onSourceTextChanged(initialText)
        }
    }

    val onBookmark: () -> Unit = { onNavigateToBookmarks() }
    val onHistory: () -> Unit = { onNavigateToHistory() }

    val languageSelectAction: (Boolean, LanguageItem) -> Unit = { isSourceLang, selectedLang ->
        if (isSourceLang) {
            sourceLang = selectedLang
            viewModel.updateLanguages(sourceLang, targetLang)
        } else {
            targetLang = selectedLang
            viewModel.updateLanguages(sourceLang, targetLang)
        }
        viewModel.onSourceTextChanged(sourceText)
    }

    val languageSwapAction = {
        val temp = sourceLang
        sourceLang = targetLang
        targetLang = temp
        viewModel.updateLanguages(sourceLang, targetLang)
    }

    val textChangeAction: (TextFieldValue) -> Unit = { text ->
        sourceText = text.text
        viewModel.onSourceTextChanged(sourceText)
    }

    val clearTextAction = {
        viewModel.clearText()
        sourceText = ""
    }

    // Actions for copying, sharing, and speaking
    val copyAction: (String) -> Unit = { text -> context.copyToClipboard(text) }
    val shareAction: (String) -> Unit = { text -> (context as Activity).shareText(text) }
    val speakAction: (String) -> Unit = { text -> ttsViewModel.speak(text) }
    val onPasteAction: () -> Unit = {
        val pastedText = context.pasteFromClipboard()
        pastedText?.let {
            sourceText = it
            viewModel.onSourceTextChanged(it)
        }
    }

    // Content composable work for both dialog and fullScreen modes
    @Composable
    fun TranslationContent(modifier: Modifier = Modifier) {
        Column(modifier = modifier) {
            TranslationSection(
                sourceLang = sourceLang,
                targetLang = targetLang,
                sourceTextValue = TextFieldValue(sourceText),
                translatedTextValue = TextFieldValue(state.translatedText),
                onTextChange = textChangeAction,
                onLanguageSelect = languageSelectAction,
                onSwapLanguages = languageSwapAction,
                onClearText = clearTextAction,
                isLoading = state.isLoading,

                onCopy = copyAction,
                onShare = shareAction,
                onSpeak = speakAction,
                onPaste = onPasteAction,
                onFullscreen = {
                    @Composable {
                        FullscreenTextDialog(
                            textValue = TextFieldValue(state.translatedText),
                            onDismiss = onDismiss,
                            onCopy = copyAction,
                            onShare = shareAction,
                            onSpeak = speakAction
                        )
                    }
                }
            )

            // Only show thesaurus in fullScreen mode
            if (!isDialog && state.synonyms.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                CustomCard() {
                    ThesaurusView(synonyms = state.synonyms, onWordClick = { selectedWord ->
                        sourceText = selectedWord
                        viewModel.onSourceTextChanged(selectedWord)
                    })
                }
            }
        }
    }


    if (isDialog) {
        DraggableDialog(onDismissRequest = onDismiss) {
            TranslationContent()
        }
    } else {
        Scaffold(
            topBar = {
                TopBar(title = stringResource(R.string.app_name),
                    onNavigationClick = onDismiss,
                    actions = {
                        CustomIcon(
                            icon = R.drawable.icon_history,
                            contentDescription = stringResource(R.string.history_title),
                            onClick = onHistory
                        )
                        CustomIcon(
                            icon = R.drawable.icon_bookmark_filled,
                            contentDescription = stringResource(R.string.bookmarks_title),
                            onClick = onBookmark
                        )
                    })
            }, containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            TranslationContent(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(8.dp)
                    .fillMaxSize()
            )
        }
    }
}