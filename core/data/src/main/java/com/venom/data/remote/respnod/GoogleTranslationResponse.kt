package com.venom.data.remote.respnod

import androidx.compose.runtime.Immutable
import com.google.gson.annotations.SerializedName

@Immutable
data class GoogleTranslationResponse(
    val sentences: List<Sentence>? = null,
    val dict: List<DictionaryEntry>? = null,
    val synsets: List<Synset>? = null,
    val definitions: List<Definition>? = null,
    @SerializedName("alternative_translations")
    val alternativeTranslations: List<AlternativeTranslation>? = null,
    val confidence: Double = 1.0,
    val src: String = "",
    val examples: Examples? = null
)

@Immutable
data class Sentence(
    val trans: String? = null,
    val orig: String? = null,
    val translit: String? = null,
    @SerializedName("src_translit")
    val srcTranslit: String? = null
)

@Immutable
data class DictionaryEntry(
    val pos: String,
    val terms: List<String>,
    val entry: List<DictionaryTerm>,
    @SerializedName("base_form")
    val baseForm: String? = null
)

@Immutable
data class DictionaryTerm(
    val word: String,
    @SerializedName("reverse_translation")
    val reverseTranslation: List<String> = emptyList(),
    val score: Double = 0.0
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
data class AlternativeTranslation(
    @SerializedName("src_phrase")
    val srcPhrase: String,
    val alternative: List<AlternativeOption>
)

@Immutable
data class AlternativeOption(
    @SerializedName("word_postproc")
    val wordPostproc: String,
    val score: Double = 0.0
)

@Immutable
data class Examples(
    val example: List<ExampleEntry>? = null
)

@Immutable
data class ExampleEntry(
    val text: String
)