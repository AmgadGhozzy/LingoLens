package com.venom.settings.presentation.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.venom.ui.components.common.adp
import com.venom.ui.components.other.GlassCard
import com.venom.ui.components.other.GlassThickness
import com.venom.ui.theme.SettingsSpacing

@Composable
fun SettingsCard(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    content: @Composable ColumnScope.() -> Unit
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        thickness = GlassThickness.Thin,
        shape = MaterialTheme.shapes.large,
        showShadow = false,
    ) {
        Column {
            Row(
                modifier = Modifier.padding(
                    start = SettingsSpacing.cardPadding,
                    top = 16.adp
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(title).uppercase(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Column(
                modifier = Modifier.padding(SettingsSpacing.contentPadding),
                verticalArrangement = Arrangement.spacedBy(SettingsSpacing.itemSpacing),
                content = content
            )
        }
    }
}
