package com.venom.settings.presentation.components.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.venom.data.model.LANGUAGES_LIST
import com.venom.data.model.LanguageItem
import com.venom.data.model.ThemeColor
import com.venom.domain.model.AppTheme
import com.venom.domain.model.FontStyles
import com.venom.domain.model.PaletteStyle
import com.venom.domain.model.SettingsDialog
import com.venom.resources.R
import com.venom.settings.presentation.screen.AboutScreen
import com.venom.ui.components.dialogs.BaseDialog
import com.venom.ui.components.dialogs.DialogRadioOption
import com.venom.ui.components.dialogs.DialogScrollableContent
import com.venom.ui.viewmodel.SettingsUiState

@Composable
fun SettingsDialogController(
    dialogType: SettingsDialog,
    uiState: SettingsUiState,
    onDismiss: () -> Unit,
    onUpdateLanguage: (LanguageItem) -> Unit,
    onUpdateNativeLanguage: (LanguageItem) -> Unit,
    onUpdateTheme: (AppTheme) -> Unit,
    onUpdateColorStyle: (PaletteStyle) -> Unit,
    onUpdatePrimaryColor: (ThemeColor) -> Unit,
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
                    LANGUAGES_LIST.take(10).forEach { language ->
                        DialogRadioOption(
                            text = language.englishName,
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
                    LANGUAGES_LIST.take(10).forEach { language ->
                        DialogRadioOption(
                            text = language.englishName,
                            selected = language == uiState.nativeLanguage,
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