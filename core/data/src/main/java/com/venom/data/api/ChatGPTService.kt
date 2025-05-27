package com.venom.data.api

import com.google.gson.annotations.SerializedName
import com.venom.data.model.ChatGPTResponse
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

data class ChatGPTMessage(
    val role: String,
    val content: String
)

data class ChatGPTRequestBody(
    val model: String = "gpt-3.5-turbo",
    val messages: List<ChatGPTMessage>,
    val temperature: Double = 0.2,
    @SerializedName("max_tokens")
    val maxTokens: Int = 500
)