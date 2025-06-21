package com.venom.data.repo

import com.venom.data.model.TranslationEntity
import kotlinx.coroutines.flow.Flow

interface TranslationHistoryOperations {
    suspend fun saveTranslationEntity(entry: TranslationEntity): Long
    suspend fun getTranslationEntity(sourceText: String, sourceLang: String, targetLang: String): TranslationEntity?
    suspend fun getTranslationEntityById(id: Long): TranslationEntity?
    fun getTranslationHistory(): Flow<List<TranslationEntity>>
    fun getBookmarkedTranslations(): Flow<List<TranslationEntity>>
    fun getTranslationsByProvider(providerId: String): Flow<List<TranslationEntity>>
    suspend fun updateTranslationEntity(entry: TranslationEntity)
    suspend fun deleteTranslationEntity(entry: TranslationEntity)
    suspend fun updateBookmarkStatus(id: Long, isBookmarked: Boolean)
    suspend fun clearBookmarks()
    suspend fun deleteNonBookmarkedEntries()
    suspend fun clearAllHistory()
}