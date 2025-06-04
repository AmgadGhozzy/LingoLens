package com.venom.ui.screen.dictionary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.data.model.DictionaryEntry
import com.venom.data.model.DictionaryTerm
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomButton
import com.venom.ui.components.dialogs.CustomCard

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TranslationsCard(
    modifier: Modifier = Modifier,
    translations: List<DictionaryEntry>,
    onWordClick: (String) -> Unit,
    onSpeak: (String) -> Unit,
    onExpand: (() -> Unit)? = null,
) {
    var showAll by remember { mutableStateOf(false) }

    CustomCard {
        Box(modifier = Modifier.padding(12.dp)) {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Section header with icon and title
                SectionHeader(
                    title = stringResource(id = R.string.translations),
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_translate),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )

                // Iterate over translations and display their content
                translations.forEach { entry ->
                    TranslationEntry(
                        entry = entry,
                        showAll = showAll,
                        onWordClick = onWordClick,
                        onSpeak = onSpeak,
                        toggleShowAll = { showAll = !showAll },
                        isAlpha = true
                    )
                }
            }

            // Expand button in the top-right corner, if applicable
            onExpand?.let {
                CustomButton(
                    icon = R.drawable.icon_fullscreen,
                    onClick = it,
                    contentDescription = stringResource(R.string.action_expand),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 8.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun TranslationsCardPreview() {
    val translations = listOf(
        DictionaryEntry(
            pos = "noun",
            terms = listOf("apple"),
            entry = listOf(
                DictionaryTerm(
                    word = "apple",
                    reverseTranslation = listOf("fruit"),
                    score = 1.0
                )
            ),
            baseForm = "apple"
        )
    )

    TranslationsCard(
        translations = translations,
        onWordClick = { },
        onSpeak = { },
        onExpand = { },
        modifier = Modifier
    )
}