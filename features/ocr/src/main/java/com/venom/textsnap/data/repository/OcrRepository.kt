package com.venom.textsnap.data.repository

import com.venom.textsnap.data.api.OcrApiService
import com.venom.textsnap.data.model.OcrResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class OcrRepository(private val service: OcrApiService) {
    suspend fun performOcr(imageFile: File): Result<OcrResponse> {
        return try {
            val authToken =
                "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiZDJhMzNiODgtZjdjMi00MzIyLThkNDEtZWI4NzY1YmNkYTZjIiwidHlwZSI6ImFwaV90b2tlbiJ9.NCRDaPF5ko8cIuwiXLzkCck7IvBrTHqeAns2VHqT4rA"
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