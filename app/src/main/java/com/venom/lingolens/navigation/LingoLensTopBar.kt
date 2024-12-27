package com.venom.lingolens.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.venom.resources.R
import com.venom.ui.components.bars.TopBar
import com.venom.ui.components.buttons.CustomButton

@Composable
fun LingoLensTopBar(
    navController: NavHostController, onBookmarkClick: () -> Unit
) {
    TopBar(title = stringResource(id = R.string.app_name), onNavigationClick = {
        navController.navigateToStart(Screen.Translation)
    }, actions = {
        TopBarActions(onBookmarkClick)
    })
}

@Composable
private fun TopBarActions(onBookmarkClick: () -> Unit) {
    CustomButton(
        icon = R.drawable.icon_bookmark_filled,
        contentDescription = stringResource(R.string.bookmarks_title),
        onClick = onBookmarkClick
    )
    CustomButton(icon = R.drawable.icon_settings,
        contentDescription = stringResource(R.string.settings_title),
        onClick = {})
}
