package com.venom.data.api

import com.google.gson.annotations.SerializedName
import com.venom.data.remote.respnod.HuggingFaceTranslationResult
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

interface HuggingFaceService {
    /**
     * Translates text using Hugging Face's Inference API.
     *
     * @param url The full URL including the model path.
     * @param apiKey The API key for authentication.
     * @param requestBody The request body containing the text to translate.
     * @return The response from the Hugging Face API containing the translated text.
     */
    @POST
    suspend fun translate(
        @Url url: String,
        @Header("Authorization") apiKey: String,
        @Body requestBody: HuggingFaceRequestBody
    ): List<HuggingFaceTranslationResult>

    companion object {
        const val BASE_URL = "https://router.huggingface.co/"
        const val DEFAULT_MODEL = "google/madlad400-3b-mt"
        const val MULTILINGUAL_MODEL = "google/mt5-small"

        fun getModelUrl(model: String = DEFAULT_MODEL): String {
            return "${BASE_URL}models/$model"
        }
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