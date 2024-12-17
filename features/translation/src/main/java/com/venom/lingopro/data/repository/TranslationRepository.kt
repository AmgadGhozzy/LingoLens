package com.venom.lingopro.data.repository

import android.util.Log
import com.venom.lingopro.data.api.TranslationService
import com.venom.lingopro.data.local.dao.TranslationDao
import com.venom.lingopro.data.model.TranslationEntry
import com.venom.lingopro.data.model.TranslationResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TranslationRepository @Inject constructor(
    private val translationService: TranslationService, private val translationDao: TranslationDao
) {
    suspend fun getTranslation(
        sourceLanguage: String = "au", targetLanguage: String = "ar", query: String
    ): Result<TranslationResponse> = withContext(Dispatchers.IO) {
        try {
            val response = translationService.getTranslation(
                sourceLanguage = sourceLanguage, targetLanguage = targetLanguage, query = query
            )
            Result.success(response)
        } catch (e: Exception) {
            Log.e("TranslateRepository", "Translation failed: ${e.message}", e)
            Result.failure(Exception("Translation failed: ${e.message}", e))
        }
    }

    suspend fun saveTranslationEntry(
        translationEntry: TranslationEntry
    ) = withContext(Dispatchers.IO) {
        translationDao.insert(translationEntry)
    }

    fun getTranslationHistory(): Flow<List<TranslationEntry>> = translationDao.getAllEntries()

    fun getBookmarkedTranslations(): Flow<List<TranslationEntry>> =
        translationDao.getBookmarkedEntries()

    suspend fun updateTranslationEntry(entry: TranslationEntry) = translationDao.update(entry)

    suspend fun deleteTranslationEntry(entry: TranslationEntry) = translationDao.delete(entry)

    suspend fun clearBookmarks() = translationDao.clearBookmarks()

    suspend fun deleteNonBookmarkedEntries() = translationDao.deleteNonBookmarkedEntries()
}
