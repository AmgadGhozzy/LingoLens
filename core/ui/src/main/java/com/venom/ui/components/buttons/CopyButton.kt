package com.venom.ui.components.buttons

import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.components.dialogs.CopiedToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CopyButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    iconSize: Dp = 24.dp,
    modifier: Modifier = Modifier
) {
    var showCopiedToast by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    IconButton(
        onClick = {
            onClick()
            showCopiedToast = true
            scope.launch {
                delay(2000)
                showCopiedToast = false
            }
        },
        enabled = enabled,
        modifier = modifier.size(56.dp)
    ) {
        Icon(
            modifier = Modifier.size(iconSize),
            painter = painterResource(id = R.drawable.icon_copy),
            contentDescription = stringResource(R.string.action_copy),
            tint = MaterialTheme.colorScheme.primary
        )
        CopiedToast(visible = showCopiedToast)
    }
}