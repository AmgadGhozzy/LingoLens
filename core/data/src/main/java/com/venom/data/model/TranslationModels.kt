package com.venom.data.model

import com.google.gson.annotations.SerializedName

data class TranslationResponse(
    val src: String = "",
    val sentences: List<Sentence>? = null,
    val dict: List<DictionaryEntry>? = null,
    val synsets: List<Synset>? = null,
    val definitions: List<Definition>? = null
)

data class Sentence(
    val trans: String,
    val orig: String,
    val translit: String?
)

data class DictionaryEntry(
    val pos: String,
    val terms: List<String>,
    val entry: List<DictionaryTerm>,
    @SerializedName("base_form") val baseForm: String
)

data class DictionaryTerm(
    val word: String,
    @SerializedName("reverse_translation") val reverseTranslation: List<String>,
    val score: Double
)

data class Synset(
    val pos: String,
    val entry: List<SynsetEntry>,
    @SerializedName("base_form") val baseForm: String
)

data class SynsetEntry(
    val synonym: List<String>,
    @SerializedName("definition_id") val definitionId: String
)

data class Definition(
    val pos: String,
    val entry: List<DefinitionEntry>,
    @SerializedName("base_form") val baseForm: String
)

data class DefinitionEntry(
    val gloss: String,
    val example: String?
)