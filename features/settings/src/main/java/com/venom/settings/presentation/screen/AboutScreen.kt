package com.venom.settings.presentation.screen

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.MailOutline
import androidx.compose.material.icons.rounded.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import com.venom.resources.R
import com.venom.settings.presentation.components.AppHeader
import com.venom.settings.presentation.components.SettingsCard
import com.venom.settings.presentation.components.SettingsIcon
import com.venom.settings.presentation.components.SettingsItem
import com.venom.ui.components.common.SettingsScaffold
import com.venom.ui.components.dialogs.WebViewDialog
import com.venom.utils.EMAIL
import com.venom.utils.Extensions.shareText
import com.venom.utils.GITHUB
import com.venom.utils.LINKEDIN
import com.venom.utils.PLAY_STORE
import com.venom.utils.openUrl
import com.venom.utils.sendEmail

@Composable
fun AboutScreen(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var showTermsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showLicensesDialog by remember { mutableStateOf(false) }

    SettingsScaffold(
        title = R.string.about,
        onDismiss = onDismiss
    ) {
        item { AppHeader() }
        item { SocialSection(context) }
        item {
            LegalSection(
                onTermsClick = { showTermsDialog = true },
                onPrivacyClick = { showPrivacyDialog = true },
                onLicensesClick = { showLicensesDialog = true }
            )
        }
    }

    if (showTermsDialog) {
        WebViewDialog(
            title = R.string.terms,
            assetPath = "terms.html",
            onDismiss = { showTermsDialog = false }
        )
    }

    if (showPrivacyDialog) {
        WebViewDialog(
            title = R.string.privacy_policy,
            assetPath = "privacy.html",
            onDismiss = { showPrivacyDialog = false }
        )
    }

    if (showLicensesDialog) {
        OpenSourceLicensesDialog(
            onDismiss = { showLicensesDialog = false }
        )
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
private fun LegalSection(
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onLicensesClick: () -> Unit
) {
    SettingsCard(title = R.string.legal) {
        SettingsItem(
            leadingContent = { SettingsIcon(Icons.Outlined.LocalLibrary) },
            title = R.string.open_source_licenses,
            subtitle = stringResource(R.string.open_source_licenses_desc),
            onClick = onLicensesClick
        )
        SettingsItem(
            leadingContent = { SettingsIcon(Icons.Outlined.Policy) },
            title = R.string.privacy_policy,
            onClick = onPrivacyClick
        )
        SettingsItem(
            leadingContent = { SettingsIcon(Icons.Outlined.Description) },
            title = R.string.terms,
            onClick = onTermsClick
        )
    }
}

@Composable
private fun OpenSourceLicensesDialog(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        LibrariesContainer(
            modifier = Modifier.fillMaxSize()
        )
    }
}