package com.venom.textsnap.ui.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.venom.resources.R
import com.venom.textsnap.ui.components.SelectedTextItem
import com.venom.ui.components.common.ActionItem
import com.venom.ui.components.common.adp

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
        modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.adp)
    ) {
        items(texts) { text ->
            SelectedTextItem(
                text = text,
                expanded = isSingleSelection,
                actions = listOf(
                    ActionItem.Action(R.drawable.icon_copy, R.string.action_copy, { onCopy(text) }),
                    ActionItem.Action(R.drawable.icon_sound, R.string.action_speak, { onSpeak(text) }),
                    ActionItem.Action(R.drawable.icon_translate, R.string.action_translate, { onTranslate(text) }),
                    ActionItem.Action(R.drawable.icon_share, R.string.action_share, { onShare(text) })
                )
            )
        }
    }
}