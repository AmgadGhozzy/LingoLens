package com.venom.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userProfile")
data class UserProfileEntity(
    @PrimaryKey
    val userId: String,

    // XP & Level
    val totalXp: Int = 0,
    val level: Int = 1,

    // Streak
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val lastActiveDate: String = "",// "yyyy-MM-dd"
    val streakFreezes: Int = 1,

    // Aggregates
    val totalWordsViewed: Int = 0,
    val totalWordsMastered: Int = 0,
    val totalSessionCount: Int = 0,
    val totalTimeMs: Long = 0,
    val totalDaysActive: Int = 0,

    // Settings
    val dailyGoalXp: Int = 50,// User's chosen daily goal

    // Meta
    val joinedAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)