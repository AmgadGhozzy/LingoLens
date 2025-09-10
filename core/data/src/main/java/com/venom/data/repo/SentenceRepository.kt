package com.venom.data.repo

import com.venom.data.api.SentenceService
import com.venom.data.cache.SentenceCacheManager
import com.venom.data.remote.respnod.NetworkResult
import com.venom.data.remote.respnod.SentenceResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SentenceRepository @Inject constructor(
    private val sentenceService: SentenceService,
    private val cacheManager: SentenceCacheManager
) {
    fun getSentences(
        word: String,
        limit: Int? = null,
        source: String = "all"
    ): Flow<NetworkResult<SentenceResponse>> = flow {
        val normalizedWord = word.trim().lowercase()
        val normalizedSource = source.lowercase()

        // Create cache key that includes both limit and source
        val cacheKey = buildCacheKey(normalizedWord, limit, normalizedSource)

        // Check cache first
        cacheManager.getCachedResponse(cacheKey)?.let {
            emit(NetworkResult.Success(it))
            return@flow
        }

        emit(NetworkResult.Loading())

        runCatching {
            sentenceService.getSentences(normalizedWord, limit, normalizedSource)
        }.onSuccess { response ->
            response.body()?.let { sentenceResponse ->
                cacheManager.cacheResponse(cacheKey, sentenceResponse)
                emit(NetworkResult.Success(sentenceResponse))
            } ?: emit(NetworkResult.Error("No data received"))
        }.onFailure { throwable ->
            emit(NetworkResult.Error(throwable.message ?: "Network error"))
        }
    }

    /**
     * Builds a consistent cache key that includes word, limit, and source
     */
    private fun buildCacheKey(word: String, limit: Int?, source: String): String {
        val limitPart = when (limit) {
            null -> "default"
            else -> limit.toString()
        }
        return "${word}_${limitPart}_${source}"
    }

    suspend fun clearCache() = cacheManager.clearCache()
    suspend fun getCacheInfo() = cacheManager.getCachedWordsCount()
}