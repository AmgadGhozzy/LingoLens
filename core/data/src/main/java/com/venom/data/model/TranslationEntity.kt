package com.venom.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.venom.data.local.converters.Converters
import com.venom.domain.model.Definition
import com.venom.domain.model.DictionaryEntry
import com.venom.domain.model.DictionaryTerm
import com.venom.domain.model.IHistoryEntry
import com.venom.domain.model.Synset
import com.venom.domain.model.TranslationSentence

@Entity(
    tableName = "translations",
    indices = [
        Index("sourceText", "sourceLang", "targetLang", "providerId", unique = true),
        Index("timestamp"),
        Index("isBookmarked"),
        Index("providerId")
    ]
)
@TypeConverters(Converters::class)
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
    val sentences: List<TranslationSentence> = emptyList(),
    val dict: List<DictionaryEntry> = emptyList(),
    val synsets: List<Synset> = emptyList(),
    val definitionEntries: List<Definition> = emptyList(),
    val terms: List<DictionaryTerm> = emptyList(),
    val transliteration: String? = null,
    val allExamples: List<String> = emptyList(),
    val posTerms: Map<String, List<String>> = emptyMap(),
    val confidence: Double = 1.0,
    override val timestamp: Long = System.currentTimeMillis(),
    override val isBookmarked: Boolean = false
) : IHistoryEntry