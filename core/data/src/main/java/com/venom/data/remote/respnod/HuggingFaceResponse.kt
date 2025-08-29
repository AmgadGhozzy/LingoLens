package com.venom.data.remote.respnod

import com.google.gson.annotations.SerializedName

data class HuggingFaceTranslationResult(
    @SerializedName("translation_text")
    val translationText: String
)