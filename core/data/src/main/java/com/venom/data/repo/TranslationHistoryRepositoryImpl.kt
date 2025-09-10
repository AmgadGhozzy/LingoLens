package com.venom.data.repo

import com.venom.data.local.dao.TranslationDao
import com.venom.data.mapper.TranslateMapper.fromEntity
import com.venom.data.mapper.TranslateMapper.toEntity
import com.venom.domain.model.TranslationResult
import com.venom.domain.repo.ITranslationHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TranslationHistoryRepositoryImpl @Inject constructor(
    private val translationDao: TranslationDao
) : ITranslationHistory {

    override suspend fun saveTranslation(entry: TranslationResult): Long = withContext(Dispatchers.IO) {
        translationDao.insert(toEntity(entry))
    }

    override suspend fun getTranslation(
        sourceText: String,
        sourceLang: String,
        targetLang: String
    ): TranslationResult? = withContext(Dispatchers.IO) {
        translationDao.getTranslationEntity(sourceText, sourceLang, targetLang)?.let {
            fromEntity(it)
        }
    }

    override suspend fun getTranslationById(id: Long): TranslationResult? = withContext(Dispatchers.IO) {
        translationDao.getTranslationById(id)?.let {
            fromEntity(it)
        }
    }

    override fun getTranslationHistory(): Flow<List<TranslationResult>> =
        translationDao.getAllEntries().map { entities ->
            entities.map { fromEntity(it) }
        }

    override fun getBookmarkedTranslations(): Flow<List<TranslationResult>> =
        translationDao.getBookmarkedEntries().map { entities ->
            entities.map { fromEntity(it) }
        }

    override fun getTranslationsByProvider(providerId: String): Flow<List<TranslationResult>> =
        translationDao.getEntriesByProvider(providerId).map { entities ->
            entities.map { fromEntity(it) }
        }

    override suspend fun updateTranslation(entry: TranslationResult) = withContext(Dispatchers.IO) {
        translationDao.update(toEntity(entry))
    }

    override suspend fun deleteTranslation(entry: TranslationResult) = withContext(Dispatchers.IO) {
        translationDao.delete(toEntity(entry))
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