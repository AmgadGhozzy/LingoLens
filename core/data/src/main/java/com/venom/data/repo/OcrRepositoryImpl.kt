package com.venom.data.repo

import android.util.Log
import com.venom.data.BuildConfig
import com.venom.data.api.OcrService
import com.venom.data.local.Entity.OcrEntity
import com.venom.data.local.dao.OcrDao
import com.venom.data.remote.respnod.OcrResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

private val TEXT_PLAIN = "text/plain".toMediaTypeOrNull()!!

class OcrRepository @Inject constructor(private val ocrService: OcrService,private val ocrDao: OcrDao) {
    suspend fun performOcr(imageFile: File): Result<OcrResponse> = withContext(Dispatchers.IO) {
        try {
            val authToken = BuildConfig.OCR_API_KEY
            val showOriginalResponse = "true".toRequestBody(TEXT_PLAIN)
            val fallbackProviders = "".toRequestBody(TEXT_PLAIN)
            val providers = "google".toRequestBody(TEXT_PLAIN)
            val language = "au".toRequestBody(TEXT_PLAIN)
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
        ocrEntry: OcrEntity
    ) = withContext(Dispatchers.IO){
        ocrDao.insert(ocrEntry)
    }

    fun getOcrHistory() = ocrDao.getAllEntries()

    fun getBookmarkedOcrEntries() = ocrDao.getBookmarkedEntries()

    suspend fun updateOcrEntry(entry: OcrEntity) = ocrDao.update(entry)

    suspend fun deleteOcrEntry(entry: OcrEntity) = ocrDao.delete(entry)

    suspend fun clearBookmarks() = ocrDao.clearBookmarks()

    suspend fun deleteNonBookmarkedEntries() = ocrDao.deleteNonBookmarkedEntries()

}