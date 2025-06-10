package com.venom.data.repo

import com.venom.data.model.TranslationEntry
import kotlinx.coroutines.flow.Flow

interface TranslationHistoryOperations {
    suspend fun saveTranslationEntry(entry: TranslationEntry)
    suspend fun getTranslationEntry(sourceText: String, sourceLang: String, targetLang: String): TranslationEntry?
    fun getTranslationHistory(): Flow<List<TranslationEntry>>
    fun getBookmarkedTranslations(): Flow<List<TranslationEntry>>
    suspend fun updateTranslationEntry(entry: TranslationEntry)
    suspend fun deleteTranslationEntry(entry: TranslationEntry)
    suspend fun clearBookmarks()
    suspend fun deleteNonBookmarkedEntries()
}
