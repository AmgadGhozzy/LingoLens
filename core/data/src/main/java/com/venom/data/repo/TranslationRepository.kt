package com.venom.data.repo

import android.util.Log
import com.venom.data.local.dao.TranslationDao
import com.venom.data.mapper.TranslateMapper
import com.venom.data.model.TranslationProvider
import com.venom.domain.model.TranslationResult
import com.venom.domain.repo.ITranslationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TranslationRepository @Inject constructor(
    private val dao: TranslationDao,
    private val onlineTranslationOperations: OnlineTranslationOperations,
    private val offlineTranslationOperations: OfflineTranslationOperations,
    private val translationHistoryOperations: TranslationHistoryOperations
) : ITranslationRepository {

    override suspend fun translate(
        sourceText: String,
        sourceLang: String,
        targetLang: String,
        providerId: String,
        forceRefresh: Boolean
    ): Result<TranslationResult> {
        return try {
            // Check cache first if not forcing refresh
            if (!forceRefresh) {
                val cached = dao.getCachedTranslation(sourceText, sourceLang, targetLang, providerId)
                if (cached != null) {
                    return Result.success(
                        TranslateMapper.fromEntity(cached).copy(isFromCache = true)
                    )
                }
            }

            // Get translation from appropriate provider
            val response = when (providerId) {
                TranslationProvider.GOOGLE.id -> onlineTranslationOperations.getGoogleTranslation(sourceLang, targetLang, sourceText)
                TranslationProvider.OFFLINE.id -> offlineTranslationOperations.getOfflineTranslation(sourceLang, targetLang, sourceText)
                TranslationProvider.CHATGPT.id -> onlineTranslationOperations.getChatGPTTranslation(sourceLang, targetLang, sourceText)
                TranslationProvider.GEMINI.id -> onlineTranslationOperations.getGeminiTranslation(sourceLang, targetLang, sourceText)
                TranslationProvider.GROQ.id -> onlineTranslationOperations.getGroqTranslation(sourceLang, targetLang, sourceText)
                TranslationProvider.DEEPSEEK.id -> onlineTranslationOperations.getDeepSeekTranslation(sourceLang, targetLang, sourceText)
                TranslationProvider.HUGGINGFACE.id -> onlineTranslationOperations.getHuggingFaceTranslation(sourceLang, targetLang, sourceText)
                else -> throw Exception("Unknown provider: $providerId")
            }
            Log.d("TranslationRepository", "Translation result: $response")

            // Save to database
            val entity = TranslateMapper.toEntity(response)
            dao.insert(entity)

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun bookmarkTranslation(id: Long, isBookmarked: Boolean) {
        translationHistoryOperations.updateBookmarkStatus(id, isBookmarked)
    }

    override fun getTranslationHistory(): Flow<List<TranslationResult>> {
        return translationHistoryOperations.getTranslationHistory().map { entities ->
            entities.map { TranslateMapper.fromEntity(it) }
        }
    }

    override suspend fun clearHistory() {
        translationHistoryOperations.clearAllHistory()
    }

    // Additional utility methods
    suspend fun getTranslationById(id: Long): TranslationResult? {
        return translationHistoryOperations.getTranslationEntityById(id)?.let {
            TranslateMapper.fromEntity(it)
        }
    }

    fun getBookmarkedTranslations(): Flow<List<TranslationResult>> {
        return translationHistoryOperations.getBookmarkedTranslations().map { entities ->
            entities.map { TranslateMapper.fromEntity(it) }
        }
    }

    fun getTranslationsByProvider(providerId: String): Flow<List<TranslationResult>> {
        return translationHistoryOperations.getTranslationsByProvider(providerId).map { entities ->
            entities.map { TranslateMapper.fromEntity(it) }
        }
    }

    suspend fun toggleBookmark(translation: TranslationResult) {
        val entity = dao.getCachedTranslation(
            translation.sourceText,
            translation.sourceLang,
            translation.targetLang,
            translation.providerId
        )
        entity?.let {
            translationHistoryOperations.updateBookmarkStatus(it.id, !it.isBookmarked)
        }
    }

    suspend fun clearBookmarks() {
        translationHistoryOperations.clearBookmarks()
    }

    suspend fun deleteNonBookmarkedEntries() {
        translationHistoryOperations.deleteNonBookmarkedEntries()
    }
}