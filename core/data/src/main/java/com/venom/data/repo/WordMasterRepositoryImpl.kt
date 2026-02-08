package com.venom.data.repo

import com.venom.data.local.dao.UserWordProgressDao
import com.venom.data.local.dao.WordMasterDao
import com.venom.data.mapper.WordMasterEntityMapper
import com.venom.domain.model.CefrLevel
import com.venom.domain.model.WordLevels
import com.venom.domain.model.WordMaster
import com.venom.domain.repo.IWordMasterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordMasterRepositoryImpl @Inject constructor(
    private val wordMasterDao: WordMasterDao,
    private val progressDao: UserWordProgressDao,
    private val identityRepository: UserIdentityRepository
) : IWordMasterRepository {

    override suspend fun getWordById(id: Int): WordMaster? = withContext(Dispatchers.IO) {
        wordMasterDao.getWordById(id)?.let { WordMasterEntityMapper.toDomain(it) }
    }

    override suspend fun getWordsByIds(ids: List<Int>): List<WordMaster> =
        withContext(Dispatchers.IO) {
            wordMasterDao.getWordsByIds(ids).map { WordMasterEntityMapper.toDomain(it) }
        }

    override suspend fun getWordsByLevel(level: CefrLevel, limit: Int): List<WordMaster> =
        withContext(Dispatchers.IO) {
            wordMasterDao.getWordsByLevel(level.name, limit)
                .map { WordMasterEntityMapper.toDomain(it) }
        }

    override suspend fun getWordsByRankRange(
        minRank: Int,
        maxRank: Int,
        limit: Int
    ): List<WordMaster> =
        withContext(Dispatchers.IO) {
            wordMasterDao.getRandomWordsByRankRange(minRank, maxRank, limit)
                .map { WordMasterEntityMapper.toDomain(it) }
        }


    override suspend fun getLevelProgress(level: WordLevels): Float = withContext(Dispatchers.IO) {
        val userId = identityRepository.getCurrentUserId()
        val totalWords = wordMasterDao.getCountInRange(level.range.start, level.range.end)
        if (totalWords == 0) return@withContext 0f

        val seenWords = progressDao.getSeenCountInRange(userId, level.range.start, level.range.end)
        seenWords.toFloat() / totalWords.toFloat()
    }

    override suspend fun getLevelsProgress(): Map<String, Float> = withContext(Dispatchers.IO) {
        WordLevels.values().associate { level ->
            level.id to getLevelProgress(level)
        }
    }


    override suspend fun getWordCount(): Int = withContext(Dispatchers.IO) {
        wordMasterDao.getWordCount()
    }

    override suspend fun insertWords(words: List<WordMaster>) = withContext(Dispatchers.IO) {
        wordMasterDao.insertAll(WordMasterEntityMapper.toEntityList(words))
    }
    
    // ===== ENRICHMENT-AWARE IMPLEMENTATIONS =====
    
    override suspend fun getEnrichedWords(limit: Int): List<WordMaster> =
        withContext(Dispatchers.IO) {
            wordMasterDao.getEnrichedWords(limit).map { 
                WordMasterEntityMapper.toDomain(it) 
            }
        }
    
    override suspend fun getEnrichedWordsByRankRange(
        minRank: Int,
        maxRank: Int,
        limit: Int
    ): List<WordMaster> = withContext(Dispatchers.IO) {
        wordMasterDao.getEnrichedWordsByRankRange(minRank, maxRank, limit).map {
            WordMasterEntityMapper.toDomain(it)
        }
    }
    
    override suspend fun hasEnrichedWords(minCount: Int): Boolean =
        withContext(Dispatchers.IO) {
            wordMasterDao.getEnrichedCount() >= minCount
        }
}

