package com.venom.data.api

import com.google.gson.annotations.SerializedName
import com.venom.data.remote.respnod.DeepSeekResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface DeepSeekService {
    /**
     * Translates text using DeepSeek's API.
     *
     * @param apiKey The API key for authentication.
     * @param requestBody The request body containing the model and messages.
     * @return The response from the DeepSeek API containing the translated text.
     */
    @POST("chat/completions")
    suspend fun translate(
        @Header("Authorization") apiKey: String,
        @Body requestBody: DeepSeekRequestBody
    ): DeepSeekResponse

    companion object {
        const val BASE_URL = "https://api.deepseek.com/"
        const val DEFAULT_MODEL = "deepseek-chat"
    }
}

data class DeepSeekMessage(
    val role: String,
    val content: String
)

data class DeepSeekRequestBody(
    val model: String = DeepSeekService.DEFAULT_MODEL,
    val messages: List<DeepSeekMessage>,
    val temperature: Double = 0.2,
    @SerializedName("max_tokens")
    val maxTokens: Int = 500,
    val stream: Boolean = false
)