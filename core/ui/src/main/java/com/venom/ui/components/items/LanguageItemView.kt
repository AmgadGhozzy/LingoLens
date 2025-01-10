package com.venom.ui.components.items

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.venom.domain.model.LANGUAGES_LIST
import com.venom.domain.model.LanguageItem
import com.venom.resources.R

@Composable
fun LanguageItemView(
    language: LanguageItem,
    isSelected: Boolean = false,
    showNativeNameHint: Boolean = false,
    showFlag: Boolean = false,
    showArrow: Boolean = true,
    isExpanded: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    flagSize: Dp = 40.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
) {
    val interactionSource = remember { MutableInteractionSource() }
    val arrowRotation by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "")

    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource, indication = null
            ) { onClick?.invoke() }
            .padding(contentPadding)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            if (showFlag) {
                LanguageFlag(language = language, flagSize = flagSize)
            }
            LanguageText(
                language = language,
                isSelected = isSelected,
                showNativeNameHint = showNativeNameHint
            )
        }
        if (showArrow && onClick != null) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "Expand language selection",
                modifier = Modifier
                    .size(20.dp)
                    .rotate(arrowRotation),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LanguageFlag(language: LanguageItem, flagSize: Dp) {
    val flagPainter = painterResource(id = language.flagResId ?: R.drawable.default_flag)
    Image(
        painter = flagPainter,
        contentDescription = "Flag of ${language.name}",
        modifier = Modifier
            .size(flagSize)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun LanguageText(language: LanguageItem, isSelected: Boolean, showNativeNameHint: Boolean) {
    Column {
        Text(
            text = language.name,
            style = MaterialTheme.typography.titleMedium,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
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
}

@Preview(showBackground = true)
@Composable
private fun LanguageItemViewPreview() {
    MaterialTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(8.dp)
        ) {
            LanguageItemView(language = LANGUAGES_LIST[0], onClick = {})
            LanguageItemView(language = LANGUAGES_LIST[1],
                isSelected = true,
                showNativeNameHint = true,
                showFlag = true,
                isExpanded = true,
                onClick = {})
            LanguageItemView(language = LANGUAGES_LIST[2],
                showArrow = false,
                showFlag = true,
                onClick = {})
        }
    }
}