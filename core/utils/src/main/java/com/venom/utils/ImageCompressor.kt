package com.venom.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ImageCompressor(private val context: Context) {

    suspend fun compressImage(uri: Uri): Triple<File?, ImageBitmap?, Uri?> {
        return withContext(Dispatchers.IO) {
            try {
                // Decode image dimensions only
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                context.contentResolver.openInputStream(uri)?.use { input ->
                    BitmapFactory.decodeStream(input, null, options)
                }

                // Calculate optimal scale factor
                options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT)
                options.inJustDecodeBounds = false

                // Decode the scaled image as a Bitmap
                var bitmap = context.contentResolver.openInputStream(uri)?.use { input ->
                    BitmapFactory.decodeStream(input, null, options)
                }

                // Rotate the bitmap if needed based on EXIF data
                bitmap = rotateImageIfRequired(uri, bitmap)

                // Process the bitmap and return results
                processBitmap(bitmap)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to compress image from URI", e)
                Triple(null, null, null)
            }
        }
    }

    suspend fun compressImage(imageBitmap: ImageBitmap): Triple<File?, ImageBitmap?, Uri?> {
        return withContext(Dispatchers.IO) {
            try {
                val androidBitmap = imageBitmap.asAndroidBitmap()
                val scaledBitmap = scaleBitmapIfNeeded(androidBitmap)
                processBitmap(scaledBitmap)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to compress ImageBitmap", e)
                Triple(null, null, null)
            }
        }
    }

    private fun scaleBitmapIfNeeded(bitmap: Bitmap): Bitmap {
        return if (bitmap.width > MAX_WIDTH || bitmap.height > MAX_HEIGHT) {
            val scaleWidth = MAX_WIDTH.toFloat() / bitmap.width
            val scaleHeight = MAX_HEIGHT.toFloat() / bitmap.height
            val scaleFactor = minOf(scaleWidth, scaleHeight)

            Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * scaleFactor).toInt(),
                (bitmap.height * scaleFactor).toInt(),
                true
            )
        } else {
            bitmap
        }
    }

    private fun processBitmap(bitmap: Bitmap?): Triple<File?, ImageBitmap?, Uri?> {
        if (bitmap == null) return Triple(null, null, null)

        val compressedFile = createCompressedFile(bitmap)
        val contentUri = generateContentUri(compressedFile)
        val resultImageBitmap = bitmap.asImageBitmap()

        return Triple(compressedFile, resultImageBitmap, contentUri)
    }

    private fun createCompressedFile(bitmap: Bitmap): File {
        val compressedFile =
            File(context.cacheDir, "compressed_image_${System.currentTimeMillis()}.jpg")
        FileOutputStream(compressedFile).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, outputStream)
        }
        return compressedFile
    }

    private fun generateContentUri(file: File): Uri {
        return FileProvider.getUriForFile(
            context, "${context.packageName}.provider", file
        )
    }

    private fun rotateImageIfRequired(uri: Uri?, bitmap: Bitmap?): Bitmap? {
        return try {
            val inputStream: InputStream? =
                context.contentResolver.openInputStream(uri ?: return null)
            val exif = ExifInterface(inputStream!!)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
            )
            inputStream.close()

            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            }
            if (orientation != ExifInterface.ORIENTATION_NORMAL) {
                Bitmap.createBitmap(bitmap!!, 0, 0, bitmap.width, bitmap.height, matrix, true)
            } else {
                bitmap
            }
        } catch (e: Exception) {
            e.printStackTrace()
            bitmap
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
    ): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    companion object {
        val TAG = ImageCompressor::class.java.simpleName
        private const val MAX_WIDTH = 800
        private const val MAX_HEIGHT = 800
        private const val QUALITY = 75
    }
}