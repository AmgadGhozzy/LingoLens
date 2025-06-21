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
import androidx.compose.ui.unit.dp
import com.venom.domain.model.DictionaryEntry
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

                translations.forEach { entry ->
                    TranslationEntryComponent(
                        entry = entry,
                        showAll = showAll,
                        onWordClick = onWordClick,
                        onSpeak = onSpeak,
                        toggleShowAll = { showAll = !showAll },
                        isAlpha = true
                    )
                }
            }

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
