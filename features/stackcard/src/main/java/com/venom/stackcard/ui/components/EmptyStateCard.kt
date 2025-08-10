package com.venom.stackcard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.ui.components.other.TextShimmer

@Preview
@Composable
fun EmptyStateCard(
    modifier: Modifier = Modifier,
    onRefresh: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .padding(32.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.empty_state_emoji),
                style = MaterialTheme.typography.displayLarge
            )

            Text(
                text = stringResource(id = R.string.empty_state_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(id = R.string.empty_state_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
            )

            Button(
                onClick = onRefresh,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(stringResource(id = R.string.empty_state_button_text))
            }
        }
    }
}

@Preview
@Composable
fun OnboardingOverlay(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.onSurface.copy(0.7f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(32.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.onboarding_title),
                    style = MaterialTheme.typography.headlineSmall
                )

                OnboardingStep(
                    icon = stringResource(id = R.string.onboarding_step_1_icon),
                    title = stringResource(id = R.string.onboarding_step_1_title),
                    description = stringResource(id = R.string.onboarding_step_1_description)
                )

                OnboardingStep(
                    icon = stringResource(id = R.string.onboarding_step_2_icon),
                    title = stringResource(id = R.string.onboarding_step_2_title),
                    description = stringResource(id = R.string.onboarding_step_2_description)
                )

                OnboardingStep(
                    icon = stringResource(id = R.string.onboarding_step_3_icon),
                    title = stringResource(id = R.string.onboarding_step_3_title),
                    description = stringResource(id = R.string.onboarding_step_3_description)
                )

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(id = R.string.onboarding_button_text))
                }
            }
        }
    }
}

@Composable
private fun OnboardingStep(
    icon: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.headlineMedium
        )

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
            )
        }
    }
}

@Preview
@Composable
fun LoadingShimmer(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.size(280.dp, 400.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(0.2f)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            TextShimmer(lines = 3, lineHeight = 32, spacing = 32)
        }
    }
}
