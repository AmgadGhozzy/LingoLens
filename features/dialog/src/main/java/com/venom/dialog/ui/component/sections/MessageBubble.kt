package com.venom.dialog.ui.component.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.dialog.data.model.DialogMessage
import com.venom.domain.model.LanguageItem

@Composable
fun MessageBubble(
    message: DialogMessage,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = if (message.isSender) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (message.isSender) 20.dp else 4.dp,
                        bottomEnd = if (message.isSender) 4.dp else 20.dp
                    )
                )
                .background(if (message.isSender) Color(0xFF1F2937) else Color(0xFF1B3B36))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = message.sourceText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    modifier = Modifier.padding(end = if (message.isSender) 32.dp else 0.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = message.translatedText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )

                    IconButton(
                        onClick = onPlayClick,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.VolumeUp,
                            contentDescription = "Play translation",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
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