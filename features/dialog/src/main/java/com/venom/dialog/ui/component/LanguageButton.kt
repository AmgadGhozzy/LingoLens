package com.venom.dialog.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun LanguageButton(
    language: String, onClick: () -> Unit, backgroundColor: Color, modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick, colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        ), contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp), modifier = modifier
    ) {
        Text(
            text = language,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = Icons.Filled.KeyboardArrowDown,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
    }
}
