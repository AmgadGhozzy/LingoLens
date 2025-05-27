package com.venom.lingopro.ui.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.venom.data.model.TranslationProvider
import com.venom.resources.R
import com.venom.ui.components.common.ActionItem
import com.venom.ui.components.menus.AnimatedDropdownMenu
import com.venom.ui.components.other.PremiumBadge

@Composable
fun ProviderSelectorAction(
    selectedProvider: TranslationProvider,
    availableProviders: List<TranslationProvider>,
    onProviderSelected: (TranslationProvider) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var isMenuExpanded by rememberSaveable { mutableStateOf(false) }

    val providerActions = availableProviders.map { provider ->
        ActionItem.Action(
            icon = provider.iconResId,
            description = provider.descriptionResId,
            onClick = {
                if (provider.id != selectedProvider.id) {
                    onProviderSelected(provider)
                }
            }
        )
    }

    IconButton(
        onClick = { isMenuExpanded = true },
        enabled = enabled,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(selectedProvider.iconResId),
            contentDescription = stringResource(
                R.string.change_translation_provider_cd,
                stringResource(selectedProvider.nameResId)
            ),
            tint = if (enabled) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
            },
            modifier = Modifier.size(32.dp)
        )
    }

    AnimatedDropdownMenu(
        expanded = isMenuExpanded,
        onDismissRequest = { isMenuExpanded = false },
        options = providerActions,
        customContent = { index, _ ->
            ProviderItem(
                provider = availableProviders[index],
                isSelected = availableProviders[index].id == selectedProvider.id
            )
        }
    )
}

@Composable
private fun ProviderItem(
    provider: TranslationProvider,
    isSelected: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                painter = painterResource(provider.iconResId),
                contentDescription = null,
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSecondaryContainer
                },
                modifier = Modifier.padding(8.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(provider.nameResId),
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            val description = stringResource(provider.descriptionResId)
            if (description.isNotEmpty()) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (provider.isPremium) {
                PremiumBadge()
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = stringResource(R.string.selected_indicator_cd),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}