package com.venom.data.api

import com.venom.data.remote.respnod.SentenceResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface SentenceService {
    @GET("{word}")
    suspend fun getSentences(
        @Path("word") word: String,
        @Query("limit") limit: Int? = null,
        @Query("source") source: String = "all",
        @Header("Authorization") authToken: String,
        @Header("apikey") apiKey: String
    ): Response<SentenceResponse>


    companion object {
        const val BASE_URL =
            "https://kbfoelamasuioqhdbghg.supabase.co/functions/v1/word_to_sentence/"
    }
}