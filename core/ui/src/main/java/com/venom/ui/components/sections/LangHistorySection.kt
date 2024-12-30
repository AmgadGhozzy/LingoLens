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
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.resources.R

@Composable
fun LangHistorySection(
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = languageName,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (showLanguageCode) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.extraSmall,
                    ) {
                        Text(
                            text = languageCode.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Surface(modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "$languageName text: $text"
            }
            .then(
                if (onTextClick != null) {
                    Modifier.clickable(
                        onClick = onTextClick, role = Role.Button
                    )
                } else Modifier
            ),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 2.dp) {
            SelectionContainer {
                Text(
                    text = text,
                    style = style,
                    maxLines = if (isExpanded) Int.MAX_VALUE else maxCollapsedLines,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        if (!isExpanded && text.lines().size > maxCollapsedLines) {
            Text(
                text = stringResource(
                    R.string.more_lines_count, text.lines().size - maxCollapsedLines
                ),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.End)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun LanguageSectionPreview() {
    MaterialTheme {
        LangHistorySection(
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