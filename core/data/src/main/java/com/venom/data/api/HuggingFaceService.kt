package com.venom.data.api

import com.google.gson.annotations.SerializedName
import com.venom.data.remote.respnod.HuggingFaceTranslationResult
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface HuggingFaceService {
    /**
     * Translates text using Hugging Face's Inference API.
     *
     * @param model The model path to use for translation.
     * @param apiKey The API key for authentication.
     * @param requestBody The request body containing the text to translate.
     * @return The response from the Hugging Face API containing the translated text.
     */
    @POST("models/{model}")
    suspend fun translate(
        @Path("model", encoded = true) model: String = DEFAULT_MODEL,
        @Header("Authorization") apiKey: String,
        @Body requestBody: HuggingFaceRequestBody
    ): List<HuggingFaceTranslationResult>

    companion object {
        const val BASE_URL = "https://api-inference.huggingface.co/"
        const val DEFAULT_MODEL = "facebook/mbart-large-50-many-to-many-mmt"
        const val MULTILINGUAL_MODEL = "google/mt5-small"
    }
}

data class HuggingFaceRequestBody(
    val inputs: String,
    val parameters: HuggingFaceParameters = HuggingFaceParameters()
)

data class HuggingFaceParameters(
    @SerializedName("src_lang")
    val srcLang: String? = null,
    @SerializedName("tgt_lang")
    val tgtLang: String? = null,
    @SerializedName("max_length")
    val maxLength: Int = 512
)
