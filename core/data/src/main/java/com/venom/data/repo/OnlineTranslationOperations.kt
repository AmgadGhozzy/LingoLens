package com.venom.data.repo

import com.venom.data.model.TranslationResponse

interface OnlineTranslationOperations {
    suspend fun getGoogleTranslation(source: String, target: String, query: String): TranslationResponse
    suspend fun getChatGPTTranslation(source: String, target: String, query: String): TranslationResponse
    suspend fun getGeminiTranslation(source: String, target: String, query: String): TranslationResponse
    suspend fun getGroqTranslation(source: String, target: String, query: String): TranslationResponse
    suspend fun getDeepSeekTranslation(source: String, target: String, query: String): TranslationResponse
    suspend fun getHuggingFaceTranslation(source: String, target: String, query: String): TranslationResponse
}
