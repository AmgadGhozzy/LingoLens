package com.venom.ui.components.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun EmptyState(
    @DrawableRes icon: Int,
    title: String,
    subtitle: String? = null,
    iconSize: Dp = 120.dp,
    iconAlpha: Float = 0.5f,
    titleStyle: TextStyle = MaterialTheme.typography.titleLarge,
    subtitleStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "$title Icon",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(iconSize)
                    .alpha(iconAlpha)
            )

            Text(
                text = title,
                style = titleStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )

            subtitle?.let {
                Text(
                    text = it,
                    style = subtitleStyle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}