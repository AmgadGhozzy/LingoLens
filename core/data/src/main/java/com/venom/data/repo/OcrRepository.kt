package com.venom.data.repo

import android.util.Log
import com.venom.data.BuildConfig
import com.venom.data.api.OcrService
import com.venom.data.local.dao.OcrDao
import com.venom.data.model.OcrEntry
import com.venom.data.model.OcrResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class OcrRepository @Inject constructor(private val ocrService: OcrService,private val ocrDao: OcrDao) {
    suspend fun performOcr(imageFile: File): Result<OcrResponse> = withContext(Dispatchers.IO) {
        try {
            val authToken = "Bearer ${BuildConfig.OCR_API_KEY}"
            val showOriginalResponse = "true".toRequestBody("text/plain".toMediaTypeOrNull())
            val fallbackProviders = "".toRequestBody("text/plain".toMediaTypeOrNull())
            val providers = "google".toRequestBody("text/plain".toMediaTypeOrNull())
            val language = "au".toRequestBody("text/plain".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", imageFile.name, imageFile.asRequestBody("image/*".toMediaTypeOrNull()))

            val response = ocrService.performOcr(
                authToken, showOriginalResponse, fallbackProviders, providers, filePart, language
            )

            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Log.e("OcrRepository", "API call failed with code ${response.code()}")
                Result.failure(Exception("API call failed with code ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("OcrRepository", "API call failed: ${e.message}", e)
            Result.failure(Exception("API call failed: ${e.message}", e))
        }
    }

    suspend fun saveOcrEntry(
        ocrEntry: OcrEntry
    ) = withContext(Dispatchers.IO){
        ocrDao.insert(ocrEntry)
    }

    fun getOcrHistory() = ocrDao.getAllEntries()

    fun getBookmarkedOcrEntries() = ocrDao.getBookmarkedEntries()

    suspend fun updateOcrEntry(entry: OcrEntry) = ocrDao.update(entry)

    suspend fun deleteOcrEntry(entry: OcrEntry) = ocrDao.delete(entry)

    suspend fun clearBookmarks() = ocrDao.clearBookmarks()

    suspend fun deleteNonBookmarkedEntries() = ocrDao.deleteNonBookmarkedEntries()

}