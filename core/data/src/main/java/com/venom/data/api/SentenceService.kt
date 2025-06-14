package com.venom.data.api

import com.venom.data.model.SentenceResponse
import retrofit2.Response
import retrofit2.http.*

interface SentenceService {
    @GET("sentences/{word}")
    suspend fun getSentences(
        @Path("word") word: String,
        @Query("limit") limit: Int? = null
    ): Response<SentenceResponse>

    @GET("health")
    suspend fun healthCheck(): Response<Map<String, String>>

    companion object {
        const val BASE_URL = "https://sentencer.up.railway.app/"
    }
}