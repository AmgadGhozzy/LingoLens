package com.venom.ui.components.bars

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.components.buttons.CopyButton
import com.venom.ui.components.buttons.CustomButton

@Composable
fun BookMarkActionBar(
    onShareClick: () -> Unit,
    onCopyClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CopyButton(onClick = onCopyClick)

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
fun BookMarkActionBarsPreview() {
    BookMarkActionBar(onShareClick = {}, onCopyClick = {}, onDeleteClick = {})
}
