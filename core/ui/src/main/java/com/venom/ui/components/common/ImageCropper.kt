package com.venom.ui.components.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.venom.resources.R
import com.venom.utils.cropImage

@Composable
fun ImageCropper(
    imageBitmap: ImageBitmap, onImageCropped: (ImageBitmap) -> Unit, modifier: Modifier = Modifier
) {
    var imageSize by remember { mutableStateOf(IntSize.Zero) }
    var cropRect by remember { mutableStateOf<Rect?>(null) }
    var dragHandle by remember { mutableStateOf<DragHandle?>(null) }

    val density = LocalDensity.current
    val visualProps = remember(density) {
        VisualProperties(handleRadius = with(density) { 8.dp.toPx() },
            handleLength = with(density) { 56.dp.toPx() },
            handleThickness = with(density) { 6.dp.toPx() },
            touchArea = with(density) { 100.dp.toPx() },
            strokeWidth = with(density) { 4.dp.toPx() },
            minCropSize = with(density) { 80.dp.toPx() })
    }

    LaunchedEffect(imageSize) {
        if (cropRect == null && imageSize.width > 0 && imageSize.height > 0) {
            cropRect = Rect(
                imageSize.width * 0.1f,
                imageSize.height * 0.1f,
                imageSize.width * 0.9f,
                imageSize.height * 0.9f
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(MaterialTheme.colorScheme.scrim), contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier
            .wrapContentSize()
            .aspectRatio(imageBitmap.width.toFloat() / imageBitmap.height)
            .onSizeChanged { imageSize = it }
            .pointerInput(Unit) {
                detectDragGestures(onDragStart = { offset ->
                    cropRect?.let { rect ->
                        dragHandle = DragHandle.fromOffset(offset, rect, visualProps)

                        // If not on a handle and not in the crop rect, set to MOVE
                        if (dragHandle == null && offset !in rect) {
                            dragHandle = DragHandle.MOVE
                        }
                    }
                }, onDrag = { change, dragAmount ->
                    cropRect = cropRect?.let { rect ->
                        updateCropRect(
                            rect, dragHandle, dragAmount, imageSize, visualProps.minCropSize
                        )
                    }
                    change.consume()
                }, onDragEnd = { dragHandle = null })
            }) {
            drawImage(imageBitmap, dstSize = imageSize)
            cropRect?.let { rect ->
                drawCropOverlay(rect, Color.White, Color.Black.copy(alpha = 0.5f), visualProps)
            }
        }

        cropRect?.let { rect ->
            FloatingActionButton(
                onClick = { cropImage(imageBitmap, rect).let(onImageCropped) },
                modifier = Modifier
                    .padding(end = 32.dp, bottom = 48.dp)
                    .align(Alignment.BottomEnd),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = "Confirm crop",
                )
            }
        }
    }
}

private fun DrawScope.drawCropOverlay(
    rect: Rect, handleColor: Color, overlayColor: Color, props: VisualProperties
) {
    // Draw overlay
    drawRect(overlayColor, Offset.Zero, Size(size.width, rect.top))
    drawRect(overlayColor, Offset(0f, rect.bottom), Size(size.width, size.height - rect.bottom))
    drawRect(overlayColor, Offset(0f, rect.top), Size(rect.left, rect.height))
    drawRect(overlayColor, Offset(rect.right, rect.top), Size(size.width - rect.right, rect.height))

    // Draw crop rectangle
    drawRect(
        handleColor.copy(alpha = 0.5f), rect.topLeft, rect.size, style = Stroke(
            props.strokeWidth
        )
    )

    // Draw corner handles with offset
    val cornerHandles = listOf(
        Offset(rect.left + props.handleOffset, rect.top + props.handleOffset),
        Offset(rect.right - props.handleOffset, rect.top + props.handleOffset),
        Offset(rect.left + props.handleOffset, rect.bottom - props.handleOffset),
        Offset(rect.right - props.handleOffset, rect.bottom - props.handleOffset)
    )

    cornerHandles.forEach { point ->
        drawCircle(handleColor, props.handleRadius, point)
    }

    // Draw edge handles
    val handles = listOf(
        Pair(Offset(rect.center.x, rect.top), props.handleLength to props.handleThickness),
        Pair(Offset(rect.center.x, rect.bottom), props.handleLength to props.handleThickness),
        Pair(Offset(rect.left, rect.center.y), props.handleThickness to props.handleLength),
        Pair(Offset(rect.right, rect.center.y), props.handleThickness to props.handleLength)
    )

    handles.forEach { (center, size) ->
        val (width, height) = size
        drawRoundRect(
            handleColor,
            topLeft = Offset(center.x - width / 2, center.y - height / 2),
            size = Size(width, height),
            cornerRadius = CornerRadius(props.handleThickness / 2)
        )
    }
}

private fun updateCropRect(
    rect: Rect, handle: DragHandle?, dragAmount: Offset, imageSize: IntSize, minCropSize: Float
): Rect {
    return when (handle) {
        DragHandle.TOP_LEFT -> rect.copy(
            left = (rect.left + dragAmount.x).coerceIn(0f, rect.right - minCropSize),
            top = (rect.top + dragAmount.y).coerceIn(0f, rect.bottom - minCropSize)
        )

        DragHandle.TOP_RIGHT -> rect.copy(
            right = (rect.right + dragAmount.x).coerceIn(
                rect.left + minCropSize, imageSize.width.toFloat()
            ), top = (rect.top + dragAmount.y).coerceIn(0f, rect.bottom - minCropSize)
        )

        DragHandle.BOTTOM_LEFT -> rect.copy(
            left = (rect.left + dragAmount.x).coerceIn(0f, rect.right - minCropSize),
            bottom = (rect.bottom + dragAmount.y).coerceIn(
                rect.top + minCropSize, imageSize.height.toFloat()
            )
        )

        DragHandle.BOTTOM_RIGHT -> rect.copy(
            right = (rect.right + dragAmount.x).coerceIn(
                rect.left + minCropSize, imageSize.width.toFloat()
            ), bottom = (rect.bottom + dragAmount.y).coerceIn(
                rect.top + minCropSize, imageSize.height.toFloat()
            )
        )

        DragHandle.TOP -> rect.copy(
            top = (rect.top + dragAmount.y).coerceIn(0f, rect.bottom - minCropSize)
        )

        DragHandle.BOTTOM -> rect.copy(
            bottom = (rect.bottom + dragAmount.y).coerceIn(
                rect.top + minCropSize, imageSize.height.toFloat()
            )
        )

        DragHandle.LEFT -> rect.copy(
            left = (rect.left + dragAmount.x).coerceIn(0f, rect.right - minCropSize)
        )

        DragHandle.RIGHT -> rect.copy(
            right = (rect.right + dragAmount.x).coerceIn(
                rect.left + minCropSize, imageSize.width.toFloat()
            )
        )

        DragHandle.MOVE -> {
            val newLeft = (rect.left + dragAmount.x).coerceIn(0f, imageSize.width - rect.width)
            val newTop = (rect.top + dragAmount.y).coerceIn(0f, imageSize.height - rect.height)
            rect.translate(Offset(newLeft - rect.left, newTop - rect.top))
        }

        null -> rect
    }
}

private data class VisualProperties(
    val handleRadius: Float,
    val handleLength: Float,
    val handleThickness: Float,
    val touchArea: Float,
    val strokeWidth: Float,
    val minCropSize: Float
) {
    val handleOffset = handleRadius * 0.4f
    val touchRadius = touchArea / 2
    val touchLength = handleLength * 2f
    val touchThickness = touchArea
}

private enum class DragHandle {
    TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, TOP, BOTTOM, LEFT, RIGHT, MOVE;

    companion object {
        fun fromOffset(offset: Offset, rect: Rect, props: VisualProperties): DragHandle? {
            val handles = mapOf(
                TOP_LEFT to Rect(
                    rect.left - props.touchRadius,
                    rect.top - props.touchRadius,
                    rect.left + props.touchRadius,
                    rect.top + props.touchRadius
                ), TOP_RIGHT to Rect(
                    rect.right - props.touchRadius,
                    rect.top - props.touchRadius,
                    rect.right + props.touchRadius,
                    rect.top + props.touchRadius
                ), BOTTOM_LEFT to Rect(
                    rect.left - props.touchRadius,
                    rect.bottom - props.touchRadius,
                    rect.left + props.touchRadius,
                    rect.bottom + props.touchRadius
                ), BOTTOM_RIGHT to Rect(
                    rect.right - props.touchRadius,
                    rect.bottom - props.touchRadius,
                    rect.right + props.touchRadius,
                    rect.bottom + props.touchRadius
                ),

                TOP to Rect(
                    rect.center.x - props.touchLength / 2,
                    rect.top - props.touchThickness / 2,
                    rect.center.x + props.touchLength / 2,
                    rect.top + props.touchThickness / 2
                ), BOTTOM to Rect(
                    rect.center.x - props.touchLength / 2,
                    rect.bottom - props.touchThickness / 2,
                    rect.center.x + props.touchLength / 2,
                    rect.bottom + props.touchThickness / 2
                ), LEFT to Rect(
                    rect.left - props.touchThickness / 2,
                    rect.center.y - props.touchLength / 2,
                    rect.left + props.touchThickness / 2,
                    rect.center.y + props.touchLength / 2
                ), RIGHT to Rect(
                    rect.right - props.touchThickness / 2,
                    rect.center.y - props.touchLength / 2,
                    rect.right + props.touchThickness / 2,
                    rect.center.y + props.touchLength / 2
                )
            )

            return handles.entries.firstOrNull { offset in it.value }?.key
                ?: if (offset in rect) MOVE else null
        }
    }
}

@Preview()
@Composable
fun PreviewImageCropper() {
    ImageCropper(imageBitmap = ImageBitmap.imageResource(id = R.drawable.ocr_test_image),
        onImageCropped = {})
}