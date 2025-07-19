package com.venom.data.model

import com.google.gson.annotations.SerializedName

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

data class Sentence(
    val trans: String? = null,
    val orig: String? = null,
    val translit: String? = null,
    @SerializedName("src_translit")
    val srcTranslit: String? = null
)

data class DictionaryEntry(
    val pos: String,
    val terms: List<String>,
    val entry: List<DictionaryTerm>,
    @SerializedName("base_form")
    val baseForm: String? = null
)

data class DictionaryTerm(
    val word: String,
    @SerializedName("reverse_translation")
    val reverseTranslation: List<String> = emptyList(),
    val score: Double = 0.0
)

data class Synset(
    val pos: String,
    val entry: List<SynsetEntry>
)

data class SynsetEntry(
    val synonym: List<String>
)

data class Definition(
    val pos: String,
    val entry: List<DefinitionEntry>
)

data class DefinitionEntry(
    val gloss: String,
    val example: String? = null
)

data class AlternativeTranslation(
    @SerializedName("src_phrase")
    val srcPhrase: String,
    val alternative: List<AlternativeOption>
)

data class AlternativeOption(
    @SerializedName("word_postproc")
    val wordPostproc: String,
    val score: Double = 0.0
)

data class Examples(
    val example: List<ExampleEntry>? = null
)

data class ExampleEntry(
    val text: String
)