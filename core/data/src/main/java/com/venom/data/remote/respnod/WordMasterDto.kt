package com.venom.data.remote.respnod

/**
 * Response wrapper that matches the JSON schema structure
 */
data class WordEntriesResponse(
    val wordEntries: List<WordMasterDto>
)

/**
 * DTO representing individual word entry from Gemini API
 */
data class WordMasterDto(
    val id: Int?,
    val wordEn: String,
    val pos: String,
    val cefrLevel: String,
    val fromOxford: String?,
    val rank: Int?,
    val frequency: String?,
    val difficultyScore: String?,
    val phoneticUs: String,
    val phoneticUk: String?,
    val phoneticAr: String,
    val translit: String,
    val syllabify: String?,
    val definitionEn: String,
    val definitionAr: String?,
    val usageNote: String?,
    val category: String?,
    val primarySense: String?,
    val semanticTags: List<String>?,
    val register: String?,
    val mnemonicAr: String?,
    val examples: Map<String, String>?,
    val collocations: List<String>?,
    val synonyms: List<String>?,
    val antonyms: List<String>?,
    val relatedWords: RelatedWordsDto?,
    val wordFamily: WordFamilyDto?,
    val arabicAr: String,
    val frenchFr: String?,
    val germanDe: String?,
    val spanishEs: String?,
    val chineseZh: String?,
    val russianRu: String?,
    val portuguesePt: String?,
    val japaneseJa: String?,
    val italianIt: String?,
    val turkishTr: String?
)

data class WordFamilyDto(
    val noun: String?,
    val verb: String?,
    val adj: String?,
    val adv: String?
)

data class RelatedWordsDto(
    val en: List<String>?,
    val ar: List<String>?
)