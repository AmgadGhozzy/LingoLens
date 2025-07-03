package com.venom.wordcraftai.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.core.graphics.scale
import java.io.ByteArrayOutputStream
import java.io.InputStream

object ImageUtil {

    fun uriToBase64(context: Context, uri: Uri): String {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        // Resize the bitmap before encoding to reduce size
        val resizedBitmap = resizeBitmap(bitmap, 800, 800)
        return bitmapToBase64(resizedBitmap)
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        // Reduce quality to 80% to decrease file size
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val byteArray = outputStream.toByteArray()
        val base64String = Base64.encodeToString(byteArray, Base64.NO_WRAP)
        return "data:image/png;base64,$base64String"
    }

    fun resizeBitmap(bitmap: Bitmap, maxWidth: Int = 1024, maxHeight: Int = 1024): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val scaleWidth = maxWidth.toFloat() / width
        val scaleHeight = maxHeight.toFloat() / height
        val scaleFactor = minOf(scaleWidth, scaleHeight)

        return if (scaleFactor < 1) {
            val newWidth = (width * scaleFactor).toInt()
            val newHeight = (height * scaleFactor).toInt()
            bitmap.scale(newWidth, newHeight)
        } else {
            bitmap
        }
    }
}