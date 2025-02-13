package com.venom.domain.model

import com.venom.resources.R

enum class AppLanguage(val readableResId: Int, val flagUnicode: String, val isoCode: String) {
    FOLLOW_SYSTEM(R.string.lang_follow_system, "", ""),
    ENGLISH(R.string.lang_english, "\uD83C\uDDEC\uD83C\uDDE7", "en"),
    CHINESE(R.string.lang_chinese, "\uD83C\uDDE8\uD83C\uDDF3", "zh"),
    GERMAN(R.string.lang_german, "\uD83C\uDDE9\uD83C\uDDEA", "de"),
    ARABIC(R.string.lang_arabic, "\uD83C\uDDF8\uD83C\uDDE6", "ar"),
    FRENCH(R.string.lang_french, "\uD83C\uDDF2\uD83C\uDDEB", "fr"),
    TURKISH(R.string.lang_turkish, "\uD83C\uDDF9\uD83C\uDDF7", "tr"),
    HINDI(R.string.lang_hindi, "\uD83C\uDDEE\uD83C\uDDF3", "hi"),
    INDONESIA(R.string.lang_indonesian, "\uD83C\uDDEE\uD83C\uDDE9", "id"),
    SPANISH(R.string.lang_spanish, "\uD83C\uDDEA\uD83C\uDDF8", "es"),
    JAPANESE(R.string.lang_japanese, "\uD83C\uDDEF\uD83C\uDDF5", "ja"),
    ITALIAN(R.string.lang_italian, "\uD83C\uDDEE\uD83C\uDDF9", "it"),
    KOREAN(R.string.lang_korean, "\uD83C\uDDF0\uD83C\uDDF7", "ko"),
    POLISH(R.string.lang_polish, "\uD83C\uDDF5\uD83C\uDDF1", "pl"),
    PORTUGUESE(R.string.lang_portuguese, "\uD83C\uDDF5\uD83C\uDDF9", "pt")
}
