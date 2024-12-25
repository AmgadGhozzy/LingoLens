package com.venom.data.api

import com.venom.data.model.TranslationResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TranslationService {
    @GET("translate_a/single?dt=t&dt=at&dt=bd&dt=ex&dt=md&dt=rw&dt=ss&dt=rm")
    suspend fun getTranslation(
        @Query("client") client: String = "gtx",
        @Query("sl") sourceLanguage: String,
        @Query("tl") targetLanguage: String,
        @Query("q") query: String,
        @Query("dj") dj: Int = 1,
        @Query("dt") dt: List<String> = listOf("t", "at", "bd", "ex", "md", "rw", "ss", "rm"),
        @Query("tk") token: String = "361726.206039"
    ): TranslationResponse

    companion object {
        const val BASE_URL = "https://translate.googleapis.com/"
    }
}