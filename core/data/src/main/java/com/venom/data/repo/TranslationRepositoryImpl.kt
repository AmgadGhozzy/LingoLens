package com.venom.data.repo

import android.util.Log
import com.venom.data.local.dao.TranslationDao
import com.venom.data.mapper.TranslateMapper
import com.venom.data.model.TranslationProvider
import com.venom.domain.model.TranslationResult
import com.venom.domain.repo.IOfflineTranslation
import com.venom.domain.repo.IOnlineTranslation
import com.venom.domain.repo.ITranslationHistory
import com.venom.domain.repo.ITranslationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TranslationRepositoryImpl @Inject constructor(
    private val dao: TranslationDao,
    private val onlineTranslator: IOnlineTranslation,
    private val offlineTranslator: IOfflineTranslation,
    private val translationHistory: ITranslationHistory
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
                TranslationProvider.GOOGLE.id -> onlineTranslator.getGoogleTranslation(sourceLang, targetLang, sourceText)
                TranslationProvider.OFFLINE.id -> offlineTranslator.getOfflineTranslation(sourceLang, targetLang, sourceText)
                TranslationProvider.CHATGPT.id -> onlineTranslator.getChatGPTTranslation(sourceLang, targetLang, sourceText)
                TranslationProvider.GEMINI.id -> onlineTranslator.getGeminiTranslation(sourceLang, targetLang, sourceText)
                TranslationProvider.GROQ.id -> onlineTranslator.getGroqTranslation(sourceLang, targetLang, sourceText)
                TranslationProvider.DEEPSEEK.id -> onlineTranslator.getDeepSeekTranslation(sourceLang, targetLang, sourceText)
                TranslationProvider.HUGGINGFACE.id -> onlineTranslator.getHuggingFaceTranslation(sourceLang, targetLang, sourceText)
                else -> throw Exception("Unknown provider: $providerId")
            }
            Log.d("TranslationRepository", "Translation result: $response")

            // Save to database - convert TranslationResult to TranslationEntity
            val entity = TranslateMapper.toEntity(response)
            dao.insert(entity)

            translationHistory.saveTranslation(response)

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun bookmarkTranslation(id: Long, isBookmarked: Boolean) {
        translationHistory.updateBookmarkStatus(id, isBookmarked)
    }

    override fun getTranslationHistory(): Flow<List<TranslationResult>> {
        return translationHistory.getTranslationHistory()
    }

    override suspend fun clearHistory() {
        translationHistory.clearAllHistory()
    }

    // Additional utility methods
    suspend fun getTranslationById(id: Long): TranslationResult? {
        return translationHistory.getTranslationById(id)
    }

    fun getBookmarkedTranslations(): Flow<List<TranslationResult>> {
        return translationHistory.getBookmarkedTranslations()
    }

    fun getTranslationsByProvider(providerId: String): Flow<List<TranslationResult>> {
        return translationHistory.getTranslationsByProvider(providerId)
    }

    suspend fun toggleBookmark(translation: TranslationResult) {
        if (translation.id != 0L) {
            val currentTranslation = translationHistory.getTranslationById(translation.id)
            currentTranslation?.let {
                translationHistory.updateBookmarkStatus(translation.id, !it.isBookmarked)
            }
        } else {
            val existing = translationHistory.getTranslation(
                translation.sourceText,
                translation.sourceLang,
                translation.targetLang
            )
            existing?.let {
                translationHistory.updateBookmarkStatus(it.id, !it.isBookmarked)
            }
        }
    }

    override suspend fun getDownloadedModels(): Set<String> {
        return offlineTranslator.getDownloadedModels()
    }

    override fun getAllOfflineModels(): List<String> {
        return offlineTranslator.getAllModels()
    }

    suspend fun downloadLanguageModel(langCode: String): Result<Unit> {
        return offlineTranslator.downloadLanguageModel(langCode)
    }

    suspend fun deleteLanguageModel(langCode: String): Result<Unit> {
        return offlineTranslator.deleteLanguageModel(langCode)
    }

    // Check if offline translation is available for a language pair
    suspend fun isOfflineTranslationAvailable(sourceLang: String, targetLang: String): Boolean {
        val downloadedModels = getDownloadedModels()
        val allModels = getAllOfflineModels()

        return allModels.contains(sourceLang) &&
                allModels.contains(targetLang) &&
                downloadedModels.contains(sourceLang) &&
                downloadedModels.contains(targetLang)
    }

    // Get languages that need to be downloaded for offline translation
    suspend fun getMissingOfflineLanguages(sourceLang: String, targetLang: String): List<String> {
        val downloadedModels = getDownloadedModels()
        val allModels = getAllOfflineModels()
        val missing = mutableListOf<String>()

        if (allModels.contains(sourceLang) && !downloadedModels.contains(sourceLang)) {
            missing.add(sourceLang)
        }
        if (allModels.contains(targetLang) && !downloadedModels.contains(targetLang)) {
            missing.add(targetLang)
        }

        return missing
    }

    suspend fun clearBookmarks() {
        translationHistory.clearBookmarks()
    }

    suspend fun deleteNonBookmarkedEntries() {
        translationHistory.deleteNonBookmarkedEntries()
    }
}