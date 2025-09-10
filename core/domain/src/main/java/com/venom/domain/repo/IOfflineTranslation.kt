package com.venom.domain.repo

import com.venom.domain.model.TranslationResult

interface IOfflineTranslation {
    suspend fun getOfflineTranslation(sourceLang: String, targetLang: String, query: String): TranslationResult
    suspend fun getDownloadedModels(): Set<String>
    suspend fun downloadLanguageModel(langCode: String): Result<Unit>
    suspend fun deleteLanguageModel(langCode: String): Result<Unit>
    fun getAllModels(): List<String>
}
