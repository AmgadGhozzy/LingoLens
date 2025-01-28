package com.venom.lingolens.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.venom.resources.R
import com.venom.ui.components.bars.TopBar
import com.venom.ui.components.buttons.CustomButton

@Composable
fun LingoLensTopBar(
    onBookmarkClick: () -> Unit, onSettingsClick: () -> Unit
) {
    TopBar(title = stringResource(id = R.string.app_name),
        leadingIcon = R.drawable.icon_menu,
        onLeadingIconClick = { onSettingsClick },
        actions = {
            TopBarActions(onBookmarkClick, onSettingsClick)
        })
}

@Composable
fun TopBarActions(onBookmarkClick: () -> Unit, onSettingsClick: () -> Unit) {
    CustomButton(
        icon = R.drawable.icon_bookmark_filled,
        contentDescription = stringResource(R.string.bookmarks_title),
        onClick = onBookmarkClick
    )
    CustomButton(
        icon = R.drawable.icon_settings,
        contentDescription = stringResource(R.string.settings_title),
        onClick = onSettingsClick
    )
}
