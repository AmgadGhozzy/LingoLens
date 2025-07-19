package com.venom.domain.repo

import com.venom.domain.model.TranslationResult
import kotlinx.coroutines.flow.Flow

interface ITranslationRepository {
    suspend fun translate(
        sourceText: String,
        sourceLang: String = "auto",
        targetLang: String = "ar",
        providerId: String,
        forceRefresh: Boolean = false
    ): Result<TranslationResult>
    
    fun getTranslationHistory(): Flow<List<TranslationResult>>
    
    suspend fun bookmarkTranslation(id: Long, isBookmarked: Boolean)
    
    suspend fun clearHistory()
}
