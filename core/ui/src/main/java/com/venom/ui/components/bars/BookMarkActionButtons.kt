package com.venom.ui.components.bars

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomButton
import com.venom.ui.components.dialogs.CopiedToast

@Composable
fun BookMarkActionButtons(
    onShareClick: () -> Unit,
    onCopyClick: () -> Unit,
    onDeleteClick: () -> Unit,
    showCopiedToast: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(contentAlignment = Alignment.Center) {
            CustomButton(
                modifier = Modifier.size(58.dp),
                icon = R.drawable.icon_copy,
                onClick = onCopyClick,
                contentDescription = stringResource(R.string.action_copy)
            )
            CopiedToast(visible = showCopiedToast)
        }

        CustomButton(
            icon = R.drawable.icon_share,
            onClick = onShareClick,
            contentDescription = stringResource(R.string.action_share)
        )

        CustomButton(
            icon = R.drawable.icon_delete_bin,
            onClick = onDeleteClick,
            selected = true,
            selectedTint = MaterialTheme.colorScheme.error,
            contentDescription = stringResource(R.string.action_delete)
        )
    }
}

@Preview
@Composable
fun BookMarkActionButtonsPreview() {
    BookMarkActionButtons(onShareClick = {}, onCopyClick = {}, onDeleteClick = {})
}