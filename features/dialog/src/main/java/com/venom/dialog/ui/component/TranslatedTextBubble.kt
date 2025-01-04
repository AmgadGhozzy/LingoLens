package com.venom.dialog.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TranslatedTextBubble(
    text: String,
    isSender: Boolean,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .widthIn(max = 280.dp)
            .padding(8.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = if (isSender) 16.dp else 4.dp,
                topEnd = if (isSender) 4.dp else 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            color = if (isSender) Color(0xFF1D4ED8) else Color(0xFF1E3A8A),
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 6.dp
        ) {
            TranslatedTextContent(
                text = text,
                isSender = isSender,
                onPlayClick = onPlayClick
            )
        }
    }
}