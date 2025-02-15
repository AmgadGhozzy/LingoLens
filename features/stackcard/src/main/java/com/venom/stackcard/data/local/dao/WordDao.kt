package com.venom.stackcard.data.local.dao

import androidx.room.*
import com.venom.stackcard.data.model.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words")
    fun getAllWords(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words ORDER BY ranking LIMIT 10")
    fun get10Words(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE isRemembered = 0 AND isForgotten = 0 ORDER BY ranking LIMIT 10")
    fun get10UnseenWords(): Flow<List<WordEntity>>

    @Query("""
    SELECT * FROM words 
    WHERE ranking BETWEEN :minRank AND :maxRank
    AND (:includeRemembered = 1 OR isRemembered = 0)
    AND (:includeForgotten = 1 OR isForgotten = 0)
    ORDER BY 
        CASE 
            WHEN :orderBy = 'RANDOM' THEN RANDOM()
            WHEN :orderBy = 'RANKING' THEN ranking
        END
    LIMIT :pageSize
""")
    suspend fun getWordsFromLevel(
        minRank: Int,
        maxRank: Int,
        includeRemembered: Boolean = false,
        includeForgotten: Boolean = false,
        orderBy: String = "RANKING",
        pageSize: Int = 10
    ): List<WordEntity>

    @Query("SELECT * FROM words WHERE isBookmarked = 1")
    fun getBookmarkedWords(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE isRemembered = 1")
    fun getRememberedWords(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE isForgotten = 1")
    fun getForgotedWords(): Flow<List<WordEntity>>

    @Update
    suspend fun update(word: WordEntity)
}
