package com.venom.ui.components.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.venom.resources.R
import com.venom.ui.components.common.adp

@Composable
fun CopiedToast(
    visible: Boolean, modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically(),
        modifier = modifier
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(24.adp)
        ) {
            Text(
                text = stringResource(R.string.copied_message),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 12.adp, vertical = 8.adp)
            )
        }
    }
}