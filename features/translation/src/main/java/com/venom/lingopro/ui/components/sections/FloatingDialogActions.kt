package com.venom.lingopro.ui.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.venom.resources.R
import com.venom.ui.components.common.adp
import com.venom.ui.components.common.asp

@Composable
fun FloatingDialogActions(
    onDismiss: () -> Unit,
    onOpenInApp: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        // Close button
        SmallFloatingActionButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.adp),
            onClick = { onDismiss() },
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 6.adp,
                pressedElevation = 8.adp
            ),
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.action_close),
                modifier = Modifier.size(20.adp)
            )
        }

        // Open in App button
        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.adp)
                .shadow(12.adp, RoundedCornerShape(16.adp)),
            onClick = {
                onOpenInApp()
                onDismiss()
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 8.adp,
                pressedElevation = 12.adp
            ),
            shape = RoundedCornerShape(16.adp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.adp, vertical = 12.adp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = stringResource(R.string.open_in_app),
                    modifier = Modifier.size(22.adp)
                )
                Spacer(modifier = Modifier.width(8.adp))
                Text(
                    text = stringResource(R.string.open_in_app),
                    fontSize = 15.asp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}