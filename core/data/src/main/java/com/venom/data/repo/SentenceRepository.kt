package com.venom.data.repo

import com.venom.data.api.SentenceService
import com.venom.data.cache.SentenceCacheManager
import com.venom.data.model.NetworkResult
import com.venom.data.model.SentenceResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SentenceRepository @Inject constructor(
    private val sentenceService: SentenceService,
    private val cacheManager: SentenceCacheManager
) {
    fun getSentences(word: String): Flow<NetworkResult<SentenceResponse>> = flow {
        val normalizedWord = word.trim().lowercase()

        cacheManager.getCachedResponse(normalizedWord)?.let {
            emit(NetworkResult.Success(it))
            return@flow
        }

        emit(NetworkResult.Loading())

        runCatching {
            sentenceService.getSentences(normalizedWord)
        }.onSuccess { response ->
            response.body()?.let { sentenceResponse ->
                cacheManager.cacheResponse(normalizedWord, sentenceResponse)
                emit(NetworkResult.Success(sentenceResponse))
            } ?: emit(NetworkResult.Error("No data received"))
        }.onFailure {
            emit(NetworkResult.Error(it.message ?: "Network error"))
        }
    }

    suspend fun clearCache() = cacheManager.clearCache()
    suspend fun getCacheInfo() = cacheManager.getCachedWordsCount()
}