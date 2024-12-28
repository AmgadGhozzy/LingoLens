package com.venom.ui.components.speech

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.domain.model.SpeechState
import com.venom.resources.R
import com.venom.ui.components.common.AnimatedControls

@Composable
fun ControlPanel(
    state: SpeechState,
    onStop: () -> Unit,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (state) {
            is SpeechState.Listening -> {
                AnimatedControls {
                    FloatingActionButton(
                        onClick = onPause,
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Icon(Icons.Rounded.Pause, stringResource(R.string.action_pause))
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    FloatingActionButton(
                        onClick = onStop, containerColor = MaterialTheme.colorScheme.error
                    ) {
                        Icon(Icons.Rounded.Stop, "Stop")
                    }
                }
            }

            is SpeechState.Paused -> {
                AnimatedControls {
                    FloatingActionButton(
                        onClick = onResume, containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(Icons.Rounded.PlayArrow, stringResource(R.string.action_resume))
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    FloatingActionButton(
                        onClick = onStop, containerColor = MaterialTheme.colorScheme.error
                    ) {
                        Icon(Icons.Rounded.Stop, stringResource(R.string.action_stop))
                    }
                }
            }

            else -> {
                FloatingActionButton(
                    onClick = onStart,
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        Icons.Rounded.Mic,
                        stringResource(R.string.action_speak),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ControlPanelPreview() {
    ControlPanel(state = SpeechState.Listening,
        onStop = {},
        onStart = {},
        onPause = {},
        onResume = {})
}
