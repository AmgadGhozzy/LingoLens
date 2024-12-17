package com.venom.textsnap.ui.components

import android.annotation.SuppressLint
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.gson.Gson
import com.venom.textsnap.R
import com.venom.textsnap.data.api.OcrApiService
import com.venom.textsnap.data.model.OcrResponse
import com.venom.textsnap.data.model.ParagraphBox
import com.venom.textsnap.data.repository.OcrRepository
import com.venom.textsnap.ui.screens.OcrViewModel
import com.venom.textsnap.utils.Constant
import com.venom.textsnap.utils.Constant.sampleUiState
import com.venom.textsnap.utils.ImageCompressor
import com.venom.textsnap.utils.calculateConstrainedOffset
import com.venom.textsnap.utils.convertToParagraphBoxes
import com.venom.textsnap.utils.getBoxRect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ZoomableImageWithBoundingBoxes(
    viewModel: OcrViewModel, imageBitmap: ImageBitmap, modifier: Modifier = Modifier
) {

    val uiState = viewModel.uiState.collectAsState().value

    var containerWidth by remember { mutableFloatStateOf(0f) }
    var containerHeight by remember { mutableFloatStateOf(0f) }

    // Memoized box rect calculations
    val boxRects = remember(uiState.paragraphBoxes, containerWidth, containerHeight) {
        uiState.paragraphBoxes.associate { box ->
            box to getBoxRect(
                block = box.boundingBlock,
                imageBitmap = imageBitmap,
                containerWidth = containerWidth,
                containerHeight = containerHeight
            )
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
        scale = (scale * zoomChange).coerceIn(1f, 5f)
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
            bitmap = imageBitmap,
            contentDescription = "OCR Image",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .transformable(transformableState)
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
            uiState.paragraphBoxes.forEach { box ->
                DrawBoundingBox(box = box,
                    rect = boxRects[box]!!,
                    isSelected = box in uiState.selectedBoxes,
                    isParageraphMode = uiState.isParagraphMode,
                    showLabels = uiState.showLabels,
                    onSelect = { viewModel.toggleSelectBox(box) })
            }
        }
    }

}

@Preview
@Composable
fun PreviewZoomableImageWithBoundingBoxes() {
    val response: OcrResponse = Gson().fromJson(Constant.OCR_JSON, OcrResponse::class.java)
    val paragraphBoxes = convertToParagraphBoxes(response.google.originalResponse, false)

    val imageBitmap = ImageBitmap.imageResource(id = R.drawable.ocr_test_image)
    val context = LocalContext.current

    ZoomableImageWithBoundingBoxes(imageBitmap = imageBitmap, viewModel = object : OcrViewModel(
        repository = OcrRepository(OcrApiService.create()),
        imageCompressor = ImageCompressor(context)
    ) {
        override val uiState = MutableStateFlow(sampleUiState).asStateFlow()
    })
}