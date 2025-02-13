package com.venom.settings.presentation.components

import androidx.annotation.StringRes
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SettingsSwitchItem(
    @StringRes title: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    enabled: Boolean = true
) {
    SettingsItem(
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        onClick = { onCheckedChange(!checked) },
        enabled = enabled,
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled
            )
        }
    )
}
