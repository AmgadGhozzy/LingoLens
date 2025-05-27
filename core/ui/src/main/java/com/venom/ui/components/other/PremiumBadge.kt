package com.venom.ui.components.other

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.venom.resources.R

@Composable
fun PremiumBadge() {
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.extraSmall
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = stringResource(R.string.provider_premium_badge),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )
    }
}