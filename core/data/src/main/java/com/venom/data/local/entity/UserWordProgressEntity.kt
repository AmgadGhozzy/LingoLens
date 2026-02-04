package com.venom.data.local.entity

import androidx.room.Entity

/**
 * Room entity for tracking user's learning progress per word.
 * Composite primary key: (userId, wordId)
 */
@Entity(
    tableName = "userWordProgress",
    primaryKeys = ["userId", "wordId"]
)
data class UserWordProgressEntity(
    val userId: String,
    val wordId: Int,
    
    // Exposure
    val viewCount: Int = 0,
    val lastViewed: Long? = null,
    
    // Interaction
    val swipeRightCount: Int = 0,
    val swipeLeftCount: Int = 0,
    val bookmarked: Boolean = false,
    
    // Practice
    val practiceCompleted: Int = 0,
    val productionSuccess: Int = 0,
    val recallSuccess: Int = 0,
    val recallFail: Int = 0,
    
    // Learning state
    val knownState: String = "NEW",
    val difficultyState: String = "MEDIUM",
    
    // SRS
    val stability: Float = 0f,
    val lapsesCount: Int = 0,
    val lastReview: Long? = null,
    val nextReview: Long? = null
)
