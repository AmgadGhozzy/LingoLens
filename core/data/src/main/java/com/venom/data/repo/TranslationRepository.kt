package com.venom.data.repo

import android.util.Log
import com.venom.data.api.TranslationService
import com.venom.data.local.dao.TranslationDao
import com.venom.data.model.TranslationEntry
import com.venom.data.model.TranslationResponse
import com.venom.utils.Extensions.preprocessText
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
                sourceLanguage = sourceLanguage, targetLanguage = targetLanguage, query = query.preprocessText()
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

    suspend fun getTranslationEntry(
        sourceText: String, sourceLangCode: String, targetLangCode: String
    ): TranslationEntry? {
        return translationDao.getTranslationEntry(sourceText, sourceLangCode, targetLangCode)
    }

    fun getTranslationHistory(): Flow<List<TranslationEntry>> = translationDao.getAllEntries()

    fun getBookmarkedTranslations(): Flow<List<TranslationEntry>> =
        translationDao.getBookmarkedEntries()

    suspend fun updateTranslationEntry(entry: TranslationEntry) = translationDao.update(entry)

    suspend fun deleteTranslationEntry(entry: TranslationEntry) = translationDao.delete(entry)

    suspend fun clearBookmarks() = translationDao.clearBookmarks()

    suspend fun deleteNonBookmarkedEntries() = translationDao.deleteNonBookmarkedEntries()
}