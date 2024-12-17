package com.venom.textsnap.utils

import androidx.compose.ui.geometry.Offset

fun calculateConstrainedOffset(
    currentOffset: Offset,
    panChange: Offset,
    scale: Float,
    containerWidth: Float,
    containerHeight: Float
): Offset {
    val extraWidth = (scale - 1) * containerWidth
    val extraHeight = (scale - 1) * containerHeight
    val maxX = extraWidth / 2
    val maxY = extraHeight / 2

    val scaledPanChange = panChange * scale

    return Offset(
        x = (currentOffset.x + scaledPanChange.x).coerceIn(-maxX, maxX),
        y = (currentOffset.y + scaledPanChange.y).coerceIn(-maxY, maxY)
    )
}