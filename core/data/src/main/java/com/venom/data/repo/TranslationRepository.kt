package com.venom.data.repo

import com.venom.data.BuildConfig
import com.venom.data.api.*
import com.venom.data.api.ChatGPTMessage
import com.venom.data.api.DeepSeekMessage
import com.venom.data.api.GeminiContent
import com.venom.data.api.GeminiPart
import com.venom.data.api.GroqMessage
import com.venom.data.local.dao.TranslationDao
import com.venom.data.model.*
import com.venom.utils.Extensions.preprocessText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

class TranslationRepository @Inject constructor(
    private val translationService: TranslationService,
    private val chatGPTService: ChatGPTService,
    private val geminiService: GeminiService,
    private val groqService: GroqService,
    private val deepSeekService: DeepSeekService,
    private val huggingFaceService: HuggingFaceService,
    private val translationDao: TranslationDao
) {

    suspend fun getTranslation(
        sourceLanguage: String = "au",
        targetLanguage: String = "ar",
        query: String,
        provider: TranslationProvider = TranslationProvider.GOOGLE
    ): Result<TranslationResponse> = withContext(Dispatchers.IO) {
        runCatching {
            when (provider.id) {
                TranslationProvider.GOOGLE.id -> getGoogleTranslation(sourceLanguage, targetLanguage, query)
                TranslationProvider.CHATGPT.id -> getChatGPTTranslation(sourceLanguage, targetLanguage, query)
                TranslationProvider.GEMINI.id -> getGeminiTranslation(sourceLanguage, targetLanguage, query)
                TranslationProvider.GROQ.id -> getGroqTranslation(sourceLanguage, targetLanguage, query)
                TranslationProvider.DEEPSEEK.id -> getDeepSeekTranslation(sourceLanguage, targetLanguage, query)
                TranslationProvider.HUGGINGFACE.id -> getHuggingFaceTranslation(sourceLanguage, targetLanguage, query)
                else -> throw Exception("Unknown provider")
            }
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it) }
        )
    }

    private suspend fun getGoogleTranslation(source: String, target: String, query: String): TranslationResponse {
        return safeCall {
            translationService.getTranslation(
                sourceLanguage = source,
                targetLanguage = target,
                query = query.preprocessText()
            )
        }
    }

    private suspend fun getChatGPTTranslation(source: String, target: String, query: String): TranslationResponse {
        val prompt = buildPrompt(source, target, query)
        val request = ChatGPTRequestBody(
            messages = listOf(
                ChatGPTMessage("system", "You are a professional translation assistant."),
                ChatGPTMessage("user", prompt)
            )
        )

        val response = safeCall { chatGPTService.translate(BuildConfig.OPENAI_API_KEY, request) }
        return convertToTranslationResponse(response.choices?.firstOrNull()?.message?.content, query)
    }

    private suspend fun getGeminiTranslation(source: String, target: String, query: String): TranslationResponse {
        val prompt = buildPrompt(source, target, query)
        val request = GeminiRequest(contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))))

        val response = safeCall { geminiService.translate(GeminiService.FLASH_MODEL, BuildConfig.GEMINI_API_KEY, request) }
        return convertToTranslationResponse(response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim(), query)
    }

    private suspend fun getGroqTranslation(source: String, target: String, query: String): TranslationResponse {
        val prompt = buildPrompt(source, target, query)
        val request = GroqRequestBody(
            messages = listOf(
                GroqMessage("system", "You are a professional translation assistant."),
                GroqMessage("user", prompt)
            )
        )

        val response = safeCall { groqService.translate(BuildConfig.GROQ_API_KEY, request) }
        return convertToTranslationResponse(response.choices?.firstOrNull()?.message?.content?.trim('"'), query)
    }

    private suspend fun getDeepSeekTranslation(source: String, target: String, query: String): TranslationResponse {
        val prompt = buildPrompt(source, target, query)
        val request = DeepSeekRequestBody(
            messages = listOf(
                DeepSeekMessage("system", "You are a professional translation assistant."),
                DeepSeekMessage("user", prompt)
            )
        )

        val response = safeCall { deepSeekService.translate(BuildConfig.DEEPSEEK_API_KEY, request) }
        return convertToTranslationResponse(response.choices?.firstOrNull()?.message?.content, query)
    }

    private suspend fun getHuggingFaceTranslation(source: String, target: String, query: String): TranslationResponse {
        val request = HuggingFaceRequestBody(
            inputs = query,
            parameters = HuggingFaceParameters(
                srcLang = mapLanguageCode(source),
                tgtLang = mapLanguageCode(target)
            )
        )

        val response = safeCall { huggingFaceService.translate(HuggingFaceService.DEFAULT_MODEL, BuildConfig.HUGGINGFACE_API_KEY, request) }
        return convertToTranslationResponse(response.firstOrNull()?.translationText?.trim(), query)
    }

    private suspend fun <T> safeCall(call: suspend () -> T): T {
        repeat(3) { attempt ->
            try {
                return call()
            } catch (e: Exception) {
                if (!shouldRetry(e) || attempt == 2) throw e
                delay(1000L * (attempt + 1))
            }
        }
        throw Exception("Max retries exceeded")
    }

    private fun shouldRetry(e: Exception): Boolean = when (e) {
        is SocketTimeoutException, is IOException -> true
        is HttpException -> e.code() >= 500
        else -> false
    }

    private fun buildPrompt(source: String, target: String, query: String): String =
        "Translate from $source to $target: \"$query\". Only provide the translation."

    private fun mapLanguageCode(code: String): String = when (code.lowercase()) {
        "en", "au" -> "en_XX"
        "ar" -> "ar_AR"
        "es" -> "es_XX"
        "fr" -> "fr_XX"
        "de" -> "de_DE"
        "it" -> "it_IT"
        "pt" -> "pt_XX"
        "ru" -> "ru_RU"
        "ja" -> "ja_XX"
        "ko" -> "ko_KR"
        "zh" -> "zh_CN"
        "hi" -> "hi_IN"
        else -> "en_XX"
    }

    private fun convertToTranslationResponse(translatedText: String?, originalText: String): TranslationResponse =
        TranslationResponse(
            sentences = listOf(
                Sentence(orig = originalText, trans = translatedText ?: "", translit = null)
            )
        )

    suspend fun saveTranslationEntry(entry: TranslationEntry) = withContext(Dispatchers.IO) { translationDao.insert(entry) }

    suspend fun getTranslationEntry(sourceText: String, sourceLang: String, targetLang: String): TranslationEntry? =
        translationDao.getTranslationEntry(sourceText, sourceLang, targetLang)

    fun getTranslationHistory(): Flow<List<TranslationEntry>> = translationDao.getAllEntries()

    fun getBookmarkedTranslations(): Flow<List<TranslationEntry>> = translationDao.getBookmarkedEntries()

    suspend fun updateTranslationEntry(entry: TranslationEntry) = translationDao.update(entry)

    suspend fun deleteTranslationEntry(entry: TranslationEntry) = translationDao.delete(entry)

    suspend fun clearBookmarks() = translationDao.clearBookmarks()

    suspend fun deleteNonBookmarkedEntries() = translationDao.deleteNonBookmarkedEntries()
}