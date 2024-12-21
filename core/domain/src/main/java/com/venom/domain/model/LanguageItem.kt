package com.venom.domain.model

import androidx.annotation.DrawableRes
import com.venom.resources.R

data class LanguageItem(
    val code: String,
    val name: String,
    val nativeName: String = "",
    @DrawableRes val flagResId: Int? = null,
)

val LANGUAGES_LIST = listOf(
    LanguageItem(
        code = "en",
        name = "English",
        nativeName = "English",
        flagResId = R.drawable.flag_en,
    ), LanguageItem(
        code = "ar",
        name = "Arabic",
        nativeName = "العربية",
        flagResId = R.drawable.flag_ar,
    ), LanguageItem(
        code = "es",
        name = "Spanish",
        nativeName = "Español",
        //flagResId = R.drawable.flag_es,
    ), LanguageItem(
        code = "fr",
        name = "French",
        nativeName = "Français",
        //flagResId = R.drawable.flag_fr,
    ), LanguageItem(
        code = "de",
        name = "German",
        nativeName = "Deutsch",
        //lagResId = R.drawable.flag_de,
    ), LanguageItem(
        code = "zh",
        name = "Chinese",
        nativeName = "中文",
        //flagResId = R.drawable.flag_zh,
    ), LanguageItem(
        code = "ru",
        name = "Russian",
        nativeName = "Русский",
        //flagResId = R.drawable.flag_ru,
    )
)