package com.venom.ui.components.common

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.utils.getTextDirection

@Composable
fun DynamicStyledText(
    text: Any,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Unspecified,
    minFontSize: Int = 22,
    maxFontSize: Int = 30,
    lineHeight: Int = 30,
    maxLines: Int = Int.MAX_VALUE,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = MaterialTheme.colorScheme.onBackground,
) {

    val dynamicFontSize = (maxFontSize - (text.toString().count() / 5)).coerceAtLeast(minFontSize)

    val baseStyle = MaterialTheme.typography.headlineMedium
    val customStyle = baseStyle.copy(
        fontSize = dynamicFontSize.sp,
        color = color,
        lineHeight = lineHeight.sp,
        fontWeight = fontWeight,
        textDirection = getTextDirection(text.toString()),
        shadow = Shadow(
            color = MaterialTheme.colorScheme.onBackground.copy(0.2f),
            offset = Offset(2f, 2f),
            blurRadius = 4f
        )
    )

    SelectionContainer {
        when (text) {
            is AnnotatedString -> {
                Text(
                    text = text,
                    textAlign = textAlign,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = maxLines,
                    style = customStyle,
                    modifier = modifier.padding(8.dp)
                )
            }

            is String -> {
                Text(
                    text = text,
                    textAlign = textAlign,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = maxLines,
                    style = customStyle,
                    modifier = modifier.padding(8.dp)
                )
            }

            else -> {}
        }
    }
}