package com.venom.settings.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.resources.R
import com.venom.settings.presentation.components.AppHeader
import com.venom.settings.presentation.components.SettingsCard
import com.venom.settings.presentation.components.SettingsScaffold
import com.venom.ui.viewmodel.UpdateState
import com.venom.ui.viewmodel.UpdateViewModel
import com.venom.utils.PLAY_STORE
import com.venom.utils.finishActivity
import com.venom.utils.openLink

@Composable
fun UpdateScreen(
    viewModel: UpdateViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
) {
    val state = viewModel.state.collectAsState().value
    val context = LocalContext.current

    fun onBackPressed() {
        if (state.isForceUpdate) context.finishActivity() else onDismiss()
    }

    BackHandler(onBack = ::onBackPressed)

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
            item { UpdateAlert(state) }
            item { WhatsNewSection(state) }
            item { UpdateButtons(state, onDismiss) }
        }
    }
}

@Composable
fun UpdateAlert(state: UpdateState) {
    SettingsCard(title = R.string.update_available) {
        Text(
            text = if (state.isForceUpdate)
                stringResource(id = R.string.force_update_description)
            else stringResource(id = R.string.destination_update_available_desc),
            style = MaterialTheme.typography.bodyLarge,
            color = if (state.isForceUpdate)
                MaterialTheme.colorScheme.onErrorContainer
            else MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
}

@Composable
fun WhatsNewSection(state: UpdateState) {
    SettingsCard(title = R.string.whats_new) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            state.features.split("\n").forEach { feature ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun UpdateButtons(state: UpdateState, onDismiss: () -> Unit) {
    var context = LocalContext.current
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (!state.isForceUpdate) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(R.string.close))
            }
        }

        Button(
            onClick = { context.openLink(PLAY_STORE) },
            modifier = Modifier.weight(1f),
            colors = if (state.isForceUpdate) {
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            } else ButtonDefaults.buttonColors()
        ) {
            Text(text = stringResource(R.string.update))
        }
    }
}
