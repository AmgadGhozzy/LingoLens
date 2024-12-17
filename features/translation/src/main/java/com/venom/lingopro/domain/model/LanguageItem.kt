package com.venom.lingopro.domain.model

import androidx.annotation.DrawableRes

data class LanguageItem(
    val code: String,
    val name: String,
    val nativeName: String = "",
    @DrawableRes val flagResId: Int? = null,
)