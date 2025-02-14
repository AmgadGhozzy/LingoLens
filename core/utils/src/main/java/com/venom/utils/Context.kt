package com.venom.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri

/**
 * Opens a URL in the default browser
 */
fun Context.openUrl(url: String) {
    try {
        val normalizedUrl =
            if (url.trim().startsWith("www.")) "https://${url.trim()}" else url.trim()
        startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(normalizedUrl)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * Opens email client with pre-filled recipient
 */
fun Context.sendEmail(address: String) {
    try {
        startActivity(Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$address")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * Finds the activity in the context chain
 */
fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

/**
 * Restarts the app
 */
fun Context.restartApp() {
    try {
        findActivity()?.finish()
        packageManager.getLaunchIntentForPackage(packageName)?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(it)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}