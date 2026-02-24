package com.venom.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.venom.data.remote.respnod.ChatGPTResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ChatGPTService {
    /**
     * Translates text using OpenAI's ChatGPT API.
     *
     * @param apiKey The API key for authentication.
     * @param requestBody The request body containing the model and messages.
     * @return The response from the ChatGPT API containing the translated text.
     */
    @POST("v1/chat/completions")
    suspend fun translate(
        @Header("Authorization") apiKey: String,
        @Body requestBody: ChatGPTRequestBody
    ): ChatGPTResponse

    companion object {
        const val BASE_URL = "https://api.openai.com/"
    }
}

@JsonClass(generateAdapter = true)
data class ChatGPTMessage(
    val role: String,
    val content: String
)

@JsonClass(generateAdapter = true)
data class ChatGPTRequestBody(
    val model: String = "gpt-3.5-turbo",
    val messages: List<ChatGPTMessage>,
    val temperature: Double = 0.2,
    @Json(name = "max_tokens")
    val maxTokens: Int = 500
)