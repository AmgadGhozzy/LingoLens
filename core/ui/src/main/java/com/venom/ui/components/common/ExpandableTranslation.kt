package com.venom.ui.components.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.venom.ui.components.other.TextShimmer

private const val ANIM_DURATION = 250

@Composable
fun ExpandableTranslation(
    isExpanded: Boolean,
    translatedText: String,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isExpanded,
        enter = fadeIn(tween(ANIM_DURATION)) + expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = tween(ANIM_DURATION)
        ),
        exit = fadeOut(tween(ANIM_DURATION / 2)) + shrinkVertically(
            shrinkTowards = Alignment.Top,
            animationSpec = tween(ANIM_DURATION / 2)
        ),
        modifier = modifier
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            when {
                isLoading -> LoadingState()
                error != null -> ErrorState(error, onRetry)
                translatedText.isNotEmpty() -> TranslatedContent(translatedText)
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Spacer(modifier = Modifier.height(16.adp))
    TextShimmer()
}

@Composable
private fun ErrorState(
    error: String,
    onRetry: () -> Unit
) {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 16.adp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(0.5f)
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = error,
            color = Color.Red.copy(0.8f),
            fontSize = 12.asp,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onRetry) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Retry",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun TranslatedContent(translatedText: String) {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 16.adp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(0.5f)
    )
    DynamicStyledText(
        text = translatedText,
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.onSurface.copy(0.85f)
    )
}