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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.venom.domain.model.AppTheme
import com.venom.resources.R
import com.venom.ui.components.buttons.CustomFilledIconButton
import com.venom.ui.components.other.ConfettiView
import com.venom.ui.theme.LingoLensTheme
import com.venom.ui.theme.lingoLens

/**
 * Session completion screen with celebration animation.
 *
 * Performance optimizations:
 * - Lottie animation limited to 2 iterations
 * - Removed unnecessary state management
 * - Simplified button interaction
 *
 * @param onBackToWelcome Callback to return to welcome screen
 * @param onExit Callback to exit/close the screen
 * @param modifier Modifier for styling
 */
@Composable
fun SessionFinishedView(
    onBackToWelcome: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize().padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Celebration effects
        ConfettiView(modifier = Modifier.fillMaxSize())

        LottieAnimation(
            composition = rememberLottieComposition(
                spec = LottieCompositionSpec.RawRes(R.raw.confetti)
            ).value,
            iterations = 2,
            modifier = Modifier.fillMaxSize()
        )
        // Exit button (top-end)
        CustomFilledIconButton(
            icon = Icons.Rounded.ArrowForwardIos,
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = onExit,
            contentDescription = stringResource(R.string.action_close),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.4f),
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.8f)
            ),
            size = 46.dp
        )

        // Main content
        Column(
            modifier = Modifier.padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Success Icon
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .shadow(8.dp, CircleShape)
                    .background(
                        MaterialTheme.lingoLens.semantic.success,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_circle_check),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.lingoLens.semantic.onSuccess
                )
            }

            // Text Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.session_complete),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = stringResource(R.string.session_complete_message),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        lineHeight = 24.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            // Action button
            Button(
                onClick = onBackToWelcome,
                modifier = Modifier
                    .widthIn(max = 260.dp)
                    .height(56.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_house),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
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
private fun SessionFinishedViewPreview() {
    LingoLensTheme(appTheme = AppTheme.DARK) {
        SessionFinishedView(
            onBackToWelcome = {},
            onExit = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F5F9)
@Composable
private fun SessionFinishedViewPreviewLight() {
    LingoLensTheme(appTheme = AppTheme.LIGHT) {
        SessionFinishedView(
            onBackToWelcome = {},
            onExit = {}
        )
    }
}