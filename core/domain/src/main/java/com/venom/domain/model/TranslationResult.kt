package com.venom.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class TranslationResult(
    val id: Long = 0,
    val sourceText: String = "",
    val translatedText: String = "",
    val sourceLang: String = "",
    val targetLang: String = "",
    val providerId: String = "",
    val alternatives: List<String> = emptyList(),
    val synonyms: List<String> = emptyList(),
    val definitions: List<String> = emptyList(),
    val examples: List<String> = emptyList(),
    val confidence: Double = 1.0,
    override val timestamp: Long = System.currentTimeMillis(),
    override val isBookmarked: Boolean = false,
    val isFromCache: Boolean = false,
    val sentences: List<TranslationSentence> = emptyList(),
    val dict: List<DictionaryEntry> = emptyList(),
    val synsets: List<Synset> = emptyList(),
    val definitionEntries: List<Definition> = emptyList(),
    val terms: List<DictionaryTerm> = emptyList(),
    val transliteration: String? = null,
    val allExamples: List<String> = emptyList(),
    val posTerms: Map<String, List<String>> = emptyMap()
) : IHistoryEntry

@Immutable
data class TranslationSentence(
    val original: String,
    val translated: String,
    val transliteration: String? = null
)