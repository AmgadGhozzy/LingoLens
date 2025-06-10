package com.venom.data.repo

import com.venom.data.local.dao.TranslationDao
import com.venom.data.model.TranslationEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TranslationHistoryRepository @Inject constructor(
    private val translationDao: TranslationDao
) : TranslationHistoryOperations {

    override suspend fun saveTranslationEntry(entry: TranslationEntry) = withContext(Dispatchers.IO) {
        translationDao.insert(entry)
    }

    override suspend fun getTranslationEntry(sourceText: String, sourceLang: String, targetLang: String): TranslationEntry? = withContext(Dispatchers.IO) {
        translationDao.getTranslationEntry(sourceText, sourceLang, targetLang)
    }

    override fun getTranslationHistory(): Flow<List<TranslationEntry>> = translationDao.getAllEntries()

    override fun getBookmarkedTranslations(): Flow<List<TranslationEntry>> = translationDao.getBookmarkedEntries()

    override suspend fun updateTranslationEntry(entry: TranslationEntry) = withContext(Dispatchers.IO) {
        translationDao.update(entry)
    }

    override suspend fun deleteTranslationEntry(entry: TranslationEntry) = withContext(Dispatchers.IO) {
        translationDao.delete(entry)
    }

    override suspend fun clearBookmarks() = withContext(Dispatchers.IO) {
        translationDao.clearBookmarks()
    }

    override suspend fun deleteNonBookmarkedEntries() = withContext(Dispatchers.IO) {
        translationDao.deleteNonBookmarkedEntries()
    }
}
