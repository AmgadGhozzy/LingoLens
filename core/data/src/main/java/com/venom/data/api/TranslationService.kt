package com.venom.data.api

import com.venom.lingopro.data.model.TranslationResponse

interface TranslationService {
    @retrofit2.http.GET("translate_a/single?dt=t&dt=at&dt=bd&dt=ex&dt=md&dt=rw&dt=ss&dt=rm")
    suspend fun getTranslation(
        @retrofit2.http.Query("client") client: String = "gtx",
        @retrofit2.http.Query("sl") sourceLanguage: String,
        @retrofit2.http.Query("tl") targetLanguage: String,
        @retrofit2.http.Query("q") query: String,
        @retrofit2.http.Query("dj") dj: Int = 1,
        @retrofit2.http.Query("dt") dt: List<String> = listOf(
            "t",
            "at",
            "bd",
            "ex",
            "md",
            "rw",
            "ss",
            "rm"
        ),
        @retrofit2.http.Query("tk") token: String = "361726.206039"
    ): TranslationResponse

    companion object {
        const val BASE_URL = "https://translate.googleapis.com/"
    }
}