package com.venom.domain.repo

import com.venom.domain.model.TranslationResult

interface IOnlineTranslation {
    suspend fun getGoogleTranslation(source: String, target: String, query: String): TranslationResult
    suspend fun getChatGPTTranslation(source: String, target: String, query: String): TranslationResult
    suspend fun getGeminiTranslation(source: String, target: String, query: String): TranslationResult
    suspend fun getGroqTranslation(source: String, target: String, query: String): TranslationResult
    suspend fun getDeepSeekTranslation(source: String, target: String, query: String): TranslationResult
    suspend fun getHuggingFaceTranslation(source: String, target: String, query: String): TranslationResult
}
