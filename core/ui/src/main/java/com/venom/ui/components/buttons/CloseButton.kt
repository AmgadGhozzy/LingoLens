package com.venom.ui.components.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.venom.resources.R
import com.venom.ui.components.common.adp

/**
 * Standard close button with consistent styling across the app
 *
 * @param onClick Callback invoked when button is clicked
 * @param modifier Modifier for the button
 * @param contentDescription Accessibility description (defaults to "Close")
 * @param size Size of the button (defaults to 38.adp)
 */
@Composable
fun CloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = stringResource(R.string.action_close),
    size: Dp = 38.adp
) {
    CustomFilledIconButton(
        icon = Icons.Rounded.Close,
        modifier = modifier,
        onClick = onClick,
        contentDescription = contentDescription,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.4f),
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        size = size
    )
}
