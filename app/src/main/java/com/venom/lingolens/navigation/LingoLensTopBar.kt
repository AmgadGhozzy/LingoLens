package com.venom.lingolens.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.venom.resources.R
import com.venom.ui.components.bars.TopBar
import com.venom.ui.components.buttons.CustomButton
import com.venom.ui.navigation.Screen

@Composable
fun LingoLensTopBar(
    currentScreen: Screen,
    onNavigateBack: () -> Unit,
    onBookmarkClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    TopBar(
        title = stringResource(
            when (currentScreen) {
                is Screen.Quiz -> R.string.quiz_title
                Screen.Ocr -> R.string.ocr_title
                else -> R.string.app_name
            }
        ),
        leadingIcon = when {
            currentScreen == Screen.Ocr -> Icons.Rounded.ArrowBackIosNew
            else -> R.drawable.icon_menu
        },
        onLeadingIconClick = when {
            currentScreen == Screen.Ocr -> onNavigateBack
            else -> onSettingsClick
        },
        actions = {
            TopBarActions(onBookmarkClick, onSettingsClick)
        }
    )
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
