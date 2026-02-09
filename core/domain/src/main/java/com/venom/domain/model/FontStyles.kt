package com.venom.domain.model

enum class FontStyles(val title: String) {
    Default("Default"),
    SANS_SERIF("Sans Serif"),
    SERIF("Serif"),
    MONOSPACE("Monospace"),
    CURSIVE("Cursive"),

    CAIRO("Cairo"),
    ALEXANDRIA("Alexandria"),
    CAVEAT("Caveat"),
    ROBOTO("Roboto"),
    QUICKSAND("Quicksand"),
    JOSEFIN_SANS("Josefin Sans"),
    PLAYPEN_SANS("Playpen Sans")
}

data class MultilingualFontConfig(
    val arabicFont: FontStyles = FontStyles.CAIRO,
    val englishFont: FontStyles = FontStyles.ROBOTO,
    val fallbackFont: FontStyles = FontStyles.Default
)
