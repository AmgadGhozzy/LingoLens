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
     * @param model The model to use for translation, default is GEMMA_MODEL.
     * @param apiKey The API key for authentication.
     * @param request The request body containing the text to translate.
     * @return The response from the Gemini API containing the translated text.
     */
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generate(
        @Path("model") model: String = FLASH_MODEL_3,
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse

    companion object {
        const val BASE_URL = "https://generativelanguage.googleapis.com/"
        const val FLASH_MODEL = "gemini-2.5-flash-lite"
        const val FLASH_MODEL_3 = "gemini-3-flash-preview"
        const val PRO_MODEL = "gemini-2.5-pro"
        const val GEMMA_MODEL = "gemma-3-27b"
    }
}

data class GeminiRequest(
    val contents: List<GeminiContent>,
    @SerializedName("systemInstruction")
    val systemInstruction: GeminiContent? = null,
    @SerializedName("generationConfig")
    val generationConfig: GeminiConfig = GeminiConfig()
) {
    companion object {
        fun create(
            text: String,
            role: String = "user",
            systemInstructionText: String? = null
        ) = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    role = role,
                    parts = listOf(GeminiPart(text))
                )
            ),
            systemInstruction = systemInstructionText?.let {
                GeminiContent(parts = listOf(GeminiPart(it)))
            }
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
    val temperature: Double = 1.0,
    val maxOutputTokens: Int = 16384,
    val topP: Double = 0.95,
    val topK: Int = 64,
    val responseMimeType: String = "application/json",
    @SerializedName("response_schema")
    val responseSchema: Map<String, Any>? = null,
    @SerializedName("thinkingConfig")
    val thinkingConfig: Map<String, Any>? = null
)

data class GeminiConfig1(
    val temperature: Double = 0.2,
    val maxOutputTokens: Int = 1024,
    val topP: Double = 0.95,
    val topK: Int = 40,
    val responseMimeType: String = "text/plain"
)