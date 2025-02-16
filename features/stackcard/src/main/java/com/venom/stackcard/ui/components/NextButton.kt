package com.venom.stackcard.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.venom.resources.R

@Composable
fun NextButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonColor = MaterialTheme.colorScheme.primary
    val disabledColor = MaterialTheme.colorScheme.surfaceVariant

    val animatedColorState by animateColorAsState(
        targetValue = if (enabled) buttonColor else disabledColor,
        label = "button color"
    )

    val scale by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.95f,
        label = "scale"
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 42.dp)
            .height(56.dp)
            .scale(scale),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = animatedColorState,
            disabledContainerColor = disabledColor
        ),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.next),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(end = if (enabled) 8.dp else 0.dp)
            )

            if (enabled) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = stringResource(R.string.next),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
