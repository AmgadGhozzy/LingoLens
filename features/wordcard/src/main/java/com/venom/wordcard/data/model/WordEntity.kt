package com.venom.wordcard.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val englishWord: String,
    val arabicWord: String,
    val ranking: Int,
    val isBookmarked: Boolean = false,
    val isRemembered: Boolean = false,
    val isForgotten: Boolean = false,
    val synonyms: List<String> = emptyList()
)
