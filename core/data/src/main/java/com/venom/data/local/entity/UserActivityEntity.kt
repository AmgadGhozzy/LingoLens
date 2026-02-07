package com.venom.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "userActivity",
    primaryKeys = ["userId", "dateKey"]
)
data class UserActivityEntity(
    val userId: String,
    val dateKey: String,
    val wordsViewed: Int = 0,
    val wordsSwipedRight: Int = 0,
    val wordsSwipedLeft: Int = 0,
    val wordsPracticed: Int = 0,
    val sessionCount: Int = 0,
    val totalTimeMs: Long = 0,
    val longestSessionMs: Long = 0,
    val recallSuccess: Int = 0,
    val recallFail: Int = 0,
    val productionSuccess: Int = 0,
    val perfectSessions: Int = 0,
    val masteredCount: Int = 0,
    val totalXpEarned: Int = 0,
    val dailyGoalTarget: Int = 50,
    val dailyGoalMet: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)