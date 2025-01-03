package com.venom.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.venom.domain.model.IHistoryEntry

@Entity(
    tableName = "translation_history", indices = [Index(
        value = ["sourceText", "translatedText", "sourceLangName", "targetLangName"],
        unique = true
    )]
)
data class TranslationEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceText: String,
    val translatedText: String,
    val sourceLangName: String,
    val targetLangName: String,
    val sourceLangCode: String,
    val targetLangCode: String,
    val synonyms: List<String>? = null,
    override val timestamp: Long = System.currentTimeMillis(),
    override val isBookmarked: Boolean = false
) : IHistoryEntry
