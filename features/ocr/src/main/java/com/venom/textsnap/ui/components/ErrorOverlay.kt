package com.venom.textsnap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.venom.resources.R
import com.venom.ui.components.common.adp

@Composable
fun ErrorOverlay(modifier: Modifier = Modifier, onRetry: () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(0.9f))
            .padding(32.adp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painterResource(R.drawable.ic_error), null,
                Modifier.size(48.adp), MaterialTheme.colorScheme.error
            )
            Spacer(Modifier.height(16.adp))
            Text(
                stringResource(R.string.error_title),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.adp))
            Text(
                stringResource(R.string.error_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(24.adp))
            Button(onClick = onRetry) {
                Icon(painterResource(R.drawable.ic_retry), null, Modifier.size(16.adp))
                Spacer(Modifier.width(8.adp))
                Text(stringResource(R.string.retry))
            }
        }
    }
}