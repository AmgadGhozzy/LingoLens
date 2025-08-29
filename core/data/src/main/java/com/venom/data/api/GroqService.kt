package com.venom.data.api

import com.google.gson.annotations.SerializedName
import com.venom.data.remote.respnod.GroqResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GroqService {
    /**
     * Translates text using Groq's API (Free tier available).
     *
     * @param apiKey The API key for authentication.
     * @param requestBody The request body containing the model and messages.
     * @return The response from the Groq API containing the translated text.
     */
    @POST("openai/v1/chat/completions")
    suspend fun translate(
        @Header("Authorization") apiKey: String,
        @Body requestBody: GroqRequestBody
    ): GroqResponse

    companion object {
        const val BASE_URL = "https://api.groq.com/"
        const val LLAMA_MODEL_3 = "llama3-70b-8192"
        const val LLAMA_MODEL_3_8 = "llama3-8b-8192"
        const val LLAMA_MODEL_4 = "meta-llama/llama-4-scout-17b-16e-instruct"
        const val QWEN_MODEL = "qwen-qwq-32b"
    }
}

data class GroqMessage(
    val role: String,
    val content: String
)

data class GroqRequestBody(
    val model: String = GroqService.LLAMA_MODEL_4,
    val messages: List<GroqMessage>,
    val temperature: Double = 0.2,
    @SerializedName("max_tokens")
    val maxTokens: Int = 500,
    val stream: Boolean = false
)