package com.venom.phrase.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun NavigateButton(categoryName: String, onClick: () -> Unit) {
    FilledTonalIconButton(
        onClick = onClick, colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        )
    ) {
        Icon(
            Icons.Rounded.ChevronRight,
            contentDescription = "View $categoryName phrases",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}