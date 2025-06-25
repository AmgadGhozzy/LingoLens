package com.venom.domain.model

data class TranslationResult(
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
    val timestamp: Long = System.currentTimeMillis(),
    val isBookmarked: Boolean = false,
    val isFromCache: Boolean = false,
    val sentences: List<TranslationSentence> = emptyList(),
    val dict: List<DictionaryEntry> = emptyList(),
    val synsets: List<Synset> = emptyList(),
    val definitionEntries: List<Definition> = emptyList()
)

data class TranslationSentence(
    val original: String,
    val translated: String,
    val transliteration: String? = null
)