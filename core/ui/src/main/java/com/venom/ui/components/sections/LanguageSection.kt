package com.venom.ui.components.sections

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.resources.R

@Composable
fun LanguageSection(
    languageName: String,
    languageCode: String,
    text: String,
    isExpanded: Boolean,
    style: TextStyle,
    modifier: Modifier = Modifier,
    maxCollapsedLines: Int = 2,
    showLanguageCode: Boolean = true,
    onTextClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = languageName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (showLanguageCode) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.extraSmall,
                    tonalElevation = 2.dp
                ) {
                    Text(
                        text = languageCode.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (onTextClick != null) {
                        Modifier.clickable(onClick = onTextClick)
                    } else Modifier
                ),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = text,
                style = style,
                maxLines = if (isExpanded) Int.MAX_VALUE else maxCollapsedLines,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(12.dp)
            )
        }

        if (!isExpanded && text.lines().size > maxCollapsedLines) {
            Text(
                text = stringResource(
                    R.string.more_lines_count, text.lines().size - maxCollapsedLines
                ),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .align(Alignment.End)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LanguageSectionPreview() {
    MaterialTheme {
        LanguageSection(
            languageName = "English",
            languageCode = "en",
            text = """
                This is a sample text that spans
                multiple lines to demonstrate
                how the component handles longer content
                with expansion and collapse functionality.
            """.trimIndent(),
            isExpanded = false,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
    }
}