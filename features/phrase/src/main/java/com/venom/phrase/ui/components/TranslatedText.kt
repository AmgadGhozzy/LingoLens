package com.venom.phrase.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDirection

@Composable
fun TranslatedText(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.CenterStart),
        style = TextStyle(
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textDirection = TextDirection.Rtl
        )
    )
}
