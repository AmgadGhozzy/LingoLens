package com.venom.ui.components.bars

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.venom.resources.R

/**
 * A top app bar with a title and optional navigation or menu icon and actions.
 *
 * @param title The title of the top app bar
 * @param modifier The modifier to apply to the top app bar
 * @param leadingIcon The icon to show on the left side (navigation or menu)
 * @param onLeadingIconClick The callback to call when the leading icon is clicked
 * @param actions The actions to show on the top app bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    modifier: Modifier = Modifier,
    leadingIcon: Any = Icons.Rounded.ArrowBackIosNew,
    onLeadingIconClick: () -> Unit,
    actions: @Composable (RowScope.() -> Unit) = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
        },
        navigationIcon = {
            NavigationIcon(leadingIcon, onLeadingIconClick)
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            navigationIconContentColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(82.dp)
            .wrapContentHeight(align = Alignment.CenterVertically)
    )
}

@Composable
private fun NavigationIcon(
    leadingIcon: Any,
    onLeadingIconClick: () -> Unit
) {
    IconButton(onClick = onLeadingIconClick) {
        when (leadingIcon) {
            is Int -> Icon(
                painter = painterResource(id = leadingIcon),
                contentDescription = getIconContentDescription(leadingIcon)
            )

            is ImageVector -> Icon(
                imageVector = leadingIcon,
                contentDescription = getIconContentDescription(leadingIcon)
            )
        }
    }
}

@Composable
private fun getIconContentDescription(leadingIcon: Any): String {
    return when (leadingIcon) {
        R.drawable.icon_menu -> stringResource(id = R.string.action_menu)
        else -> stringResource(id = R.string.action_back)
    }
}