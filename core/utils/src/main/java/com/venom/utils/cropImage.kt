package com.venom.utils

import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlin.math.roundToInt
import android.graphics.Bitmap as AndroidBitmap
import androidx.compose.ui.geometry.Rect as ComposeRect

fun cropImage(imageBitmap: ImageBitmap, cropRect: ComposeRect): ImageBitmap {
    val width = cropRect.width.roundToInt()
    val height = cropRect.height.roundToInt()

    return AndroidBitmap.createBitmap(width, height, AndroidBitmap.Config.ARGB_8888).apply {
        android.graphics.Canvas(this).drawBitmap(
            imageBitmap.asAndroidBitmap(),
            Rect(
                cropRect.left.roundToInt(),
                cropRect.top.roundToInt(),
                cropRect.right.roundToInt(),
                cropRect.bottom.roundToInt()
            ),
            RectF(0f, 0f, width.toFloat(), height.toFloat()),
            Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        )
    }.asImageBitmap()
}