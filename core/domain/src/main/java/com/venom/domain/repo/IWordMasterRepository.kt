package com.venom.domain.repo

import com.venom.domain.model.CefrLevel
import com.venom.domain.model.WordLevels
import com.venom.domain.model.WordMaster

interface IWordMasterRepository {
    
    suspend fun getWordById(id: Int): WordMaster?
    
    suspend fun getWordsByIds(ids: List<Int>): List<WordMaster>
    
    suspend fun getWordsByLevel(level: CefrLevel, limit: Int = 50): List<WordMaster>
    
    suspend fun getWordsByRankRange(minRank: Int, maxRank: Int, limit: Int = 10): List<WordMaster>
    
    suspend fun getLevelProgress(level: WordLevels): Float
    
    suspend fun getLevelsProgress(): Map<String, Float>
    
    suspend fun getWordCount(): Int
    
    suspend fun insertWords(words: List<WordMaster>)

    suspend fun getEnrichedWords(limit: Int = 100): List<WordMaster>

    suspend fun getEnrichedWordsByRankRange(
        minRank: Int,
        maxRank: Int,
        limit: Int = 10
    ): List<WordMaster>

    suspend fun hasEnrichedWords(minCount: Int = 10): Boolean
}


