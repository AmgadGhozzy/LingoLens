package com.venom.data.repo

import com.venom.data.BuildConfig
import com.venom.data.api.*
import com.venom.data.model.Sentence
import com.venom.data.model.TranslationResponse
import com.venom.utils.Extensions.preprocessText
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

class OnlineTranslationRepository @Inject constructor(
    private val translationService: TranslationService,
    private val chatGPTService: ChatGPTService,
    private val geminiService: GeminiService,
    private val groqService: GroqService,
    private val deepSeekService: DeepSeekService,
    private val huggingFaceService: HuggingFaceService
) : OnlineTranslationOperations {

    override suspend fun getGoogleTranslation(source: String, target: String, query: String): TranslationResponse {
        return safeCall {
            translationService.getTranslation(
                sourceLanguage = source,
                targetLanguage = target,
                query = query.preprocessText()
            )
        }
    }

    override suspend fun getChatGPTTranslation(source: String, target: String, query: String): TranslationResponse {
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

    override suspend fun getGeminiTranslation(source: String, target: String, query: String): TranslationResponse {
        val prompt = buildPrompt(source, target, query)
        val request = GeminiRequest(contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))))
        val response = safeCall { geminiService.translate(GeminiService.FLASH_MODEL, BuildConfig.GEMINI_API_KEY, request) }
        return convertToTranslationResponse(response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim(), query)
    }

    override suspend fun getGroqTranslation(source: String, target: String, query: String): TranslationResponse {
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

    override suspend fun getDeepSeekTranslation(source: String, target: String, query: String): TranslationResponse {
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

    override suspend fun getHuggingFaceTranslation(source: String, target: String, query: String): TranslationResponse {
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
}
