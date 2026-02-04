package com.venom.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wordsMaster")
data class WordMasterEntity(
    @PrimaryKey val id: Int,
    val wordEn: String? = null,
    val pos: String? = null,
    val cefrLevel: String? = null,
    val fromOxford: Int? = 0,
    val rank: Int? = null,
    val frequency: Int? = null,
    val difficultyScore: Int? = null,
    
    // Phonetics
    val phoneticUs: String? = null,
    val phoneticUk: String? = null,
    val phoneticAr: String? = null,
    val syllabify: String? = null,
    val translit: String? = null,
    
    // Semantics
    val definitionEn: String? = null,
    val definitionAr: String? = null,
    val usageNote: String? = null,
    val primarySense: String? = null,
    val category: String? = null,
    val semanticTags: String? = null,
    val register: String? = "Neutral",
    val mnemonicAr: String? = null,
    
    // Relational (JSON)
    val wordFamily: String? = null,
    val synonyms: String? = null,
    val antonyms: String? = null,
    val examples: String? = null,
    val collocations: String? = null,
    val relatedWords: String? = null,
    
    // Localization
    val arabicAr: String? = null,
    val frenchFr: String? = null,
    val germanDe: String? = null,
    val spanishEs: String? = null,
    val chineseZh: String? = null,
    val russianRu: String? = null,
    val portuguesePt: String? = null,
    val japaneseJa: String? = null,
    val italianIt: String? = null,
    val turkishTr: String? = null,
    
    // Enrichment Tracking (AI Pipeline)
    val isEnriched: Boolean = false,
    val enrichedAt: Long? = null,
    val enrichmentVersion: String? = null,
    val enrichmentSource: String? = null,
    val enrichmentStatus: String? = "pending" // pending, success, failed, partial
)


