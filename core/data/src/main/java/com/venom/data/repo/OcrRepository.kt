package com.venom.data.repo

import com.venom.data.BuildConfig
import com.venom.data.api.OcrApiService
import com.venom.data.model.OcrResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class OcrRepository @Inject constructor(private val service: OcrApiService) {
    suspend fun performOcr(imageFile: File): Result<OcrResponse> {
        return try {
            val authToken = "Bearer ${BuildConfig.OCR_API_KEY}"
            val showOriginalResponse = "true".toRequestBody("text/plain".toMediaTypeOrNull())
            val fallbackProviders = "".toRequestBody("text/plain".toMediaTypeOrNull())
            val providers = "google".toRequestBody("text/plain".toMediaTypeOrNull())
            val language = "au".toRequestBody("text/plain".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData(
                "file", imageFile.name, imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            )

            val response = service.performOcr(
                authToken, showOriginalResponse, fallbackProviders, providers, filePart, language
            )

            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("API call failed with code ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}