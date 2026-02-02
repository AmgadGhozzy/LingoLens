package com.venom.phrase.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.venom.phrase.data.mapper.getCategoryIcon
import com.venom.ui.components.common.adp

@Composable
fun CategoryIcon(icon: String) {
    Surface(
        shape = RoundedCornerShape(16.adp),
        color = MaterialTheme.colorScheme.primary.copy(0.1f),
        modifier = Modifier.size(52.adp)
    ) {
        Icon(
            imageVector = getCategoryIcon(icon),
            contentDescription = null,
            modifier = Modifier.padding(14.adp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}
