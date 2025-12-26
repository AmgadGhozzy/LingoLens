package com.venom.ui.screen.langselector

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.data.model.LANGUAGES_LIST
import com.venom.data.model.LanguageItem

/**
 * Displays a language item with an optional flag and native name hint, now with added clickability.
 *
 * @param language The [LanguageItem] data to display.
 * @param isSelected Whether the language is selected, affecting its styling.
 * @param showNativeNameHint Whether to show the native name below the main name.
 * @param showFlag Whether to display the language flag.
 * @param onClick Callback for when the language item is clicked.
 * @param modifier Additional modifier for customization.
 * @param flagSize The size of the flag icon.
 */
@Composable
fun LanguageItemView(
    language: LanguageItem,
    isSelected: Boolean = false,
    showNativeNameHint: Boolean = false,
    showFlag: Boolean = false,
    showArrow: Boolean = true,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    flagSize: Dp = 40.dp
) {
    val interactionSource = remember { MutableInteractionSource() }

    val baseModifier = modifier
        .clip(RoundedCornerShape(12.dp))
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = interactionSource, indication = LocalIndication.current
                ) { onClick() }
            } else {
                Modifier
            })
        .padding(6.dp)

    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = baseModifier
    ) {
        // Flag
        if (showFlag) {

            Text(
                text = language.flag,
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(flagSize)
            )
        }

        // Display the language name and optional native name
        Column {
            Text(
                text = language.englishName,
                style = MaterialTheme.typography.titleMedium,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )

            if (showNativeNameHint) {
                Text(
                    text = language.nativeName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Arrow indicator
        AnimatedVisibility(
            visible = showArrow && onClick != null,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "Select language",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LanguageItemViewClickablePreview() {
    LanguageItemView(
        language = LANGUAGES_LIST[0],
        isSelected = false,
        onClick = {})
}