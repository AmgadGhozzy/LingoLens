package com.venom.ui.components.common

import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.venom.resources.R
import com.venom.ui.components.buttons.CloseButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScaffold(
    @StringRes title: Int = R.string.settings,
    onDismiss: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(16.adp, 16.adp, 16.adp, 96.adp),
    content: LazyListScope.() -> Unit
) {
    val titleText = stringResource(title)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.adp)
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .padding(horizontal = 16.adp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = titleText,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.semantics {
                        contentDescription = "Settings: $titleText"
                    }
                )

                CloseButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .animateContentSize(),
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(16.adp),
            content = content
        )
    }
}
