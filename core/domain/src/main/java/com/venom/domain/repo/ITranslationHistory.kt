package com.venom.domain.repo

import com.venom.domain.model.TranslationResult
import kotlinx.coroutines.flow.Flow

interface ITranslationHistory {
    suspend fun saveTranslation(entry: TranslationResult): Long
    suspend fun getTranslation(sourceText: String, sourceLang: String, targetLang: String): TranslationResult?
    suspend fun getTranslationById(id: Long): TranslationResult?
    fun getTranslationHistory(): Flow<List<TranslationResult>>
    fun getBookmarkedTranslations(): Flow<List<TranslationResult>>
    fun getTranslationsByProvider(providerId: String): Flow<List<TranslationResult>>
    suspend fun updateTranslation(entry: TranslationResult)
    suspend fun deleteTranslation(entry: TranslationResult)
    suspend fun updateBookmarkStatus(id: Long, isBookmarked: Boolean)
    suspend fun clearBookmarks()
    suspend fun deleteNonBookmarkedEntries()
    suspend fun clearAllHistory()
}