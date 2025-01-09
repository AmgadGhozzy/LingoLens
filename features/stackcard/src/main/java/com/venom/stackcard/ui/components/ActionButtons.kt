package com.venom.stackcard.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.stackcard.ui.viewmodel.CardItem
import com.venom.ui.components.buttons.BookmarkFilledButton
import com.venom.ui.components.buttons.CustomFilledIconButton

@Composable
fun ActionButtons(
    card: CardItem,
    onBookmark: () -> Unit,
    onSpeak: () -> Unit,
    onCopy: () -> Unit,
    onFlip: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomFilledIconButton(
                icon = R.drawable.icon_copy,
                contentDescription = "Copy ${card.englishEn}",
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                onClick = onCopy
            )

            CustomFilledIconButton(
                icon = Icons.Rounded.Refresh,
                contentDescription = "Flip card for ${card.englishEn}",
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f),
                    contentColor = MaterialTheme.colorScheme.onTertiary
                ),
                onClick = onFlip
            )

            CustomFilledIconButton(
                icon = R.drawable.icon_sound,
                contentDescription = "Speech-to-text for ${card.englishEn}",
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                onClick = onSpeak
            )
        }

        BookmarkFilledButton(
            isBookmarked = card.isBookmarked,
            onToggleBookmark = onBookmark,
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.TopEnd)
        ).also { Log.d("BookmarkFilledButton", "isBookmarked: ${card.isBookmarked}") }
    }
}
