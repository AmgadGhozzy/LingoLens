package com.venom.ui.components.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.venom.ui.components.common.adp
import com.venom.ui.components.common.asp

@Composable
fun ContentSection(
    page: OnboardingPage,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.adp)
    ) {
        // Title & Subtitle
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.adp)
        ) {
            Text(
                text = page.title,
                fontSize = 32.asp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                lineHeight = 38.asp
            )

            Text(
                text = page.subtitle,
                fontSize = 18.asp,
                fontWeight = FontWeight.Medium,
                color = page.colors.primary,
                textAlign = TextAlign.Center
            )
        }

        // Description
        Text(
            text = page.description,
            fontSize = 16.asp,
            color = MaterialTheme.colorScheme.onBackground.copy(0.85f),
            textAlign = TextAlign.Center,
            lineHeight = 24.asp
        )

        // Features
        if (page.features.isNotEmpty()) {
            Column(
                modifier = Modifier.padding(top = 8.adp),
                verticalArrangement = Arrangement.spacedBy(14.adp)
            ) {
                page.features.forEach { feature ->
                    FeatureItem(
                        text = feature,
                        color = page.colors.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureItem(
    text: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.adp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(22.adp)
        )
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onBackground.copy(0.9f),
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 15.asp
        )
    }
}