package com.venom.textsnap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import com.venom.data.remote.respnod.ParagraphBox
import com.venom.textsnap.ui.components.sections.sampleTextColors
import com.venom.ui.components.common.adp

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
    if (!showLabels) return

    val density = LocalDensity.current
    val textSize = if (isParagraphMode) box.avgWordHeight * 1.2f else rect.height * 0.7f

    val vertices = box.boundingBlock.vertices
    val colors = remember(imageBitmap, vertices) {
        sampleTextColors(
            imageBitmap,
            Rect(
                vertices[0].x.toFloat(), vertices[0].y.toFloat(),
                vertices[2].x.toFloat(), vertices[2].y.toFloat()
            )
        )
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(rect.left.toDp().roundToPx(), rect.top.toDp().roundToPx()) }
            .size(with(density) { rect.width.toDp() }, with(density) { rect.height.toDp() })
            .pointerInput(Unit) { detectTapGestures { onSelect() } }
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(0.5f) else colors.backgroundColor,
                RoundedCornerShape(6.adp)
            )
    ) {
        SelectionContainer {
            Text(
                text = box.text,
                color = colors.textColor,
                fontSize = with(density) { textSize.toSp() },
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxSize(),
                textAlign = TextAlign.Center,
                lineHeight = with(density) { textSize.toSp() }
            )
        }
    }
}