package com.venom.lingopro.ui.components.bars

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.venom.lingopro.R

/**
 * A top app bar with a title and optional navigation icon and actions.
 *
 * @param title The title of the top app bar
 * @param modifier The modifier to apply to the top app bar
 * @param navigationIcon The icon to use for navigation. If null, no icon is shown
 * @param onNavigationClick The callback to call when the navigation icon is clicked
 * @param actions The actions to show on the top app bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: ImageVector? = Icons.Rounded.ArrowBackIosNew,
    onNavigationClick: (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit) = {}
) {
    TopAppBar(title = {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }, navigationIcon = {
        navigationIcon?.let {
            IconButton(onClick = { onNavigationClick?.invoke() }) {
                Icon(
                    imageVector = it, contentDescription = stringResource(R.string.action_back)
                )
            }
        }
    }, actions = actions, colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        navigationIconContentColor = MaterialTheme.colorScheme.primary,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        actionIconContentColor = MaterialTheme.colorScheme.primary
    ), modifier = modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun TopBarPreview() {
    TopBar(title = "LingoPro")
}