package com.venom.ui.screen.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.venom.domain.model.IHistoryEntry
import com.venom.ui.components.common.adp
import com.venom.ui.components.other.GlassCard

@Composable
fun HistoryItemView(
    entry: IHistoryEntry,
    onEntryRemove: () -> Unit,
    onToggleBookmark: () -> Unit,
    onShareClick: () -> Unit,
    onCopyClick: () -> Unit,
    onOpenInNew: () -> Unit,
    isExpanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (Boolean, () -> Unit) -> Unit
) {
    val toggle = { onExpandChange(!isExpanded) }

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.adp, vertical = 8.adp),
        onClick = toggle
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.adp)
                .animateContentSize(spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow))
        ) {
            HistoryItemHeader(
                timestamp = entry.timestamp,
                isBookmarked = entry.isBookmarked,
                onToggleBookmark = onToggleBookmark,
                onOpenInNew = onOpenInNew
            )
            Spacer(Modifier.height(16.adp))
            content(isExpanded, toggle)
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(tween(300)) + expandVertically(tween(300)),
                exit = fadeOut(tween(200)) + shrinkVertically(tween(200))
            ) {
                Column {
                    Spacer(Modifier.height(20.adp))
                    BookMarkActionBar(onShareClick, onCopyClick, onEntryRemove)
                }
            }
        }
    }
}