package com.venom.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userProfile")
data class UserProfileEntity(
    @PrimaryKey
    val userId: String,

    val totalXp: Int = 0,

    // Streak
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val lastActiveDate: String = "",
    val streakFreezes: Int = 1,

    // Settings
    val dailyGoalXp: Int = 50,

    // Placement
    val placementUnit: Int? = null,
    val placementCefr: String? = null,
    val placementTakenAt: Long? = null,

    // Meta
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)