// TranslationApi.kt
package com.venom.textsnap.data.api

import com.venom.textsnap.data.model.TranslationResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface TranslationApi {
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
        private const val BASE_URL = "https://translate.googleapis.com/"

        fun create(): TranslationApi =
            Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
                .build().create(TranslationApi::class.java)
    }
}