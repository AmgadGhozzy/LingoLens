package com.venom.stackcard.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun UndoButton(
    removedCardsCount: Int, onUndo: () -> Unit, modifier: Modifier = Modifier
) {
    FloatingActionButton(onClick = onUndo, modifier = modifier.padding(end = 16.dp, bottom = 42.dp).semantics {
        contentDescription = "Undo last action. $removedCardsCount cards available"
    }) {
        BadgedBox(badge = {
            if (removedCardsCount > 1) {
                Badge { Text(removedCardsCount.toString()) }
            }
        }) {
            Icon(
                Icons.AutoMirrored.Rounded.Undo, contentDescription = null
            )
        }
    }
}
