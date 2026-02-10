package com.venom.ui.screen.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.venom.resources.R
import com.venom.ui.components.buttons.BookmarkFilledButton
import com.venom.ui.components.buttons.CustomFilledIconButton
import com.venom.ui.components.common.adp
import com.venom.utils.Extensions.formatTimestamp

@Composable
fun HistoryItemHeader(
    timestamp: Long,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onOpenInNew: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(12.adp)
        ) {
            Text(
                text = timestamp.formatTimestamp(),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 12.adp, vertical = 6.adp)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            CustomFilledIconButton(
                icon = Icons.AutoMirrored.Rounded.OpenInNew,
                contentDescription = stringResource(R.string.open_in_new),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(0.3f),
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                onClick = onOpenInNew
            )
            Spacer(Modifier.width(4.adp))
            BookmarkFilledButton(
                isBookmarked = isBookmarked,
                onToggleBookmark = onToggleBookmark
            )
        }
    }
}
