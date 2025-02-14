package com.venom.settings.presentation.screen

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.MailOutline
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.venom.resources.R
import com.venom.settings.presentation.components.AppHeader
import com.venom.settings.presentation.components.SettingsCard
import com.venom.settings.presentation.components.SettingsIcon
import com.venom.settings.presentation.components.SettingsItem
import com.venom.settings.presentation.components.SettingsScaffold
import com.venom.utils.EMAIL
import com.venom.utils.Extensions.shareText
import com.venom.utils.GITHUB
import com.venom.utils.LICENSE
import com.venom.utils.LINKEDIN
import com.venom.utils.PLAY_STORE
import com.venom.utils.PRIVACY
import com.venom.utils.TERMS
import com.venom.utils.openUrl
import com.venom.utils.sendEmail

@Composable
fun AboutScreen(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        SettingsScaffold(
            title = R.string.about,
            onDismiss = onDismiss
        ) {
            item { AppHeader() }
            item { SocialSection(context) }
            item { LegalSection(context) }
            item { CreditsSection() }
        }
    }
}

@Composable
private fun SocialSection(context: Context) {
    SettingsCard(title = R.string.connect_with_us) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingsItem(
                leadingContent = { SettingsIcon(R.drawable.ic_github) },
                title = R.string.github,
                onClick = { context.openUrl(GITHUB) }
            )
            SettingsItem(
                leadingContent = { SettingsIcon(R.drawable.ic_linkedin) },
                title = R.string.linkedin,
                onClick = { context.openUrl(LINKEDIN) }
            )
            SettingsItem(
                leadingContent = { SettingsIcon(Icons.Rounded.MailOutline) },
                title = R.string.report_issue,
                subtitle = stringResource(R.string.report_issue_desc),
                onClick = { context.sendEmail(EMAIL) }
            )
            SettingsItem(
                leadingContent = { SettingsIcon(R.drawable.icon_share) },
                title = R.string.share_app,
                subtitle = stringResource(R.string.share_app_desc),
                onClick = { context.shareText(PLAY_STORE) }
            )
            SettingsItem(
                leadingContent = { SettingsIcon(Icons.Rounded.Star) },
                title = R.string.rate_app,
                subtitle = stringResource(R.string.rate_app_desc),
                onClick = { context.openUrl(PLAY_STORE) }
            )
        }
    }
}

@Composable
private fun LegalSection(context: Context) {
    SettingsCard(title = R.string.legal) {
        SettingsItem(
            leadingContent = { SettingsIcon(Icons.Outlined.LocalLibrary) },
            title = R.string.licenses,
            onClick = { context.openUrl(LICENSE) }
        )
        SettingsItem(
            leadingContent = { SettingsIcon(Icons.Outlined.Policy) },
            title = R.string.privacy_policy,
            onClick = { context.openUrl(PRIVACY) }
        )
        SettingsItem(
            leadingContent = { SettingsIcon(Icons.Outlined.Description) },
            title = R.string.terms,
            onClick = { context.openUrl(TERMS) }
        )
    }
}

@Composable
private fun CreditsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.about_credits),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "❤️",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
    }
}
