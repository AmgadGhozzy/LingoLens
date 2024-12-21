package com.venom.data.mapper

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import com.venom.data.model.BoundingBlock


fun getBoxRect(
    block: BoundingBlock,
    imageBitmap: ImageBitmap,
    containerWidth: Float,
    containerHeight: Float,
    padding: Int = 6
): Rect {
    val imageWidth = imageBitmap.width.toFloat()
    val imageHeight = imageBitmap.height.toFloat()

    // Calculate the bounding box coordinates relative to the image size
    return Rect(left = block.vertices.minOf { it.x.toFloat() } / imageWidth * containerWidth - padding,
        top = block.vertices.minOf { it.y.toFloat() } / imageHeight * containerHeight - padding,
        right = block.vertices.maxOf { it.x.toFloat() } / imageWidth * containerWidth + padding,
        bottom = block.vertices.maxOf { it.y.toFloat() } / imageHeight * containerHeight + padding)
}
