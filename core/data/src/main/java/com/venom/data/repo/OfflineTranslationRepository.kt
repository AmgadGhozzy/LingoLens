package com.venom.data.repo

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.venom.data.mapper.TranslateMapper
import com.venom.domain.model.TranslationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OfflineTranslationRepository @Inject constructor() : OfflineTranslationOperations {

    override suspend fun getOfflineTranslation(sourceLang: String, targetLang: String, query: String): TranslationResult = withContext(Dispatchers.IO) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.fromLanguageTag(sourceLang) ?: TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.fromLanguageTag(targetLang) ?: TranslateLanguage.ARABIC)
            .build()
        val translator = Translation.getClient(options)

        return@withContext try {
            val translatedText = translator.translate(query).await()
            translator.close()
            TranslateMapper.mapAIProviderResponse(
                translatedText = translatedText,
                sourceText = query,
                sourceLang = sourceLang,
                targetLang = targetLang,
                providerId = "Offline"
            )
        } catch (e: Exception) {
            translator.close()
            throw e
        }
    }

    override suspend fun getDownloadedModels(): Set<String> = withContext(Dispatchers.IO) {
        val modelManager = RemoteModelManager.getInstance()
        val downloadedModels = modelManager.getDownloadedModels(TranslateRemoteModel::class.java).await()
        downloadedModels.map { model ->
            when (model) {
                is TranslateRemoteModel -> model.language
                else -> ""
            }
        }.filter { it.isNotEmpty() }.toSet()
    }

    override suspend fun downloadLanguageModel(langCode: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val remoteModel = TranslateRemoteModel.Builder(langCode).build()
            val conditions = DownloadConditions.Builder()
                .requireWifi()
                .build()
            val modelManager = RemoteModelManager.getInstance()
            modelManager.download(remoteModel, conditions).await()
            Unit
        }
    }

    override suspend fun deleteLanguageModel(langCode: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val remoteModel = TranslateRemoteModel.Builder(langCode).build()
            val modelManager = RemoteModelManager.getInstance()
            modelManager.deleteDownloadedModel(remoteModel).await()
            Unit
        }
    }
    override fun getAllModels(): List<String> = TranslateLanguage.getAllLanguages()

}