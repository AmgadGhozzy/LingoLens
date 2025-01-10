package com.venom.dialog.ui.component.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.venom.dialog.data.local.model.DialogMessage
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomFilledIconButton
import com.venom.ui.components.common.EmptyState

@Composable
fun ChatMessages(
    messages: List<DialogMessage>,
    listState: LazyListState,
    onPlayClick: (DialogMessage) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (messages.isNotEmpty()) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 200.dp, top = 48.dp)
                    .align(Alignment.BottomCenter),
                reverseLayout = true
            ) {
                items(messages) { message ->
                    MessageBubble(message = message, onPlayClick = { onPlayClick(message) })
                }
            }
        } else {
            EmptyState(
                icon = R.drawable.icon_dialog,
                title = stringResource(R.string.no_messages_found),
                subtitle = stringResource(R.string.start_conversation),
            )
        }

        if (messages.isNotEmpty()) {
            CustomFilledIconButton(
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.TopEnd),
                onClick = onClearHistory,
                icon = Icons.Filled.Clear,
                contentDescription = stringResource(R.string.action_clear_history),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
            )
        }
    }
}
