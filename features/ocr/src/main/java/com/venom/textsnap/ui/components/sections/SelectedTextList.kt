package com.venom.textsnap.ui.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.venom.textsnap.ui.components.SelectedTextItem
import com.venom.textsnap.ui.components.TextAction

@Composable
fun SelectedTextList(
    texts: List<String>, isSingleSelection: Boolean, onAction: (String, TextAction) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        items(texts.withIndex().toList(), key = { it.index }) { (_, text) ->
            SelectedTextItem(text = text,
                expanded = isSingleSelection,
                onAction = { action -> onAction(text, action) })
        }
    }
}
