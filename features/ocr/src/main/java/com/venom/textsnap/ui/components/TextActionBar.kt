package com.venom.textsnap.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.textsnap.R

@Composable
fun TextActionBar(
    modifier: Modifier = Modifier,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onSpeak: () -> Unit,
    onTranslate: () -> Unit,
    onMoreOptions: () -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // More Options Button with Dropdown
            DropdownMenuTrigger(onOptionSelected = { selectedOption ->
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

            // Action Buttons
            TextActionButton(
                onClick = onShare,
                icon = R.drawable.ic_share,
                text = stringResource(R.string.action_share)
            )
            TextActionButton(
                onClick = onSpeak,
                icon = R.drawable.icon_sound,
                text = stringResource(R.string.action_speak)
            )
            TextActionButton(
                onClick = onTranslate,
                icon = R.drawable.ic_translate,
                text = stringResource(R.string.action_translate)
            )
            TextActionButton(
                onClick = onCopy,
                icon = R.drawable.icon_copy,
                text = stringResource(R.string.action_copy)
            )
        }
    }
}

@Composable
private fun TextActionButton(
    onClick: () -> Unit,
    @DrawableRes icon: Int,
    text: String,
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.primary
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = text,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TextActionBarPreview() {
    TextActionBar(onCopy = {}, onShare = {}, onSpeak = {}, onTranslate = {})
}