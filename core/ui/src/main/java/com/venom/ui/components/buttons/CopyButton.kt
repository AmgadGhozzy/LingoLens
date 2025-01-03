package com.venom.ui.components.buttons

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.components.dialogs.CopiedToast

@Composable
fun CopyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showCopiedToast: Boolean = false
) {
    IconButton(
        onClick = onClick,enabled = enabled, modifier = modifier.size(56.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.icon_copy),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        CopiedToast(visible = showCopiedToast)
    }
}
