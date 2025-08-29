package com.venom.ui.components.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.venom.resources.R

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
    iconSize: Dp = 24.dp,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    textStyle: TextStyle = MaterialTheme.typography.bodySmall,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    contentPadding: PaddingValues = PaddingValues(vertical = 4.dp)
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        contentPadding = contentPadding
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            when (icon) {
                is ImageVector -> Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = if (enabled) iconTint else iconTint.copy(0.38f),
                    modifier = Modifier.size(iconSize)
                )
                is Int -> Icon(
                    painter = painterResource(id = icon),
                    contentDescription = text,
                    tint = if (enabled) iconTint else iconTint.copy(0.38f),
                    modifier = Modifier.size(iconSize)
                )
            }
            if (withText) {
                Text(
                    text = text,
                    style = textStyle,
                    color = if (enabled) textColor else textColor.copy(0.38f),
                    maxLines = 1,
                )
            }
        }
    }
}

@Preview
@Composable
private fun CustomTextButtonPreview() {
    MaterialTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            CustomTextButton(
                onClick = {},
                icon = Icons.Rounded.Add,
                text = "Add Item"
            )
            CustomTextButton(
                onClick = {},
                icon = R.drawable.icon_ocr_mode,
                text = "Settings",
                enabled = false
            )
        }
    }
}