package com.venom.data.repo

import com.venom.data.local.dao.TranslationDao
import com.venom.data.model.TranslationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TranslationHistoryRepository @Inject constructor(
    private val translationDao: TranslationDao
) : TranslationHistoryOperations {

    override suspend fun saveTranslationEntity(entry: TranslationEntity) = withContext(Dispatchers.IO) {
        translationDao.insert(entry)
    }

    override suspend fun getTranslationEntity(
        sourceText: String,
        sourceLang: String,
        targetLang: String
    ): TranslationEntity? = withContext(Dispatchers.IO) {
        translationDao.getTranslationEntity(sourceText, sourceLang, targetLang)
    }

    override suspend fun getTranslationEntityById(id: Long): TranslationEntity? = withContext(Dispatchers.IO) {
        translationDao.getTranslationById(id)
    }

    override fun getTranslationHistory(): Flow<List<TranslationEntity>> =
        translationDao.getAllEntries()

    override fun getBookmarkedTranslations(): Flow<List<TranslationEntity>> =
        translationDao.getBookmarkedEntries()

    override fun getTranslationsByProvider(providerId: String): Flow<List<TranslationEntity>> =
        translationDao.getEntriesByProvider(providerId)

    override suspend fun updateTranslationEntity(entry: TranslationEntity) = withContext(Dispatchers.IO) {
        translationDao.update(entry)
    }

    override suspend fun deleteTranslationEntity(entry: TranslationEntity) = withContext(Dispatchers.IO) {
        translationDao.delete(entry)
    }

    override suspend fun updateBookmarkStatus(id: Long, isBookmarked: Boolean) = withContext(Dispatchers.IO) {
        translationDao.updateBookmarkStatus(id, isBookmarked)
    }

    override suspend fun clearBookmarks() = withContext(Dispatchers.IO) {
        translationDao.clearBookmarks()
    }

    override suspend fun deleteNonBookmarkedEntries() = withContext(Dispatchers.IO) {
        translationDao.deleteNonBookmarkedEntries()
    }

    override suspend fun clearAllHistory() = withContext(Dispatchers.IO) {
        translationDao.clearAll()
    }
}