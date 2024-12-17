package com.venom.textsnap.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.textsnap.R

@Composable
fun SelectedTextItem(
    text: String,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onSpeak: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // More Options Button with Dropdown
            DropdownMenuTrigger(
                onOptionSelected = { selectedOption ->
                    when (selectedOption.text) {
                        "Show Paragraphs" -> {
                        }

                        "Show Labels" -> {
                        }

                        "Select All" -> {
                        }

                        "Translate" -> {
                        }

                        "Share" -> {
                        }

                        "Speak" -> {
                        }
                    }
                }

            )

            // Main text with overflow handling
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 16.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Action buttons
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                ActionIconButton(
                    icon = R.drawable.icon_copy, contentDescription = "Copy Text", onClick = onCopy
                )
            }
        }
    }
}

@Composable
private fun ActionIconButton(
    icon: Int, contentDescription: String, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick, modifier = modifier.size(48.dp)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SelectedTextItemPreview() {
    SelectedTextItem(text = "This is a sample text for preview purposes",
        onCopy = {},
        onShare = {},
        onSpeak = {})
}
