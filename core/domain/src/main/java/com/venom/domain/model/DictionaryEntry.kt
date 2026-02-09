package com.venom.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class DictionaryEntry(
    val pos: String,
    val terms: List<String>,
    val entry: List<DictionaryTerm>
)

@Immutable
data class DictionaryTerm(
    val word: String,
    val reverseTranslation: List<String> = emptyList(),
    val score: Double = 0.0
)

@Immutable
data class Definition(
    val pos: String,
    val entry: List<DefinitionEntry>
)

@Immutable
data class DefinitionEntry(
    val gloss: String,
    val example: String? = null
)

@Immutable
data class Synset(
    val pos: String,
    val entry: List<SynsetEntry>
)

@Immutable
data class SynsetEntry(
    val synonym: List<String>
)