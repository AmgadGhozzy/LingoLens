package com.venom.textsnap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.venom.data.model.ParagraphBox
import com.venom.textsnap.ui.components.sections.sampleTextColors

@Composable
fun DrawBoundingBox(
    box: ParagraphBox,
    rect: Rect,
    imageBitmap: ImageBitmap,
    isSelected: Boolean,
    isParagraphMode: Boolean,
    showLabels: Boolean,
    onSelect: () -> Unit
) {
    val density = LocalDensity.current
    val textSize = if (isParagraphMode) box.avgWordHeight * 95f else rect.height * 0.65f

    // Get coordinates from bounding block
    val vertices = box.boundingBlock.vertices
    val boxRect = remember(vertices) {
        Rect(
            left = vertices[0].x.toFloat(),
            top = vertices[0].y.toFloat(),
            right = vertices[2].x.toFloat(),
            bottom = vertices[2].y.toFloat()
        )
    }

    val colors = remember(imageBitmap, boxRect) {
        sampleTextColors(imageBitmap, boxRect)
    }
    if (showLabels) {
        Box(modifier = Modifier
            .offset {
                IntOffset(
                    rect.left
                        .toDp()
                        .roundToPx(),
                    rect.top
                        .toDp()
                        .roundToPx()
                )
            }
            .size(width = with(density) { rect.width.toDp() },
                height = with(density) { rect.height.toDp() })
            .pointerInput(Unit) { detectTapGestures(onTap = { onSelect() }) }

            .background(
                if (isSelected) colors.backgroundColor
                else colors.backgroundColor, RoundedCornerShape(6.dp)
            )) {

            SelectionContainer {
                Text(text = box.text,
                    color = colors.textColor,
                    fontSize = with(density) { textSize.toSp() },
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxSize(),
                    textAlign = TextAlign.Center,
                    lineHeight = with(density) { textSize.toSp() })
            }
        }
    }
}