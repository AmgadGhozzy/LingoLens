package com.venom.textsnap.ui.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.textsnap.ui.components.SelectedTextItem
import com.venom.ui.components.common.ActionItem

@Composable
fun SelectedTextList(
    texts: List<String>,
    isSingleSelection: Boolean,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    onSpeak: (String) -> Unit,
    onTranslate: (String) -> Unit
) {
    if (texts.isEmpty()) return

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(texts) { text ->
            SelectedTextItem(
                text = text,
                expanded = isSingleSelection,
                actions = listOf(
                    ActionItem.Action(
                        icon = R.drawable.icon_copy,
                        description = R.string.action_copy,
                        onClick = { onCopy(text) }
                    ),
                    ActionItem.Action(
                        icon = R.drawable.icon_sound,
                        description = R.string.action_speak,
                        onClick = { onSpeak(text) }
                    ),
                    ActionItem.Action(
                        icon = R.drawable.icon_translate,
                        description = R.string.action_translate,
                        onClick = { onTranslate(text) }
                    ),
                    ActionItem.Action(
                        icon = R.drawable.icon_share,
                        description = R.string.action_share,
                        onClick = { onShare(text) }
                    )
                )
            )
        }
    }
}