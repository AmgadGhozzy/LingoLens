package com.venom.ui.components.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.venom.ui.components.common.adp

/**
 * A composable that creates a text button with an icon and text stacked vertically.
 *
 * @param icon The icon to be displayed. Can be either a [ImageVector] or [@DrawableRes Int].
 * @param onClick The callback to be invoked when the button is clicked.
 * @param text The text label to be displayed below the icon.
 * @param withText Whether to display the text label. Defaults to true.
 * @param modifier Optional [Modifier] for the button.
 * @param enabled Whether the button is enabled or disabled.
 * @param iconSize The size of the icon. Defaults to 24.dp.
 * @param iconTint The tint color for the icon.
 * @param textStyle The style for the text.
 * @param textColor The color for the text.
 * @param contentPadding The padding around the button's content.
 */
@Composable
fun CustomTextButton(
    icon: Any,
    onClick: () -> Unit,
    text: String,
    withText: Boolean = true,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    iconSize: Dp = 24.adp,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    textStyle: TextStyle = MaterialTheme.typography.bodySmall,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    contentPadding: PaddingValues = PaddingValues(vertical = 4.adp)
) {
    // Memoize alpha calculations
    val iconAlpha = remember(enabled) { if (enabled) 1f else 0.38f }
    val textAlpha = remember(enabled) { if (enabled) 1f else 0.38f }

    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        contentPadding = contentPadding
    ) {
        Column(
            modifier = Modifier.padding(6.adp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.adp)
        ) {
            IconContent(
                icon = icon,
                text = text,
                iconTint = iconTint,
                iconAlpha = iconAlpha,
                iconSize = iconSize
            )

            if (withText) {
                Text(
                    text = text,
                    style = textStyle,
                    color = textColor.copy(alpha = textAlpha),
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun IconContent(
    icon: Any,
    text: String,
    iconTint: Color,
    iconAlpha: Float,
    iconSize: Dp
) {
    val tint = iconTint.copy(alpha = iconAlpha)
    val iconModifier = Modifier.size(iconSize)

    when (icon) {
        is ImageVector -> Icon(
            imageVector = icon,
            contentDescription = text,
            tint = tint,
            modifier = iconModifier
        )

        is Int -> Icon(
            painter = painterResource(id = icon),
            contentDescription = text,
            tint = tint,
            modifier = iconModifier
        )
    }
}