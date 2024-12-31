package com.venom.phrase.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp


@Composable
fun ActionTextButton(
    onClick: () -> Unit, icon: ImageVector, text: String
) {
    TextButton(onClick = onClick) {
        Icon(
            imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(text)
    }
}