package com.venom.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.venom.data.local.entity.UserProfileEntity

@Dao
interface UserProfileDao {

    @Upsert
    suspend fun upsert(entity: UserProfileEntity)

    @Query("SELECT * FROM userProfile WHERE userId = :userId")
    suspend fun getProfile(userId: String): UserProfileEntity?

    @Query("""
        UPDATE userProfile 
        SET totalXp = totalXp + :xp, level = :level, updatedAt = :now 
        WHERE userId = :userId
    """)
    suspend fun addXpAndUpdateLevel(userId: String, xp: Int, level: Int, now: Long)

    @Query("""
        UPDATE userProfile 
        SET currentStreak = :streak, lastActiveDate = :lastActive, 
            bestStreak = CASE WHEN :streak > bestStreak THEN :streak ELSE bestStreak END,
            updatedAt = :now 
        WHERE userId = :userId
    """)
    suspend fun updateStreak(userId: String, streak: Int, lastActive: String, now: Long)

    @Query("""
        UPDATE userProfile 
        SET streakFreezes = streakFreezes - 1, updatedAt = :now 
        WHERE userId = :userId AND streakFreezes > 0
    """)
    suspend fun consumeStreakFreeze(userId: String, now: Long)

    @Query("""
        UPDATE userProfile 
        SET totalWordsMastered = totalWordsMastered + 1, updatedAt = :now 
        WHERE userId = :userId
    """)
    suspend fun incrementMastered(userId: String, now: Long)

    @Query("""
        UPDATE userProfile 
        SET dailyGoalXp = :goal, updatedAt = :now 
        WHERE userId = :userId
    """)
    suspend fun updateDailyGoal(userId: String, goal: Int, now: Long)

    @Query("""
        UPDATE userProfile 
        SET totalWordsViewed = :wordsViewed, totalWordsMastered = :mastered, 
            totalSessionCount = :sessions, totalTimeMs = :timeMs, 
            totalDaysActive = :daysActive, updatedAt = :now 
        WHERE userId = :userId
    """)
    suspend fun updateAggregates(
        userId: String,
        wordsViewed: Int,
        mastered: Int,
        sessions: Int,
        timeMs: Long,
        daysActive: Int,
        now: Long
    )

    @Query("UPDATE userProfile SET userId = :toUserId WHERE userId = :fromUserId")
    suspend fun migrateUserId(fromUserId: String, toUserId: String)
}