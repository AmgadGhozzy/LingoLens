package com.venom.phrase.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.venom.phrase.data.mapper.getCategoryIcon

@Composable
fun CategoryIcon(icon: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        modifier = Modifier.size(52.dp)
    ) {
        Icon(
            imageVector = getCategoryIcon(icon),
            contentDescription = null,
            modifier = Modifier.padding(14.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}
