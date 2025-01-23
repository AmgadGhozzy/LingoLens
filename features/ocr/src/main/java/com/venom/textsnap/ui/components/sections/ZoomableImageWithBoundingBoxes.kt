package com.venom.textsnap.ui.components.sections

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.venom.data.mapper.getBoxRect
import com.venom.data.model.ParagraphBox
import com.venom.resources.R
import com.venom.textsnap.ui.components.DrawBoundingBox
import com.venom.textsnap.ui.viewmodel.OcrViewModel
import com.venom.utils.calculateConstrainedOffset

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ZoomableImageWithBoundingBoxes(
    viewModel: OcrViewModel,
    imageBitmap: ImageBitmap = ImageBitmap.imageResource(id = R.drawable.ocr_test_image),
    modifier: Modifier = Modifier
) {
    // Collect UI state from the ViewModel
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    Log.d("ZoomableImageWithBoundingBoxes", "ZoomableImageWithBoundingBoxes: $uiState")

    var containerWidth by remember { mutableFloatStateOf(0f) }
    var containerHeight by remember { mutableFloatStateOf(0f) }

    // Memoized box rect calculations
    val boxRects = remember(uiState.currentParagraphs, containerWidth, containerHeight) {
        uiState.currentParagraphs.associate { box ->
            val rect = getBoxRect(
                block = box.boundingBlock,
                imageBitmap = imageBitmap,
                containerWidth = containerWidth,
                containerHeight = containerHeight,
                padding = 10
            )
            Log.d("ZoomableImageWithBoundingBoxes", "Box: $box, Rect: $rect")
            box to rect
        }
    }

    // Zoom and pan state
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    // Path tracking for drawing
    val pathPoints = remember { mutableStateListOf<Offset>() }

    // Selection state
    var draggedBoxes by remember { mutableStateOf(setOf<ParagraphBox>()) }

    // Transformable state with constrained zoom
    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.5f, 3f)
        offset = calculateConstrainedOffset(
            currentOffset = offset,
            panChange = panChange,
            scale = scale,
            containerWidth = containerWidth,
            containerHeight = containerHeight
        )
    }

    BoxWithConstraints(modifier = modifier
        .aspectRatio(imageBitmap.width.toFloat() / imageBitmap.height.toFloat())
        .fillMaxSize()
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
            translationX = offset.x
            translationY = offset.y
        }
        .pointerInput(Unit) {
            detectDragGestures(onDragStart = { startPoint ->
                pathPoints.clear()
                pathPoints.add(startPoint)
                draggedBoxes = emptySet()
            }, onDrag = { change, _ ->
                pathPoints.add(change.position)
                val boxAtPoint = uiState.paragraphBoxes.find {
                    boxRects[it]?.contains(change.position) == true
                }
                boxAtPoint?.let { box ->
                    // Only toggle if this box has not dragged over before
                    if (box !in draggedBoxes) {
                        viewModel.toggleSelectBox(box)
                        draggedBoxes = draggedBoxes + box
                    }
                }
            }, onDragEnd = {
                pathPoints.clear()
                draggedBoxes = emptySet()
            })
        }) {
        containerWidth = constraints.maxWidth.toFloat()
        containerHeight = constraints.maxHeight.toFloat()

        Image(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .transformable(transformableState),
            bitmap = imageBitmap,
            contentDescription = stringResource(id = R.string.ocr_image_content_description),
            alignment = Alignment.Center,
            contentScale = ContentScale.Crop,
        )

        // Draw path during drag
        if (pathPoints.isNotEmpty()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val path = Path().apply {
                    pathPoints.forEachIndexed { index, point ->
                        if (index == 0) moveTo(point.x, point.y)
                        else lineTo(point.x, point.y)
                    }
                }
                drawPath(
                    path = path, color = Color.White.copy(alpha = 0.5f), style = Stroke(
                        width = 30f / scale, cap = StrokeCap.Round, join = StrokeJoin.Round
                    )
                )
            }
        }

        SelectionContainer {
            uiState.currentParagraphs.forEach { box ->
                DrawBoundingBox(
                    box = box,
                    rect = boxRects[box]!!,
                    isSelected = box in uiState.selectedBoxes,
                    isParagraphMode = uiState.isParagraphMode,
                    showLabels = uiState.showLabels,
                    imageBitmap = imageBitmap,
                    onSelect = { viewModel.toggleSelectBox(box) },
                )
            }
        }
    }
}
