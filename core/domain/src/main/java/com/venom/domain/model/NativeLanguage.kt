package com.venom.domain.model

import androidx.annotation.StringRes
import com.venom.resources.R

enum class AppLanguage(
    @StringRes val stringRes: Int? = null,
    val readable: String = "",
    val flagUnicode: String = "",
    val isoCode: String = ""
) {
    FOLLOW_SYSTEM(stringRes = R.string.lang_follow_system),
    NOT_SPECIFIED(readable = "Not Specified", isoCode = ""),
    ENGLISH(readable = "English", isoCode = "en", flagUnicode = "\uD83C\uDDEC\uD83C\uDDE7"),
    CHINESE(readable = "中文", isoCode = "zh", flagUnicode = "\uD83C\uDDE8\uD83C\uDDF3"),
    GERMAN(readable = "Deutsch", isoCode = "de", flagUnicode = "\uD83C\uDDE9\uD83C\uDDEA"),
    ARABIC(readable = "العربية", isoCode = "ar", flagUnicode = "\uD83C\uDDF8\uD83C\uDDE6"),
    FRENCH(readable = "Français", isoCode = "fr", flagUnicode = "\uD83C\uDDF2\uD83C\uDDEB"),
    TURKISH(readable = "Türkçe", isoCode = "tr", flagUnicode = "\uD83C\uDDF9\uD83C\uDDF7"),
    HINDI(readable = "हिन्दी", isoCode = "hi", flagUnicode = "\uD83C\uDDEE\uD83C\uDDF3"),
    INDONESIA(readable = "Indonesia", isoCode = "id", flagUnicode = "\uD83C\uDDEE\uD83C\uDDE9"),
    SPANISH(readable = "Español", isoCode = "es", flagUnicode = "\uD83C\uDDEA\uD83C\uDDF8"),
    JAPANESE(readable = "日本語", isoCode = "ja", flagUnicode = "\uD83C\uDDEF\uD83C\uDDF5"),
    ITALIAN(readable = "Italiano", isoCode = "it", flagUnicode = "\uD83C\uDDEE\uD83C\uDDF9"),
    KOREAN(readable = "한국어", isoCode = "ko", flagUnicode = "\uD83C\uDDF0\uD83C\uDDF7"),
    POLISH(readable = "Polski", isoCode = "pl", flagUnicode = "\uD83C\uDDF5\uD83C\uDDF1"),
    PORTUGUESE(readable = "Português", isoCode = "pt", flagUnicode = "\uD83C\uDDF5\uD83C\uDDF9")
}
