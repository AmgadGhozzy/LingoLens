package com.venom.data.api

import com.google.gson.annotations.SerializedName
import com.venom.data.remote.respnod.GeminiResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GeminiService {
    /**
     * Translates text using Google's Gemini API.
     *
     * @param model The model to use for translation, default is FLASH_MODEL.
     * @param apiKey The API key for authentication.
     * @param request The request body containing the text to translate.
     * @return The response from the Gemini API containing the translated text.
     */
    @POST("v1beta/models/{model}:generateContent")
    suspend fun translate(
        @Path("model") model: String = FLASH_MODEL,
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse

    companion object {
        const val BASE_URL = "https://generativelanguage.googleapis.com/"
        const val FLASH_MODEL = "gemini-2.0-flash-lite"
        const val PRO_MODEL = "gemini-pro"
    }
}

data class GeminiRequest(
    val contents: List<GeminiContent>,
    @SerializedName("generationConfig")
    val generationConfig: GeminiConfig = GeminiConfig()
) {
    companion object {
        fun create(text: String, role: String = "user") = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    role = role,
                    parts = listOf(GeminiPart(text))
                )
            )
        )
    }
}

data class GeminiContent(
    val role: String = "user",
    val parts: List<GeminiPart>
)

data class GeminiPart(
    val text: String
)

data class GeminiConfig(
    val temperature: Double = 0.2,
    val maxOutputTokens: Int = 1024,
    val topP: Double = 0.95,
    val topK: Int = 40,
    val responseMimeType: String = "text/plain"
)