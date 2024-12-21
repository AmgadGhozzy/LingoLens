package com.venom.utils

// Check if text is right-to-left
fun isTextRightToLeft(text: String): Boolean {
    if (text.isEmpty()) return false
    val rtlChars = "\u0591-\u07FF\uFB1D-\uFDFD\uFE70-\uFEFC"
    val rtlRegex = Regex("[$rtlChars]")
    return rtlRegex.containsMatchIn(text.first().toString())
}
