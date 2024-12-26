package com.venom.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.content.ContextCompat
import java.text.DateFormat
import java.util.Date

/**
 * Comprehensive utility extensions for Android development
 */
object Extensions {

    /**
     * UI Extensions
     */
    fun Context.showToast(message: String?, duration: Int = Toast.LENGTH_SHORT) {
        message?.takeIf { it.isNotBlank() }?.let {
            Toast.makeText(this, it, duration).show()
        }
    }

    fun TextView.setProgressText(progressText: String) {
        text = progressText
    }

    fun TextFieldValue.getSelectedOrFullText(): String {
        return if (selection.start != selection.end) {
            text.substring(selection.start, selection.end)
        } else {
            text
        }
    }

    fun TextView.getSelectedOrFullText(): String {
        return if (hasSelection()) {
            text.subSequence(selectionStart, selectionEnd).toString()
        } else {
            text.toString()
        }
    }

    private fun TextView.hasSelection(): Boolean = isFocused && selectionStart != selectionEnd

    /**
     * Clipboard Operations
     */

    fun Context.copyToClipboard(text: String, label: String = "Copied Text") {
        val clipboard = getClipboardManager()
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        this.showToast("$text: Copied")
    }

    fun Context.pasteFromClipboard(): String? = ContextCompat.getSystemService(
        this, ClipboardManager::class.java
    )?.primaryClip?.getItemAt(0)?.text?.toString()

    /**
     * Text Processing
     */
    fun String.splitCamelCase(): String = buildString(this.length + 4) {
        this@splitCamelCase.forEachIndexed { index, char ->
            if (index > 0 && char.isUpperCase() && this@splitCamelCase[index - 1] != ' ') {
                append(' ')
            }
            append(char)
        }
    }

    private fun calculateFontSizeBasedOnBoxHeight(context: Context, boxHeight: Double): Float {
        // Convert bounding box height to appropriate text size in pixels
        val metrics = context.resources.displayMetrics
        return (boxHeight * metrics.density).toFloat()
    }

    fun String.splitCamelCaseRegex(): String =
        this.split("(?<!^)(?<!\\s)(?=[A-Z])".toRegex()).joinToString(" ")

    fun String.sanitize(): String = replace("\n", "").replace(".", "")

    fun String.preprocessText(): String = trim().replace("\n", "☆").replace(".", "★")

    fun String.postprocessText(): String = replace("☆", "\n").replace("★", ".")

    /**
     * View Visibility
     */
    fun View.show() {
        visibility = View.VISIBLE
    }

    fun View.hide() {
        visibility = View.GONE
    }

    fun View.invisible() {
        visibility = View.INVISIBLE
    }

    fun View.toggleVisibility() {
        visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    /**
     * Keyboard Management
     */
    fun View.showKeyboard() {
        requestFocus()
        context.getInputMethodManager().showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    fun View.hideKeyboard() {
        context.getInputMethodManager().hideSoftInputFromWindow(windowToken, 0)
    }

    /**
     * System Services
     */
    fun Context.getVibrator(): Vibrator {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION") getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    fun Context.getClipboardManager(): ClipboardManager =
        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    fun Context.getInputMethodManager(): InputMethodManager =
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    fun Context.isDarkMode(): Boolean =
        resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

    /**
     * Resource Handling
     */
    fun Context.getColorCompat(@ColorRes colorRes: Int) = ContextCompat.getColor(this, colorRes)

    fun Context.getDrawableCompat(@DrawableRes drawableRes: Int) =
        ContextCompat.getDrawable(this, drawableRes)

    fun Context.dpToPx(dp: Float): Int =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()

    /**
     * Intent & Navigation
     */
    fun Context.shareText(text: String?, chooserTitle: String? = null) {
        text?.let {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, it)
            }
            startActivity(Intent.createChooser(shareIntent, chooserTitle))
        }
    }

    fun Context.openAppSettings() {
        startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        })
    }

    fun <T> Context.openActivity(it: Class<T>, flags: Int? = null) {
        startActivity(Intent(this, it).apply {
            flags?.let { f -> this.flags = f }
        })
    }


    /**
     * Collection Extensions
     */
    fun <T> List<T>?.isNotNullOrEmpty(): Boolean = !this.isNullOrEmpty()

    fun <T> Collection<T>?.isNotNullOrEmpty(): Boolean = !this.isNullOrEmpty()

    /**
     * Time Conversions
     */

    fun Long.formatTimestamp(): String =
        DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(this))

    fun Long.millisToSeconds(): Long = this / 1000

    fun Long.secondsToMillis(): Long = this * 1000

    /*
     * Image Processing
     */
    fun ByteArray.toBitmap(): ImageBitmap =
        BitmapFactory.decodeByteArray(this, 0, this.size).asImageBitmap()
}