package com.venom.settings.presentation.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.venom.ui.components.common.adp
import com.venom.ui.components.other.GlassCard
import com.venom.ui.components.other.GlassThickness
import com.venom.ui.theme.SettingsSpacing

@Composable
fun SettingsItem(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    subtitle: String? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = {
        Icon(
            Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(SettingsSpacing.iconSize)
        )
    },
    badgeText: String? = null,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        thickness = GlassThickness.UltraThick,
        onClick = if (enabled) onClick else null,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(SettingsSpacing.cardPadding),
            horizontalArrangement = Arrangement.spacedBy(SettingsSpacing.itemSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingContent != null) leadingContent()

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.adp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(title),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(if (enabled) 1f else 0.38f)
                    )
                    if (badgeText != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.extraSmall
                        ) {
                            Text(
                                text = badgeText,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 4.adp, vertical = 2.adp)
                            )
                        }
                    }
                }
                if (subtitle != null) {
                    Spacer(Modifier.height(4.adp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(if (enabled) 1f else 0.38f)
                    )
                }
            }

            if (trailingContent != null) trailingContent()
        }
    }
}