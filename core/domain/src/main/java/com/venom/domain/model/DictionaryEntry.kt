package com.venom.domain.model

import androidx.compose.runtime.Immutable
import com.squareup.moshi.JsonClass

@Immutable
@JsonClass(generateAdapter = true)
data class DictionaryEntry(
    val pos: String,
    val terms: List<String>,
    val entry: List<DictionaryTerm>
)

@Immutable
@JsonClass(generateAdapter = true)
data class DictionaryTerm(
    val word: String,
    val reverseTranslation: List<String> = emptyList(),
    val score: Double = 0.0
)

@Immutable
@JsonClass(generateAdapter = true)
data class Definition(
    val pos: String,
    val entry: List<DefinitionEntry>
)

@Immutable
@JsonClass(generateAdapter = true)
data class DefinitionEntry(
    val gloss: String,
    val example: String? = null
)

@Immutable
@JsonClass(generateAdapter = true)
data class Synset(
    val pos: String,
    val entry: List<SynsetEntry>
)

@Immutable
@JsonClass(generateAdapter = true)
data class SynsetEntry(
    val synonym: List<String>
)