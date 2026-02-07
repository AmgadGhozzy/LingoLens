package com.venom.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.venom.data.local.entity.UserWordProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserWordProgressDao {
    
    @Query("SELECT * FROM userWordProgress WHERE userId = :userId AND wordId = :wordId")
    suspend fun getProgress(userId: String, wordId: Int): UserWordProgressEntity?
    
    @Query("SELECT * FROM userWordProgress WHERE userId = :userId")
    fun getAllProgress(userId: String): Flow<List<UserWordProgressEntity>>
    
    // SRS: Get due words for review
    @Query("""
        SELECT * FROM userWordProgress 
        WHERE userId = :userId 
          AND nextReview IS NOT NULL 
          AND nextReview <= :now
        ORDER BY nextReview ASC
        LIMIT :limit
    """)
    suspend fun getDueWords(userId: String, now: Long, limit: Int = 20): List<UserWordProgressEntity>
    
    // Get high-lapse words needing reinforcement
    @Query("""
        SELECT * FROM userWordProgress 
        WHERE userId = :userId 
          AND lapsesCount > 2 
          AND knownState != 'MASTERED'
        ORDER BY lapsesCount DESC
        LIMIT :limit
    """)
    suspend fun getHighLapseWords(userId: String, limit: Int = 10): List<UserWordProgressEntity>
    
    // Get bookmarked words
    @Query("SELECT * FROM userWordProgress WHERE userId = :userId AND bookmarked = 1")
    fun getBookmarkedWords(userId: String): Flow<List<UserWordProgressEntity>>
    
    // Get word IDs user has seen
    @Query("SELECT * FROM userWordProgress WHERE userId = :userId")
    suspend fun getAllProgressSnapshot(userId: String): List<UserWordProgressEntity>

    @Query("SELECT wordId FROM userWordProgress WHERE userId = :userId")
    suspend fun getSeenWordIds(userId: String): List<Int>

    @Query("UPDATE userWordProgress SET userId = :newUserId WHERE userId = :oldUserId")
    suspend fun migrateUserId(oldUserId: String, newUserId: String)

    @Query("SELECT COUNT(*) FROM userWordProgress WHERE userId = :userId")
    suspend fun getProgressCount(userId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(progress: UserWordProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(progressList: List<UserWordProgressEntity>)
    
    @Update
    suspend fun update(progress: UserWordProgressEntity)
    
    // Increment view count
    @Query("""
        UPDATE userWordProgress 
        SET viewCount = viewCount + 1, lastViewed = :timestamp 
        WHERE userId = :userId AND wordId = :wordId
    """)
    suspend fun incrementViewCount(userId: String, wordId: Int, timestamp: Long)
    
    // Toggle bookmark
    @Query("UPDATE userWordProgress SET bookmarked = :bookmarked WHERE userId = :userId AND wordId = :wordId")
    suspend fun setBookmarked(userId: String, wordId: Int, bookmarked: Boolean)

    @Query("""
        SELECT COUNT(*) FROM userWordProgress 
        JOIN wordsMaster ON userWordProgress.wordId = wordsMaster.id
        WHERE userWordProgress.userId = :userId 
          AND wordsMaster.rank BETWEEN :minRank AND :maxRank
    """)
    suspend fun getSeenCountInRange(userId: String, minRank: Int, maxRank: Int): Int

}
