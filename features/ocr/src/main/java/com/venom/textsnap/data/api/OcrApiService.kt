package com.venom.textsnap.data.api

import com.venom.textsnap.data.model.OcrResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface OcrApiService {
    @Multipart
    @POST("v2/ocr/ocr")
    suspend fun performOcr(
        @Header("authorization") authToken: String,
        @Part("show_original_response") showOriginalResponse: RequestBody,
        @Part("fallback_providers") fallbackProviders: RequestBody,
        @Part("providers") providers: RequestBody,
        @Part file: MultipartBody.Part,
        @Part("language") language: RequestBody
    ): Response<OcrResponse>

    companion object {
        private const val BASE_URL = "https://api.edenai.run/"

        fun create(): OcrApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OcrApiService::class.java)
        }
    }
}