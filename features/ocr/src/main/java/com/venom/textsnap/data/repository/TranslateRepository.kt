package com.venom.textsnap.data.repository

import com.venom.textsnap.data.api.TranslationApi
import com.venom.textsnap.data.model.TranslationResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class TranslateRepository(private val translationApi: TranslationApi) {
    suspend fun getTranslation(
        sourceLanguage: String, targetLanguage: String, query: String
    ): Result<TranslationResponse> = withContext(Dispatchers.IO) {
        try {
            val encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString())
            val response = translationApi.getTranslation(
                sourceLanguage = sourceLanguage,
                targetLanguage = targetLanguage,
                query = encodedQuery
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(Exception("Translation failed: ${e.message}", e))
        }
    }
}