package com.venom.stackcard.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.venom.domain.model.AppTheme
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomFilledIconButton
import com.venom.ui.components.common.adp
import com.venom.ui.components.common.asp
import com.venom.ui.components.other.ConfettiView
import com.venom.ui.theme.LingoLensTheme
import com.venom.ui.theme.lingoLens

@Composable
fun SessionFinishedView(
    onBackToWelcome: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ConfettiView()

        CustomFilledIconButton(
            icon = Icons.Rounded.ArrowForwardIos,
            modifier = Modifier.align(Alignment.TopEnd).padding(24.adp),
            onClick = onExit,
            contentDescription = stringResource(R.string.action_close),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.4f),
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.8f)
            ),
            size = 46.adp
        )

        Column(
            modifier = Modifier.padding(horizontal = 32.adp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.adp)
        ) {
            Box(
                modifier = Modifier
                    .size(96.adp)
                    .shadow(8.adp, CircleShape)
                    .background(MaterialTheme.lingoLens.semantic.success, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_circle_check),
                    contentDescription = null,
                    modifier = Modifier.size(48.adp),
                    tint = MaterialTheme.lingoLens.semantic.onSuccess
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.adp)
            ) {
                Text(
                    text = stringResource(R.string.session_complete),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.asp
                    ),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = stringResource(R.string.session_complete_message),
                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.asp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Button(
                onClick = onBackToWelcome,
                modifier = Modifier.widthIn(max = 260.adp).height(56.adp),
                shape = CircleShape,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.adp,
                    pressedElevation = 8.adp
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.adp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_house),
                        contentDescription = null,
                        modifier = Modifier.size(20.adp)
                    )
                    Text(
                        text = stringResource(R.string.action_back_to_home),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun SessionFinishedPreviewDark() {
    LingoLensTheme(appTheme = AppTheme.DARK) {
        SessionFinishedView({}, {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F5F9)
@Composable
private fun SessionFinishedPreviewLight() {
    LingoLensTheme(appTheme = AppTheme.LIGHT) {
        SessionFinishedView({}, {})
    }
}