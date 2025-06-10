package com.venom.data.repo

import com.venom.data.model.TranslationResponse

interface OfflineTranslationOperations {
    suspend fun getOfflineTranslation(sourceLang: String, targetLang: String, query: String): TranslationResponse
    suspend fun getDownloadedModels(): Set<String>
    suspend fun downloadLanguageModel(langCode: String): Result<Unit>
    suspend fun deleteLanguageModel(langCode: String): Result<Unit>
    fun getAllModels(): List<String>
}
