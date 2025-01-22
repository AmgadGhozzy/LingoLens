package com.venom.phrase.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.components.buttons.CopyButton
import com.venom.ui.components.buttons.CustomButton

@Composable
fun ActionButtons(
    onCopyClick: () -> Unit, onShareClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        CopyButton(onClick = onCopyClick, iconSize = 28.dp)

        CustomButton(
            icon = R.drawable.icon_share,
            onClick = onShareClick,
            contentDescription = stringResource(R.string.action_share)
        )
    }
}
