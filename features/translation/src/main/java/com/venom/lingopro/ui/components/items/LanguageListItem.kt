package com.venom.lingopro.ui.components.items

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DoneOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venom.lingopro.R
import com.venom.lingopro.domain.model.LanguageItem

/**
 * LanguageListItem composable with enhanced design and flexibility
 *
 * @param language The language item to display
 * @param isSelected Whether the language is currently selected
 * @param onClick Callback when the language item is clicked
 * @param showNativeNameHint Whether to show the native language name as a hint
 * @param showFlag Whether to display the language flag
 * @param modifier Additional modifier for customization
 */
@Composable
fun LanguageListItem(
    language: LanguageItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    showNativeNameHint: Boolean = true,
    showFlag: Boolean = true,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(6.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .background(
                if (isSelected) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainerHighest
            )
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
        ) {
            // Language Item View
            LanguageItemView(
                language = language,
                isSelected = isSelected,
                showNativeNameHint = showNativeNameHint,
                showFlag = showFlag,
                modifier = Modifier.weight(1f)
            )

            // Selection Indicator with improved semantics
            AnimatedVisibility(
                visible = isSelected, enter = fadeIn(), exit = fadeOut()
            ) {
                Icon(
                    imageVector = Icons.Rounded.DoneOutline,
                    contentDescription = stringResource(R.string.select_language),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun LanguageListItemPreview() {
    LanguageListItem(language = LanguageItem(
        code = "en", name = "English", nativeName = "English", flagResId = R.drawable.flag_en
    ), isSelected = true, onClick = {})
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun LanguageListItemUnselectedPreview() {
    LanguageListItem(LanguageItem(
        code = "ar", name = "Arabic", nativeName = "العربية", flagResId = R.drawable.flag_ar
    ), isSelected = false, onClick = {})
}