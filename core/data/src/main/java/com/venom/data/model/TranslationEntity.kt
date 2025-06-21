
package com.venom.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.venom.data.local.converters.StringListConverter
import com.venom.domain.model.IHistoryEntry

@Entity(
    tableName = "translations",
    indices = [
        Index("sourceText", "sourceLang", "targetLang", "providerId", unique = true),
        Index("timestamp"),
        Index("isBookmarked"),
        Index("providerId")
    ]
)
@TypeConverters(StringListConverter::class)
data class TranslationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sourceText: String,
    val translatedText: String,
    val sourceLang: String,
    val targetLang: String,
    val providerId: String,
    val alternatives: List<String> = emptyList(),
    val synonyms: List<String> = emptyList(),
    val definitions: List<String> = emptyList(),
    val examples: List<String> = emptyList(),
    val confidence: Double = 1.0,
    override val timestamp: Long = System.currentTimeMillis(),
    override val isBookmarked: Boolean = false
) : IHistoryEntry