package com.venom.settings.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.venom.ui.theme.SettingsSpacing


@Composable
fun SettingsIcon(
    icon: Any,
    contentDescription: String? = null,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    when (icon) {
        is Int -> Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(SettingsSpacing.iconSize)
        )

        is ImageVector -> Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(SettingsSpacing.iconSize)
        )
    }
}

@Composable
fun UpdateBadge(hasUpdate: Boolean) {
    if (hasUpdate) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(SettingsSpacing.badgeSize)
        ) {}
    }
}

@Composable
fun ColorPreviewIcon(color: Int) {
    Surface(
        modifier = Modifier.size(SettingsSpacing.colorPreviewSize),
        shape = CircleShape,
        color = Color(color),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
        )
    ) {}
}
