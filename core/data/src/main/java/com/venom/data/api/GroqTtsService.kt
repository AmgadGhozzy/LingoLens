package com.venom.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GroqTtsService {
    @POST("openai/v1/audio/speech")
    suspend fun textToSpeech(
        @Header("Authorization") apiKey: String,
        @Body body: GroqTtsRequest
    ): ResponseBody

    companion object {
        const val BASE_URL = "https://api.groq.com/"
        const val ORPHEUS_EN = "canopylabs/orpheus-v1-english"
        const val ORPHEUS_AR = "canopylabs/orpheus-arabic-saudi"
    }
}

@JsonClass(generateAdapter = true)
data class GroqTtsRequest(
    val model: String,
    val input: String,
    val voice: String,
    @Json(name = "response_format")
    val responseFormat: String = "wav"
)
