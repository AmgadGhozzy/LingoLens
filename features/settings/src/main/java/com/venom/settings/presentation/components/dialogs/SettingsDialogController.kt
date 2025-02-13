package com.venom.settings.presentation.components.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.venom.domain.model.AppLanguage
import com.venom.domain.model.AppTheme
import com.venom.domain.model.FontStyles
import com.venom.domain.model.PaletteStyle
import com.venom.resources.R
import com.venom.settings.presentation.screen.AboutScreen
import com.venom.ui.viewmodel.SettingsDialog
import com.venom.ui.viewmodel.SettingsUiState

@Composable
fun SettingsDialogController(
    dialogType: SettingsDialog,
    uiState: SettingsUiState,
    onDismiss: () -> Unit,
    onUpdateLanguage: (AppLanguage) -> Unit,
    onUpdateNativeLanguage: (AppLanguage) -> Unit,
    onUpdateTheme: (AppTheme) -> Unit,
    onUpdateColorStyle: (PaletteStyle) -> Unit,
    onUpdatePrimaryColor: (Int) -> Unit,
    onUpdateFontFamily: (FontStyles) -> Unit
) {
    when (dialogType) {
        SettingsDialog.SelectTheme -> {
            BaseDialog(
                title = stringResource(id = R.string.app_theme),
                onDismiss = onDismiss
            ) {
                DialogScrollableContent {
                    AppTheme.entries.forEach { theme ->
                        DialogRadioOption(
                            text = stringResource(id = theme.title),
                            selected = theme == uiState.themePrefs.appTheme,
                            onClick = { onUpdateTheme(theme) }
                        )
                    }
                }
            }
        }

        SettingsDialog.SelectPaletteStyle -> {
            BaseDialog(
                title = stringResource(id = R.string.palette_style),
                onDismiss = onDismiss
            ) {
                DialogScrollableContent {
                    PaletteStyle.entries
                        .filter { it.title.isNotEmpty() }
                        .forEach { style ->
                            DialogRadioOption(
                                text = style.title,
                                selected = style == uiState.themePrefs.colorStyle,
                                onClick = { onUpdateColorStyle(style) }
                            )
                        }
                }
            }
        }

        SettingsDialog.SelectPrimaryColor -> {
            ColorPickerDialog(
                onColorSelected = { color ->
                    onUpdatePrimaryColor(color)
                    onDismiss()
                },
                initialColor = uiState.themePrefs.primaryColor,
                onDismiss = onDismiss
            )
        }

        SettingsDialog.SelectFont -> {
            BaseDialog(
                title = stringResource(id = R.string.font_type),
                onDismiss = onDismiss
            ) {
                DialogScrollableContent {
                    FontStyles.entries.forEach { style ->
                        DialogRadioOption(
                            text = style.title,
                            selected = style == uiState.themePrefs.fontFamily,
                            onClick = { onUpdateFontFamily(style) }
                        )
                    }
                }
            }
        }

        SettingsDialog.SelectAppLanguage -> {
            BaseDialog(
                title = stringResource(id = R.string.app_language),
                onDismiss = onDismiss
            ) {
                DialogScrollableContent {
                    AppLanguage.entries.forEach { language ->
                        DialogRadioOption(
                            text = stringResource(language.readableResId),
                            selected = language == uiState.appLanguage,
                            onClick = { onUpdateLanguage(language) }
                        )
                    }
                }
            }
        }

        SettingsDialog.SelectNativeLanguage -> {
            BaseDialog(
                title = stringResource(id = R.string.native_language),
                onDismiss = onDismiss
            ) {
                DialogScrollableContent {
                    AppLanguage.entries.forEach { language ->
                        DialogRadioOption(
                            text = stringResource(language.readableResId),
                            selected = language == uiState.appLanguage,
                            onClick = { onUpdateNativeLanguage(language) }
                        )
                    }
                }
            }
        }

        SettingsDialog.About -> {
            AboutScreen(onDismiss = onDismiss)
        }

        SettingsDialog.None -> {}
    }
}