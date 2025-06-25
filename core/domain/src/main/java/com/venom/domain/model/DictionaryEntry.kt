package com.venom.domain.model

data class DictionaryEntry(
    val pos: String,
    val terms: List<String>,
    val entry: List<DictionaryTerm>
)

data class DictionaryTerm(
    val word: String,
    val reverseTranslation: List<String> = emptyList(),
    val score: Double = 0.0
)

data class Definition(
    val pos: String,
    val entry: List<DefinitionEntry>
)

data class DefinitionEntry(
    val gloss: String,
    val example: String? = null
)

data class Synset(
    val pos: String,
    val entry: List<SynsetEntry>
)

data class SynsetEntry(
    val synonym: List<String>
)