package com.venom.dialog.ui.component.sections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.venom.dialog.data.local.model.DialogMessage
import com.venom.dialog.ui.component.AnimatedPlayButton
import com.venom.ui.components.common.DynamicStyledText

@Composable
fun MessageBubble(
    message: DialogMessage,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bubbleColor = if (message.isSender) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
    val textColor = if (message.isSender) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
    val maxWidth = LocalConfiguration.current.screenWidthDp.dp * 0.8f

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isSender) Alignment.End else Alignment.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = if (message.isSender) 20.dp else 4.dp,
                bottomEnd = if (message.isSender) 4.dp else 20.dp
            ),
            color = bubbleColor,
            tonalElevation = 6.dp,
            shadowElevation = 4.dp,
            modifier = Modifier
                .wrapContentSize()
                .widthIn(max = maxWidth)
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DynamicStyledText(
                        text = message.sourceText,
                        maxFontSize = 24,
                        color = textColor,
                        modifier = Modifier.fillMaxWidth()
                    )

                    HorizontalDivider(
                        color = textColor.copy(alpha = 0.2f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    DynamicStyledText(
                        text = message.translatedText,
                        fontWeight = FontWeight.Normal,
                        maxFontSize = 24,
                        color = textColor,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                AnimatedPlayButton(
                    onClick = onPlayClick,
                    tint = textColor,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }
    }
}