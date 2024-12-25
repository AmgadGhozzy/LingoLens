package com.venom.data.api

import com.venom.data.model.OcrResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface OcrService {
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
        const val BASE_URL = "https://api.edenai.run/"
    }
}