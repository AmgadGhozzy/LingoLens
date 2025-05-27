package com.venom.data.repo

import com.venom.data.BuildConfig
import com.venom.data.api.ChatGPTMessage
import com.venom.data.api.ChatGPTRequestBody
import com.venom.data.api.ChatGPTService
import com.venom.data.api.GeminiContent
import com.venom.data.api.GeminiPart
import com.venom.data.api.GeminiRequest
import com.venom.data.api.GeminiService
import com.venom.data.api.TranslationService
import com.venom.data.local.dao.TranslationDao
import com.venom.data.model.ChatGPTResponse
import com.venom.data.model.GeminiResponse
import com.venom.data.model.Sentence
import com.venom.data.model.TranslationEntry
import com.venom.data.model.TranslationProvider
import com.venom.data.model.TranslationResponse
import com.venom.utils.Extensions.preprocessText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TranslationRepository @Inject constructor(
    private val translationService: TranslationService,
    private val chatGPTService: ChatGPTService,
    private val geminiService: GeminiService,
    private val translationDao: TranslationDao
) {
    suspend fun getTranslation(
        sourceLanguage: String = "au",
        targetLanguage: String = "ar",
        query: String,
        provider: TranslationProvider = TranslationProvider.GOOGLE
    ): Result<TranslationResponse> = withContext(Dispatchers.IO) {
        when (provider.id) {
            TranslationProvider.GOOGLE.id -> {
                val response = translationService.getTranslation(
                    sourceLanguage = sourceLanguage,
                    targetLanguage = targetLanguage,
                    query = query.preprocessText()
                )
                Result.success(response)
            }
            TranslationProvider.CHATGPT.id -> {
                val chatGptResponse = getChatGPTTranslation(sourceLanguage, targetLanguage, query)
                Result.success(convertChatGPTToTranslationResponse(chatGptResponse, query))
            }
            TranslationProvider.GEMINI.id -> {
                val geminiResponse = getGeminiTranslation(sourceLanguage, targetLanguage, query)
                Result.success(convertGeminiToTranslationResponse(geminiResponse, query))
            }
            else -> Result.failure(Exception("Unsupported provider"))
        }
    }

    private suspend fun getChatGPTTranslation(sourceLanguage: String, targetLanguage: String, query: String): ChatGPTResponse {
        val promptMessage = "Translate the following text from $sourceLanguage " +
                "to $targetLanguage: \"$query\". Only provide the translation without any explanations or additional text."
        val requestBody = ChatGPTRequestBody(
            messages = listOf(
                ChatGPTMessage(role = "system", content = "You are a translation assistant."),
                ChatGPTMessage(role = "user", content = promptMessage)
            )
        )
        return chatGPTService.translate(BuildConfig.OPENAI_API_KEY, requestBody)
    }

    private suspend fun getGeminiTranslation(sourceLanguage: String, targetLanguage: String, query: String): GeminiResponse {
        val promptMessage =
            "Translate the following text from $sourceLanguage to $targetLanguage: \"$query\". Only provide the translation without any explanations or additional text."

        val requestBody = GeminiRequest(
            contents = listOf(
                GeminiContent(parts = listOf(GeminiPart(text = promptMessage)))
            )
        )
        return geminiService.translate(GeminiService.FLASH_MODEL, BuildConfig.GEMINI_API_KEY, requestBody)
    }

    private fun convertChatGPTToTranslationResponse(chatGPTResponse: ChatGPTResponse, originalText: String): TranslationResponse {
        val translatedText = chatGPTResponse.choices?.firstOrNull()?.message?.content ?: ""
        return TranslationResponse(
            sentences = listOf(
                Sentence(orig = originalText, trans = translatedText, translit = null)
            )
        )
    }

    private fun convertGeminiToTranslationResponse(geminiResponse: GeminiResponse, originalText: String): TranslationResponse {
        val translatedText = geminiResponse.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
        return TranslationResponse(
            sentences = listOf(
                Sentence(orig = originalText, trans = translatedText, translit = null)
            )
        )
    }

    suspend fun saveTranslationEntry(translationEntry: TranslationEntry) = withContext(Dispatchers.IO) {
        translationDao.insert(translationEntry)
    }

    suspend fun getTranslationEntry(sourceText: String, sourceLangCode: String, targetLangCode: String): TranslationEntry? {
        return translationDao.getTranslationEntry(sourceText, sourceLangCode, targetLangCode)
    }

    fun getTranslationHistory(): Flow<List<TranslationEntry>> = translationDao.getAllEntries()

    fun getBookmarkedTranslations(): Flow<List<TranslationEntry>> = translationDao.getBookmarkedEntries()

    suspend fun updateTranslationEntry(entry: TranslationEntry) = translationDao.update(entry)

    suspend fun deleteTranslationEntry(entry: TranslationEntry) = translationDao.delete(entry)

    suspend fun clearBookmarks() = translationDao.clearBookmarks()

    suspend fun deleteNonBookmarkedEntries() = translationDao.deleteNonBookmarkedEntries()
}