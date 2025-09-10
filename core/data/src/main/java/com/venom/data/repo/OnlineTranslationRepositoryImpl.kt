package com.venom.data.repo

import com.venom.data.BuildConfig
import com.venom.data.api.ChatGPTMessage
import com.venom.data.api.ChatGPTRequestBody
import com.venom.data.api.ChatGPTService
import com.venom.data.api.DeepSeekMessage
import com.venom.data.api.DeepSeekRequestBody
import com.venom.data.api.DeepSeekService
import com.venom.data.api.GeminiContent
import com.venom.data.api.GeminiPart
import com.venom.data.api.GeminiRequest
import com.venom.data.api.GeminiService
import com.venom.data.api.GoogleTranslateService
import com.venom.data.api.GroqMessage
import com.venom.data.api.GroqRequestBody
import com.venom.data.api.GroqService
import com.venom.data.api.HuggingFaceParameters
import com.venom.data.api.HuggingFaceRequestBody
import com.venom.data.api.HuggingFaceService
import com.venom.data.mapper.TranslateMapper
import com.venom.domain.model.TranslationResult
import com.venom.domain.repo.IOnlineTranslation
import com.venom.utils.Extensions.preprocessText
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

class OnlineTranslationRepositoryImpl @Inject constructor(
    private val translationService: GoogleTranslateService,
    private val chatGPTService: ChatGPTService,
    private val geminiService: GeminiService,
    private val groqService: GroqService,
    private val deepSeekService: DeepSeekService,
    private val huggingFaceService: HuggingFaceService
) : IOnlineTranslation {

    override suspend fun getGoogleTranslation(source: String, target: String, query: String): TranslationResult {
        return safeCall {
            val response = translationService.translateWithGoogle(
                sourceLanguage = source,
                targetLanguage = target,
                query = query.preprocessText()
            )
            val result = TranslateMapper.mapGoogleResponseToTranslationResult(response, query)
            result.copy(
                sourceLang = source,
                targetLang = target,
                providerId = "google"
            )
        }
    }

    override suspend fun getChatGPTTranslation(source: String, target: String, query: String): TranslationResult {
        val prompt = buildPrompt(source, target, query)
        val request = ChatGPTRequestBody(
            messages = listOf(
                ChatGPTMessage("system", "You are a professional translation assistant."),
                ChatGPTMessage("user", prompt)
            )
        )
        return safeCall {
            val response = chatGPTService.translate(BuildConfig.OPENAI_API_KEY, request)
            TranslateMapper.mapAIProviderResponse(
                translatedText = response.choices?.firstOrNull()?.message?.content,
                sourceText = query,
                sourceLang = source,
                targetLang = target,
                providerId = "chatgpt"
            )
        }
    }

    override suspend fun getGeminiTranslation(source: String, target: String, query: String): TranslationResult {
        val prompt = buildPrompt(source, target, query)
        val request = GeminiRequest(contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))))
        return safeCall {
            val response = geminiService.translate(GeminiService.FLASH_MODEL, BuildConfig.GEMINI_API_KEY, request)
            TranslateMapper.mapAIProviderResponse(
                translatedText = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text,
                sourceText = query,
                sourceLang = source,
                targetLang = target,
                providerId = "gemini"
            )
        }
    }

    override suspend fun getGroqTranslation(source: String, target: String, query: String): TranslationResult {
        val prompt = buildPrompt(source, target, query)
        val request = GroqRequestBody(
            messages = listOf(
                GroqMessage("system", "You are a professional translation assistant."),
                GroqMessage("user", prompt)
            )
        )
        return safeCall {
            val response = groqService.translate(BuildConfig.GROQ_API_KEY, request)
            TranslateMapper.mapAIProviderResponse(
                translatedText = response.choices?.firstOrNull()?.message?.content,
                sourceText = query,
                sourceLang = source,
                targetLang = target,
                providerId = "groq"
            )
        }
    }

    override suspend fun getDeepSeekTranslation(source: String, target: String, query: String): TranslationResult {
        val prompt = buildPrompt(source, target, query)
        val request = DeepSeekRequestBody(
            messages = listOf(
                DeepSeekMessage("system", "You are a professional translation assistant."),
                DeepSeekMessage("user", prompt)
            )
        )
        return safeCall {
            val response = deepSeekService.translate(BuildConfig.DEEPSEEK_API_KEY, request)
            TranslateMapper.mapAIProviderResponse(
                translatedText = response.choices?.firstOrNull()?.message?.content,
                sourceText = query,
                sourceLang = source,
                targetLang = target,
                providerId = "deepseek"
            )
        }
    }

    override suspend fun getHuggingFaceTranslation(source: String, target: String, query: String): TranslationResult {
        val request = HuggingFaceRequestBody(
            inputs = query,
            parameters = HuggingFaceParameters(
                srcLang = mapLanguageCode(source),
                tgtLang = mapLanguageCode(target)
            )
        )
        return safeCall {
            val response = huggingFaceService.translate(HuggingFaceService.DEFAULT_MODEL, BuildConfig.HUGGINGFACE_API_KEY, request)
            TranslateMapper.mapAIProviderResponse(
                translatedText = response.firstOrNull()?.translationText,
                sourceText = query,
                sourceLang = source,
                targetLang = target,
                providerId = "huggingface"
            )
        }
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
}