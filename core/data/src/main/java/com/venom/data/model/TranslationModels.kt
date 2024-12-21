package com.venom.data.model

import com.google.gson.annotations.SerializedName

/**
 * Main response model for translation API.
 */
data class TranslationResponse(
    val src: String = "",
    val spell: Map<String, String> = emptyMap(),
    val sentences: List<Sentence>? = null,
    val dict: List<DictionaryEntry>? = null,
    val confidence: Double = 0.0,
    @SerializedName("ld_result") val languageDetectionResult: LanguageDetectionResult? = null,
    @SerializedName("alternative_translations") val alternativeTranslations: List<AlternativeTranslation>? = null,
    val synsets: List<Synset>? = null,
    val definitions: List<Definition>? = null
)

data class TranslationUiState(
    val userInput: String = "",
    val sourceLanguage: String = "auto",
    val targetLanguage: String = "ar",
    val translationResult: TranslationResponse? = null,
    val isBookmarked: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isNetworkAvailable: Boolean = true
)

/**
 * Represents a sentence in the translation response.
 */
data class Sentence(
    val trans: String,
    val orig: String,
    val backend: Int,
    @SerializedName("model_specification") val modelSpecification: List<Map<String, Any>>,
    @SerializedName("translation_engine_debug_info") val translationEngineDebugInfo: List<TranslationEngineDebugInfo>,
    val translit: String?
)

/**
 * Contains debug information related to the translation model.
 */
data class TranslationEngineDebugInfo(
    @SerializedName("model_tracking") val modelTracking: ModelTracking
)

/**
 * Provides tracking information for the translation model.
 */
data class ModelTracking(
    @SerializedName("checkpoint_md5") val checkpointMd5: String,
    @SerializedName("launch_doc") val launchDoc: String
)

/**
 * Represents an entry in the dictionary.
 */
data class DictionaryEntry(
    val pos: String,
    val terms: List<String>,
    val entry: List<DictionaryTerm>,
    @SerializedName("base_form") val baseForm: String,
    @SerializedName("pos_enum") val posEnum: Int
)

/**
 * Details about a term in the dictionary.
 */
data class DictionaryTerm(
    val word: String,
    @SerializedName("reverse_translation") val reverseTranslation: List<String>,
    val score: Double
)

/**
 * Contains results from language detection.
 */
data class LanguageDetectionResult(
    val srclangs: List<String>,
    @SerializedName("srclangs_confidences") val srclangsConfidences: List<Double>,
    @SerializedName("extended_srclangs") val extendedSrclangs: List<String>
)

/**
 * Represents alternative translations for a source phrase.
 */
data class AlternativeTranslation(
    @SerializedName("src_phrase") val srcPhrase: String,
    val alternative: List<Alternative>,
    val srcunicodeoffsets: List<SrcUnicodeOffset>,
    @SerializedName("raw_src_segment") val rawSrcSegment: String,
    @SerializedName("start_pos") val startPos: Int,
    @SerializedName("end_pos") val endPos: Int
)

/**
 * Details about an alternative translation.
 */
data class Alternative(
    @SerializedName("word_postproc") val wordPostproc: String,
    @SerializedName("has_preceding_space") val hasPrecedingSpace: Boolean,
    @SerializedName("attach_to_next_token") val attachToNextToken: Boolean,
    val backends: List<Int>,
    @SerializedName("backend_infos") val backendInfos: List<BackendInfo>
)

/**
 * Information about a translation backend.
 */
data class BackendInfo(
    val backend: Int
)

/**
 * Represents the start and end offsets of a Unicode segment.
 */
data class SrcUnicodeOffset(
    val begin: Int, val end: Int
)

/**
 * Represents a set of synonyms.
 */
data class Synset(
    val pos: String,
    val entry: List<SynsetEntry>,
    @SerializedName("base_form") val baseForm: String,
    @SerializedName("pos_enum") val posEnum: Int
)

/**
 * Details about a synonym entry.
 */
data class SynsetEntry(
    val synonym: List<String>,
    @SerializedName("definition_id") val definitionId: String,
    @SerializedName("label_info") val labelInfo: LabelInfo?
)

/**
 * Information about the label for a synonym entry.
 */
data class LabelInfo(
    val register: List<String>
)

/**
 * Represents a definition.
 */
data class Definition(
    val pos: String,
    val entry: List<DefinitionEntry>,
    @SerializedName("base_form") val baseForm: String,
    @SerializedName("pos_enum") val posEnum: Int
)

/**
 * Details about a definition entry.
 */
data class DefinitionEntry(
    val gloss: String,
    @SerializedName("definition_id") val definitionId: String,
    val example: String?
)
