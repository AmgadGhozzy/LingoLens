package com.venom.phrase.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.venom.resources.R

@Composable
fun ActionButtons() {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
    ) {
        ActionTextButton(
            onClick = { }, icon = Icons.Rounded.ContentCopy, text = stringResource(R.string.action_copy)
        )
        ActionTextButton(
            onClick = { }, icon = Icons.Rounded.Share, text = stringResource(R.string.action_share)
        )
    }
}
