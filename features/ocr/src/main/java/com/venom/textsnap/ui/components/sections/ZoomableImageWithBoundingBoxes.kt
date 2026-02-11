package com.venom.textsnap.ui.components.sections

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.venom.data.mapper.getBoxRect
import com.venom.data.remote.respnod.ParagraphBox
import com.venom.resources.R
import com.venom.textsnap.ui.components.DrawBoundingBox
import com.venom.textsnap.ui.viewmodel.OcrViewModel
import com.venom.utils.calculateConstrainedOffset

@Composable
fun ZoomableImageWithBoundingBoxes(
    viewModel: OcrViewModel,
    imageBitmap: ImageBitmap,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var containerWidth by remember { mutableFloatStateOf(0f) }
    var containerHeight by remember { mutableFloatStateOf(0f) }

    val boxRects = remember(uiState.currentParagraphs, containerWidth, containerHeight) {
        uiState.currentParagraphs.associateWith { box ->
            getBoxRect(box.boundingBlock, imageBitmap, containerWidth, containerHeight, 10)
        }
    }

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val pathPoints = remember { mutableStateListOf<Offset>() }
    var draggedBoxes by remember { mutableStateOf(emptySet<ParagraphBox>()) }

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.5f, 3f)
        offset = calculateConstrainedOffset(offset, panChange, scale, containerWidth, containerHeight)
    }

    @Suppress("UnusedBoxWithConstraintsScope")
    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(imageBitmap.width.toFloat() / imageBitmap.height.toFloat())
            .fillMaxSize()
            .graphicsLayer {
                scaleX = scale; scaleY = scale
                translationX = offset.x; translationY = offset.y
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        pathPoints.clear()
                        pathPoints.add(it)
                        draggedBoxes = emptySet()
                    },
                    onDrag = { change, _ ->
                        pathPoints.add(change.position)
                        uiState.paragraphBoxes.find { boxRects[it]?.contains(change.position) == true }
                            ?.takeIf { it !in draggedBoxes }
                            ?.let {
                                viewModel.toggleSelectBox(it)
                                draggedBoxes = draggedBoxes + it
                            }
                    },
                    onDragEnd = {
                        pathPoints.clear()
                        draggedBoxes = emptySet()
                    }
                )
            }
    ) {
        containerWidth = constraints.maxWidth.toFloat()
        containerHeight = constraints.maxHeight.toFloat()

        Image(
            bitmap = imageBitmap,
            contentDescription = stringResource(R.string.ocr_image_content_description),
            modifier = Modifier.fillMaxSize().align(Alignment.Center).transformable(transformableState),
            alignment = Alignment.Center,
            contentScale = ContentScale.Crop
        )

        if (pathPoints.isNotEmpty()) {
            Canvas(Modifier.fillMaxSize()) {
                val path = Path().apply {
                    pathPoints.forEachIndexed { i, pt ->
                        if (i == 0) moveTo(pt.x, pt.y) else lineTo(pt.x, pt.y)
                    }
                }
                drawPath(
                    path, Color.White.copy(0.5f),
                    style = Stroke(30f / scale, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
        }

        SelectionContainer {
            uiState.currentParagraphs.forEach { box ->
                boxRects[box]?.let { rect ->
                    DrawBoundingBox(
                        box = box,
                        rect = rect,
                        isSelected = box in uiState.selectedBoxes,
                        isParagraphMode = uiState.isParagraphMode,
                        showLabels = uiState.showLabels,
                        imageBitmap = imageBitmap,
                        onSelect = { viewModel.toggleSelectBox(box) }
                    )
                }
            }
        }
    }
}