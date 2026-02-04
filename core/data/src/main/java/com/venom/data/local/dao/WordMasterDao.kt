package com.venom.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.venom.data.local.entity.WordMasterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordMasterDao {
    
    @Query("SELECT * FROM wordsMaster ORDER BY rank ASC")
    fun getAllWords(): Flow<List<WordMasterEntity>>
    
    @Query("SELECT * FROM wordsMaster WHERE id = :id")
    suspend fun getWordById(id: Int): WordMasterEntity?
    
    @Query("SELECT * FROM wordsMaster WHERE id IN (:ids)")
    suspend fun getWordsByIds(ids: List<Int>): List<WordMasterEntity>
    
    @Query("SELECT * FROM wordsMaster WHERE cefrLevel = :level ORDER BY rank ASC LIMIT :limit")
    suspend fun getWordsByLevel(level: String, limit: Int = 50): List<WordMasterEntity>

    @Query("SELECT * FROM wordsMaster WHERE rank BETWEEN :minRank AND :maxRank ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomWordsByRankRange(minRank: Int, maxRank: Int, limit: Int): List<WordMasterEntity>

    
    // Get unseen words by rank priority (for new word selection)
    @Query("""
        SELECT * FROM wordsMaster 
        WHERE id NOT IN (:seenIds)
        ORDER BY rank ASC
        LIMIT :limit
    """)
    suspend fun getUnseenByRank(seenIds: List<Int>, limit: Int = 20): List<WordMasterEntity>
    
    @Query("SELECT COUNT(*) FROM wordsMaster")
    suspend fun getWordCount(): Int

    @Query("SELECT COUNT(*) FROM wordsMaster WHERE rank BETWEEN :minRank AND :maxRank")
    suspend fun getCountInRange(minRank: Int, maxRank: Int): Int

    // ===== ENRICHMENT PIPELINE QUERIES =====
    /**
     * Get batch of un-enriched words for AI processing.
     * Returns random unenriched words.
     */
    @Query("""
        SELECT * FROM wordsMaster
        WHERE isEnriched = 0 OR isEnriched IS NULL
        ORDER BY RANDOM()
        LIMIT :batchSize
    """)
    suspend fun getUnenrichedBatch(batchSize: Int = 20): List<WordMasterEntity>
    
    /**
     * Get count of enriched words.
     */
    @Query("SELECT COUNT(*) FROM wordsMaster WHERE isEnriched = 1")
    suspend fun getEnrichedCount(): Int
    
    /**
     * Get count of pending/failed enrichments.
     */
    @Query("SELECT COUNT(*) FROM wordsMaster WHERE enrichmentStatus = :status")
    suspend fun getCountByEnrichmentStatus(status: String): Int
    
    /**
     * Get only enriched words (for UI display).
     */
    @Query("""
        SELECT * FROM wordsMaster
        WHERE isEnriched = 1
        ORDER BY RANDOM()
        LIMIT :limit
    """)
    suspend fun getEnrichedWords(limit: Int = 100): List<WordMasterEntity>
    
    /**
     * Get enriched words by rank range (for level-based learning).
     */
    @Query("""
        SELECT * FROM wordsMaster 
        WHERE isEnriched = 1 AND rank BETWEEN :minRank AND :maxRank
        ORDER BY RANDOM()
        LIMIT :limit
    """)
    suspend fun getEnrichedWordsByRankRange(minRank: Int, maxRank: Int, limit: Int): List<WordMasterEntity>
    
    /**
     * Update enrichment status for a single word.
     */
    @Query("""
        UPDATE wordsMaster 
        SET isEnriched = :isEnriched,
            enrichedAt = :enrichedAt,
            enrichmentVersion = :version,
            enrichmentSource = :source,
            enrichmentStatus = :status
        WHERE id = :wordId
    """)
    suspend fun updateEnrichmentStatus(
        wordId: Int,
        isEnriched: Boolean,
        enrichedAt: Long,
        version: String,
        source: String,
        status: String
    )
    
    /**
     * Batch update enrichment status to 'pending' before AI call.
     */
    @Query("""
        UPDATE wordsMaster 
        SET enrichmentStatus = 'pending'
        WHERE id IN (:wordIds)
    """)
    suspend fun markAsPending(wordIds: List<Int>)
    
    /**
     * Mark failed enrichments for retry.
     */
    @Query("""
        UPDATE wordsMaster 
        SET enrichmentStatus = 'failed'
        WHERE id IN (:wordIds)
    """)
    suspend fun markAsFailed(wordIds: List<Int>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<WordMasterEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(word: WordMasterEntity)
    
    @Query("UPDATE wordsMaster SET enrichmentStatus = 'pending', isEnriched = 0 WHERE enrichmentVersion != :version")
    suspend fun resetOutdatedEnrichments(version: String)
}

