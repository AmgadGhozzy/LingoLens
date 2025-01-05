package com.venom.dialog.ui.component.sections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.dialog.data.model.DialogMessage
import com.venom.dialog.ui.component.AnimatedPlayButton
import com.venom.domain.model.LanguageItem

@Composable
fun MessageBubble(
    message: DialogMessage, onPlayClick: () -> Unit, modifier: Modifier = Modifier
) {
    val bubbleColor = if (message.isSender) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.tertiary
    }

    val textColor = if (message.isSender) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onTertiary
    }

    Column(
        modifier = modifier.fillMaxWidth(),
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
            tonalElevation = 4.dp,
            shadowElevation = 2.dp,
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = message.sourceText, style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 18.sp
                        ), color = textColor
                    )
                    Text(
                        text = message.translatedText,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 16.sp
                        ),
                        color = textColor.copy(alpha = 0.8f)
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

@Preview(showBackground = true)
@Composable
fun MessageBubblePreview() {
    MaterialTheme {
        Surface(
            color = Color(0xFF121212),
            modifier = Modifier.padding(16.dp)
        ) {
            Column {
                MessageBubble(
                    message = DialogMessage(
                        sourceText = "How are you.",
                        translatedText = "مرحبا.",
                        sourceLanguage = LanguageItem("en", "English"),
                        targetLanguage = LanguageItem("ar", "Arabic"),
                        isSender = true
                    ),
                    onPlayClick = {}
                )

                Spacer(modifier = Modifier.height(8.dp))

                MessageBubble(
                    message = DialogMessage(
                        sourceText = "Welcome.",
                        translatedText = "اهلا وسهلا.",
                        sourceLanguage = LanguageItem("ar", "Arabic"),
                        targetLanguage = LanguageItem("en", "English"),
                        isSender = false
                    ),
                    onPlayClick = {}
                )
            }
        }
    }
}
