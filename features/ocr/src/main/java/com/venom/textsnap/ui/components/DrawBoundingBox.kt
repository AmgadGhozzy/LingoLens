package com.venom.textsnap.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.venom.data.model.ParagraphBox


@Composable
fun DrawBoundingBox(
    box: ParagraphBox,
    rect: Rect,
    isSelected: Boolean,
    isParageraphMode: Boolean,
    showLabels: Boolean,
    onSelect: () -> Unit
) {
    val density = LocalDensity.current
    val textSize = if (isParageraphMode) box.avgWordHeight * 1.4f else rect.height * 0.7f

    Log.d("BoundingBox", "box=$box, rect=$rect, isSelected=$isSelected")
    Log.d("BoundingBox", "textSize: $textSize")

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
        .border(
            (if (isSelected) 2 else 1).dp,
            if (isSelected) Color.Green else Color.Red,
            RoundedCornerShape(6.dp)
        )
        .background(
            if (isSelected) Color.Green.copy(alpha = 0.4f)
            else Color.Black.copy(alpha = 0.2f), RoundedCornerShape(6.dp)
        )) {

        if (showLabels) SelectionContainer {
            Text(text = box.text,
                color = Color.White,
                fontSize = with(density) { textSize.toSp() }, // Dinamic font size based on bounding box height
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxSize(),
                textAlign = TextAlign.Center,
                lineHeight = with(density) { textSize.toSp() })
        }
    }
}


