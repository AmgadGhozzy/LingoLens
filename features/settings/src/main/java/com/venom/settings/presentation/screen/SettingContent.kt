package com.venom.settings.presentation.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Animation
import androidx.compose.material.icons.rounded.Autorenew
import androidx.compose.material.icons.rounded.BlurCircular
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.DocumentScanner
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Waves
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.venom.data.BuildConfig
import com.venom.domain.model.SettingsDialog
import com.venom.resources.R
import com.venom.settings.presentation.components.SettingsCard
import com.venom.settings.presentation.components.SettingsIcon
import com.venom.settings.presentation.components.SettingsItem
import com.venom.settings.presentation.components.SettingsSliderItem
import com.venom.settings.presentation.components.SettingsSwitchItem
import com.venom.settings.presentation.components.UpdateBadge
import com.venom.settings.presentation.components.dialogs.SettingsDialogController
import com.venom.ui.components.common.SettingsScaffold
import com.venom.ui.navigation.Screen
import com.venom.ui.viewmodel.SettingsUiState
import com.venom.ui.viewmodel.SettingsViewModel
import com.venom.utils.PLAY_STORE
import com.venom.utils.openUrl

@Composable
fun SettingsContent(
    viewModel: SettingsViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsState()
    var dialogType by remember { mutableStateOf(SettingsDialog.None) }

    SettingsScaffold(onDismiss = onDismiss) {
        item { DisplaySettings(viewModel, uiState, onDialogTypeChange = { dialogType = it }) }
        item { AnimationSettings(viewModel, uiState) }
        item { LanguageSettings(uiState, onDialogTypeChange = { dialogType = it }) }
        item { SpeechSettings(uiState, viewModel) }
        item { AboutSettings({ dialogType = it }, navController, onDismiss) }
    }

    SettingsDialogController(
        dialogType = dialogType,
        uiState = uiState,
        onDismiss = { dialogType = SettingsDialog.None },
        onUpdateLanguage = viewModel::setAppLanguage,
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
        )
//        SettingsItem(
//            title = R.string.primary_color,
//            subtitle = uiState.themePrefs.primaryColor.name,
//            leadingContent = { ColorPreviewIcon(uiState.themePrefs.primaryColor.color) },
//            onClick = { onDialogTypeChange(SettingsDialog.SelectPrimaryColor) }
//        )
//        SettingsItem(
//            title = R.string.palette_style,
//            subtitle = uiState.themePrefs.colorStyle.name,
//            leadingContent = { SettingsIcon(Icons.Rounded.Star) },
//            onClick = { onDialogTypeChange(SettingsDialog.SelectPaletteStyle) }
//        )
        SettingsItem(
            title = R.string.font_type,
            subtitle = uiState.themePrefs.fontFamily.name,
            leadingContent = { SettingsIcon(Icons.Rounded.DocumentScanner) },
            onClick = { onDialogTypeChange(SettingsDialog.SelectFont) },
            badgeText = stringResource(R.string.badge_new)
        )
        SettingsSwitchItem(
            title = R.string.amoled_black,
            subtitle = stringResource(R.string.amoled_black_desc),
            checked = uiState.themePrefs.isAmoledBlack,
            onCheckedChange = { viewModel.toggleAmoledBlack() }
        )
//        SettingsSwitchItem(
//            title = R.string.wallpaper_colors,
//            subtitle = stringResource(R.string.wallpaper_colors_desc),
//            checked = uiState.themePrefs.materialYou,
//            onCheckedChange = { viewModel.toggleWallpaperColor() }
//        )
    }
}

@Composable
private fun AnimationSettings(
    viewModel: SettingsViewModel,
    uiState: SettingsUiState
) {
    SettingsCard(title = R.string.animations) {
        SettingsSwitchItem(
            title = R.string.floating_orbs,
            subtitle = stringResource(R.string.floating_orbs_desc),
            leadingContent = { SettingsIcon(Icons.Rounded.BlurCircular) },
            checked = uiState.orbPrefs.enabled,
            onCheckedChange = { viewModel.toggleOrbs() }
        )

        if (uiState.orbPrefs.enabled) {
            SettingsSwitchItem(
                title = R.string.floating_animation,
                subtitle = stringResource(R.string.floating_animation_desc),
                leadingContent = { SettingsIcon(Icons.Rounded.Waves) },
                checked = uiState.orbPrefs.enableFloatingAnimation,
                onCheckedChange = { viewModel.toggleFloatingAnimation() }
            )
            SettingsSwitchItem(
                title = R.string.scale_animation,
                subtitle = stringResource(R.string.scale_animation_desc),
                leadingContent = { SettingsIcon(Icons.Rounded.Autorenew) },
                checked = uiState.orbPrefs.enableScaleAnimation,
                onCheckedChange = { viewModel.toggleScaleAnimation() }
            )
            SettingsSwitchItem(
                title = R.string.alpha_animation,
                subtitle = stringResource(R.string.alpha_animation_desc),
                leadingContent = { SettingsIcon(Icons.Rounded.Animation) },
                checked = uiState.orbPrefs.enableAlphaAnimation,
                onCheckedChange = { viewModel.toggleAlphaAnimation() }
            )
            SettingsSliderItem(
                title = R.string.animation_speed,
                icon = Icons.Rounded.Speed,
                value = uiState.orbPrefs.animationSpeed,
                onValueChange = viewModel::setAnimationSpeed,
                valueRange = 0.5f..2.0f,
                steps = 2,
                valueText = "${(uiState.orbPrefs.animationSpeed * 100).toInt()}%"
            )
        }
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
            subtitle = uiState.appLanguage.englishName,
            onClick = { onDialogTypeChange(SettingsDialog.SelectAppLanguage) }
        )
        SettingsItem(
            title = R.string.native_language,
            subtitle = uiState.nativeLanguage.englishName,
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
            checked = uiState.autoPronunciation,
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
    onDialogTypeChange: (SettingsDialog) -> Unit,
    navController: NavHostController,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    SettingsCard(title = R.string.about) {
        SettingsItem(
            title = R.string.about,
            leadingContent = { SettingsIcon(Icons.Rounded.Info) },
            onClick = { onDialogTypeChange(SettingsDialog.About) }
        )
        SettingsItem(
            title = R.string.rate_app,
            leadingContent = { SettingsIcon(Icons.Rounded.Star) },
            onClick = { context.openUrl(PLAY_STORE) }
        )
        SettingsItem(
            title = R.string.onboarding,
            leadingContent = { SettingsIcon(Icons.AutoMirrored.Rounded.MenuBook) },
            onClick = { navController.navigate(Screen.Onboarding.route); onDismiss() }
        )
        SettingsItem(
            title = R.string.app_version,
            subtitle = BuildConfig.APP_VERSION_NAME + BuildConfig.APP_VERSION_CODE,
            leadingContent = { SettingsIcon(Icons.Rounded.Info) },
            trailingContent = { UpdateBadge(hasUpdate = true) },
            onClick = {}
        )
    }
}