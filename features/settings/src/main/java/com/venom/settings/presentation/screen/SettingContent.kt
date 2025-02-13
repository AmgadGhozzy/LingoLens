package com.venom.settings.presentation.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.resources.R
import com.venom.settings.presentation.components.*
import com.venom.settings.presentation.components.dialogs.SettingsDialogController
import com.venom.ui.viewmodel.*

@Composable
fun SettingsContent(
    viewModel: SettingsViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var dialogType by remember { mutableStateOf(SettingsDialog.None) }

    SettingsScaffold(onDismiss = onDismiss) {
        item { DisplaySettings(viewModel, uiState, onDialogTypeChange = { dialogType = it }) }
        item { LanguageSettings(uiState, onDialogTypeChange = { dialogType = it }) }
        item { SpeechSettings(uiState, viewModel) }
        item { AboutSettings(onDialogTypeChange = { dialogType = it }) }
    }

    SettingsDialogController(
        dialogType = dialogType,
        uiState = uiState,
        onDismiss = { dialogType = SettingsDialog.None },
        onUpdateLanguage = viewModel::updateLanguage,
        onUpdateNativeLanguage = viewModel::updateNativeLanguage,
        onUpdateTheme = viewModel::updateAppTheme,
        onUpdateColorStyle = viewModel::setColorStyle,
        onUpdatePrimaryColor = viewModel::setPrimaryColor,
        onUpdateFontFamily = viewModel::setFontFamily
    )
}

@Composable
private fun DisplaySettings(
    viewModel: SettingsViewModel,
    uiState: SettingsUiState,
    onDialogTypeChange: (SettingsDialog) -> Unit
) {
    SettingsCard(title = R.string.display) {
        SettingsItem(
            title = R.string.theme,
            subtitle = uiState.themePrefs.appTheme.name,
            leadingContent = { SettingsIcon(Icons.Rounded.DarkMode) },
            onClick = { onDialogTypeChange(SettingsDialog.SelectTheme) },
            badgeText = "New"
        )
        SettingsItem(
            title = R.string.palette_style,
            subtitle = uiState.themePrefs.colorStyle.name,
            leadingContent = { SettingsIcon(Icons.Rounded.Star) },
            onClick = { onDialogTypeChange(SettingsDialog.SelectPaletteStyle) }
        )
        SettingsItem(
            title = R.string.primary_color,
            subtitle = stringResource(R.string.primary_color_desc),
            leadingContent = { ColorPreviewIcon(uiState.themePrefs.primaryColor) },
            onClick = { onDialogTypeChange(SettingsDialog.SelectPrimaryColor) }
        )
        SettingsItem(
            title = R.string.font_type,
            subtitle = uiState.themePrefs.fontFamily.name,
            leadingContent = { SettingsIcon(Icons.Rounded.DocumentScanner) },
            onClick = { onDialogTypeChange(SettingsDialog.SelectFont) }
        )
        SettingsSwitchItem(
            title = R.string.amoled_black,
            subtitle = stringResource(R.string.amoled_black_desc),
            checked = uiState.themePrefs.isAmoledBlack,
            onCheckedChange = { viewModel.toggleAmoledBlack() }
        )
        SettingsSwitchItem(
            title = R.string.wallpaper_colors,
            subtitle = stringResource(R.string.wallpaper_colors_desc),
            checked = uiState.themePrefs.extractWallpaperColor,
            onCheckedChange = { viewModel.toggleWallpaperColor() }
        )
    }
}

@Composable
private fun LanguageSettings(
    uiState: SettingsUiState,
    onDialogTypeChange: (SettingsDialog) -> Unit
) {
    SettingsCard(title = R.string.language) {
        SettingsItem(
            title = R.string.app_language,
            subtitle = stringResource(uiState.appLanguage.readableResId),
            onClick = { onDialogTypeChange(SettingsDialog.SelectAppLanguage) }
        )
        SettingsItem(
            title = R.string.app_language,
            subtitle = stringResource(uiState.nativeLanguage.readableResId),
            onClick = { onDialogTypeChange(SettingsDialog.SelectNativeLanguage) }
        )
    }
}

@Composable
private fun SpeechSettings(
    uiState: SettingsUiState,
    viewModel: SettingsViewModel
) {
    SettingsCard(title = R.string.speech) {
        SettingsSwitchItem(
            title = R.string.auto_pronunciation,
            subtitle = stringResource(R.string.auto_pronunciation_desc),
            checked = uiState.autoPronounciation,
            onCheckedChange = viewModel::updateAutoPronounciation
        )
        SettingsSliderItem(
            title = R.string.speech_rate,
            icon = Icons.Rounded.Speed,
            value = uiState.speechRate,
            onValueChange = viewModel::updateSpeechRate,
            valueRange = 0.5f..2.5f,
            steps = 4,
            valueText = "${(uiState.speechRate * 100).toInt()}%"
        )
    }
}

@Composable
private fun AboutSettings(
    onDialogTypeChange: (SettingsDialog) -> Unit
) {
    SettingsCard(title = R.string.about) {
        SettingsItem(
            title = R.string.about,
            leadingContent = { SettingsIcon(Icons.Rounded.Info) },
            onClick = { onDialogTypeChange(SettingsDialog.About) }
        )
        SettingsItem(
            title = R.string.privacy_policy,
            leadingContent = { SettingsIcon(Icons.Rounded.Security) },
            onClick = {}
        )
        SettingsItem(
            title = R.string.rate_app,
            leadingContent = { SettingsIcon(Icons.Rounded.Star) },
            onClick = {}
        )
        SettingsItem(
            title = R.string.app_version,
            subtitle = "3.8.11",
            leadingContent = { SettingsIcon(Icons.Rounded.Info) },
            trailingContent = { UpdateBadge(hasUpdate = true) },
            onClick = {}
        )
    }
}
