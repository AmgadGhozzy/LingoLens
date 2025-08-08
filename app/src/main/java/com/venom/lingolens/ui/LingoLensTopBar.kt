package com.venom.lingolens.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.venom.data.model.TranslationProvider
import com.venom.lingopro.ui.components.sections.ProviderSelectorAction
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomButton
import com.venom.ui.navigation.Screen

@Composable
fun LingoLensTopBar(
    currentScreen: Screen,
    onNavigateBack: () -> Unit,
    onBookmarkClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAboutClick: () -> Unit,
    showProviderSelector: Boolean,
    selectedProvider: TranslationProvider,
    availableProviders: List<TranslationProvider>,
    onProviderSelected: (TranslationProvider) -> Unit,
    modifier: Modifier = Modifier
) {
    val isOcrScreen = currentScreen == Screen.Ocr

    Row(
        modifier = modifier
            .fillMaxWidth()
            .scale(0.95f)
            .height(58.dp)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomButton(
            icon = if (isOcrScreen) Icons.Rounded.ArrowBackIosNew else R.drawable.icon_menu,
            contentDescription = if (isOcrScreen) stringResource(R.string.action_back) else stringResource(
                R.string.action_menu
            ),
            onClick = if (isOcrScreen) onNavigateBack else onAboutClick,
            showBorder = false
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = stringResource(
                when (currentScreen) {
                    is Screen.Quiz -> R.string.quiz_title
                    Screen.Ocr -> R.string.ocr_title
                    else -> R.string.app_name
                }
            ),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showProviderSelector) {
                ProviderSelectorAction(
                    selectedProvider = selectedProvider,
                    availableProviders = availableProviders,
                    onProviderSelected = onProviderSelected
                )
            }

            TopBarActions(onBookmarkClick, onSettingsClick)
        }
    }
}

@Composable
fun TopBarActions(onBookmarkClick: () -> Unit, onSettingsClick: () -> Unit) {
    CustomButton(
        icon = R.drawable.icon_bookmark_filled,
        contentDescription = stringResource(R.string.bookmarks_title),
        onClick = onBookmarkClick,
        showBorder = false
    )
    CustomButton(
        icon = R.drawable.icon_settings,
        contentDescription = stringResource(R.string.settings_title),
        onClick = onSettingsClick,
        showBorder = false
    )
}