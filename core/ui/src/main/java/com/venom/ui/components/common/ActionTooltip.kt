package com.venom.ui.components.common

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.delay

@Composable
fun ActionTooltip(
    @StringRes descriptionRes: Int,
    onDismiss: () -> Unit
) {
    LaunchedEffect(descriptionRes) {
        delay(3000)
        onDismiss()
    }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessLow)) +
                scaleIn(animationSpec = spring(stiffness = Spring.StiffnessMedium)),
        exit = fadeOut() + scaleOut()
    ) {
        Surface(
            modifier = Modifier
                .padding(top = 64.adp)
                .clip(RoundedCornerShape(16.adp)),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shadowElevation = 4.adp
        ) {
            Text(
                text = stringResource(descriptionRes),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(12.adp)
            )
        }
    }
}
