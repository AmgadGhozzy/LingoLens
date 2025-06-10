package com.venom.data.repo

import com.venom.data.model.TranslationEntry
import com.venom.data.model.TranslationProvider
import com.venom.data.model.TranslationResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TranslationRepository @Inject constructor(
    private val onlineTranslationOperations: OnlineTranslationOperations,
    private val offlineTranslationOperations: OfflineTranslationOperations,
    private val translationHistoryOperations: TranslationHistoryOperations
) {

    suspend fun getTranslation(
        sourceLanguage: String = "au",
        targetLanguage: String = "ar",
        query: String,
        provider: TranslationProvider = TranslationProvider.GOOGLE
    ): Result<TranslationResponse> = withContext(Dispatchers.IO) {
        return@withContext runCatching {
            when (provider.id) {
                TranslationProvider.GOOGLE.id -> onlineTranslationOperations.getGoogleTranslation(sourceLanguage, targetLanguage, query)
                TranslationProvider.OFFLINE.id -> offlineTranslationOperations.getOfflineTranslation(sourceLanguage, targetLanguage, query)
                TranslationProvider.CHATGPT.id -> onlineTranslationOperations.getChatGPTTranslation(sourceLanguage, targetLanguage, query)
                TranslationProvider.GEMINI.id -> onlineTranslationOperations.getGeminiTranslation(sourceLanguage, targetLanguage, query)
                TranslationProvider.GROQ.id -> onlineTranslationOperations.getGroqTranslation(sourceLanguage, targetLanguage, query)
                TranslationProvider.DEEPSEEK.id -> onlineTranslationOperations.getDeepSeekTranslation(sourceLanguage, targetLanguage, query)
                TranslationProvider.HUGGINGFACE.id -> onlineTranslationOperations.getHuggingFaceTranslation(sourceLanguage, targetLanguage, query)
                else -> throw Exception("Unknown provider: ${provider.id}")
            }
        }
    }
    suspend fun saveTranslationEntry(entry: TranslationEntry) = translationHistoryOperations.saveTranslationEntry(entry)

    suspend fun getTranslationEntry(sourceText: String, sourceLang: String, targetLang: String): TranslationEntry? =
        translationHistoryOperations.getTranslationEntry(sourceText, sourceLang, targetLang)

    fun getTranslationHistory(): Flow<List<TranslationEntry>> = translationHistoryOperations.getTranslationHistory()

    fun getBookmarkedTranslations(): Flow<List<TranslationEntry>> = translationHistoryOperations.getBookmarkedTranslations()

    suspend fun updateTranslationEntry(entry: TranslationEntry) = translationHistoryOperations.updateTranslationEntry(entry)

    suspend fun deleteTranslationEntry(entry: TranslationEntry) = translationHistoryOperations.deleteTranslationEntry(entry)

    suspend fun clearBookmarks() = translationHistoryOperations.clearBookmarks()

    suspend fun deleteNonBookmarkedEntries() = translationHistoryOperations.deleteNonBookmarkedEntries()
}