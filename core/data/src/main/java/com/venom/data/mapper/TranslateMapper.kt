package com.venom.data.mapper

import com.venom.data.model.AlternativeTranslation
import com.venom.data.model.Examples
import com.venom.data.model.GoogleTranslationResponse
import com.venom.data.model.TranslationEntity
import com.venom.domain.model.Definition
import com.venom.domain.model.DefinitionEntry
import com.venom.domain.model.DictionaryEntry
import com.venom.domain.model.DictionaryTerm
import com.venom.domain.model.Synset
import com.venom.domain.model.SynsetEntry
import com.venom.domain.model.TranslationResult
import com.venom.domain.model.TranslationSentence

object TranslateMapper {

    fun mapGoogleResponseToTranslationResult(
        response: GoogleTranslationResponse,
        sourceText: String?
    ): TranslationResult {
        val safeSourceText = sourceText?.takeIf { it.isNotBlank() } ?: ""
        val sentences = response.sentences?.map { sentence ->
            TranslationSentence(
                original = sentence.orig?.takeIf { it.isNotBlank() } ?: safeSourceText,
                translated = sentence.trans ?: "",
                transliteration = sentence.translit
            )
        } ?: emptyList()

        val translatedText = response.sentences?.firstOrNull()?.trans ?: ""
        val mainTransliteration = response.sentences?.firstOrNull()?.translit

        return TranslationResult(
            sourceText = safeSourceText,
            translatedText = translatedText,
            sourceLang = response.src,
            targetLang = "",
            providerId = "google",
            confidence = response.confidence,
            sentences = sentences,
            alternatives = extractAlternatives(response.alternativeTranslations),
            synonyms = extractSynonyms(response.synsets),
            definitions = extractDefinitions(response.definitions),
            examples = extractExamples(response.examples),
            dict = mapDictionaryEntries(response.dict ?: emptyList()),
            synsets = mapSynsets(response.synsets ?: emptyList()),
            definitionEntries = mapDefinitions(response.definitions ?: emptyList()),
            // Extract new fields
            terms = extractAllTerms(response.dict),
            transliteration = mainTransliteration,
            allExamples = extractAllExamples(response.examples),
            posTerms = extractPosTerms(response.dict)
        )
    }

    fun mapAIProviderResponse(
        translatedText: String?,
        sourceText: String,
        sourceLang: String,
        targetLang: String,
        providerId: String
    ): TranslationResult {
        return TranslationResult(
            sourceText = sourceText,
            translatedText = translatedText?.trim('"', ' ', '\n') ?: "",
            sourceLang = sourceLang,
            targetLang = targetLang,
            providerId = providerId
        )
    }

    private fun mapDictionaryEntries(dict: List<com.venom.data.model.DictionaryEntry>): List<DictionaryEntry> {
        return dict.map { entry ->
            DictionaryEntry(
                pos = entry.pos,
                terms = entry.terms,
                entry = entry.entry.map { term ->
                    DictionaryTerm(
                        word = term.word,
                        reverseTranslation = term.reverseTranslation,
                        score = term.score
                    )
                }
            )
        }
    }

    private fun mapSynsets(synsets: List<com.venom.data.model.Synset>): List<Synset> {
        return synsets.map { synset ->
            Synset(
                pos = synset.pos,
                entry = synset.entry.map { entry ->
                    SynsetEntry(
                        synonym = entry.synonym
                    )
                }
            )
        }
    }

    private fun mapDefinitions(definitions: List<com.venom.data.model.Definition>): List<Definition> {
        return definitions.map { definition ->
            Definition(
                pos = definition.pos,
                entry = definition.entry.map { entry ->
                    DefinitionEntry(
                        gloss = entry.gloss,
                        example = entry.example
                    )
                }
            )
        }
    }

    private fun extractAlternatives(alternatives: List<AlternativeTranslation>?): List<String> {
        return alternatives?.flatMap { alternative ->
            alternative.alternative.mapNotNull { alt ->
                alt.wordPostproc.takeIf { it.isNotBlank() }
            }
        }?.distinct() ?: emptyList()
    }

    private fun extractSynonyms(synsets: List<com.venom.data.model.Synset>?): List<String> {
        return synsets?.flatMap { synset ->
            synset.entry.flatMap { entry ->
                entry.synonym.filter { it.isNotBlank() }
            }
        }?.distinct() ?: emptyList()
    }

    private fun extractDefinitions(definitions: List<com.venom.data.model.Definition>?): List<String> {
        return definitions?.flatMap { def ->
            def.entry.mapNotNull { entry ->
                val gloss = entry.gloss.takeIf { it.isNotBlank() }
                val pos = def.pos.takeIf { it.isNotBlank() }

                if (gloss != null) {
                    buildString {
                        if (pos != null) append("($pos) ")
                        append(gloss)
                        entry.example?.takeIf { it.isNotBlank() }?.let {
                            append(" - Example: $it")
                        }
                    }
                } else null
            }
        } ?: emptyList()
    }

    private fun extractExamples(examples: Examples?): List<String> {
        return examples?.example?.mapNotNull {
            it.text.takeIf { text -> text.isNotBlank() }
        } ?: emptyList()
    }

    // New extraction methods

    private fun extractAllTerms(dict: List<com.venom.data.model.DictionaryEntry>?): List<DictionaryTerm> {
        return dict?.flatMap { entry ->
            entry.entry.map { term ->
                DictionaryTerm(
                    word = term.word,
                    reverseTranslation = term.reverseTranslation,
                    score = term.score
                )
            }
        } ?: emptyList()
    }

    private fun extractAllExamples(examples: Examples?): List<String> {
        return examples?.example?.mapNotNull { example ->
            example.text.takeIf { it.isNotBlank() }
        } ?: emptyList()
    }

    private fun extractPosTerms(dict: List<com.venom.data.model.DictionaryEntry>?): Map<String, List<String>> {
        return dict?.associate { entry ->
            val pos = entry.pos
            val terms = entry.terms
            pos to terms
        } ?: emptyMap()
    }

    // Updated entity mapping methods with all fields
    fun toEntity(result: TranslationResult): TranslationEntity {
        return TranslationEntity(
            id = result.id,
            sourceText = result.sourceText,
            translatedText = result.translatedText,
            sourceLang = result.sourceLang,
            targetLang = result.targetLang,
            providerId = result.providerId,
            alternatives = result.alternatives,
            synonyms = result.synonyms,
            definitions = result.definitions,
            examples = result.examples,
            confidence = result.confidence,
            timestamp = result.timestamp,
            isBookmarked = result.isBookmarked,
            sentences = result.sentences,
            dict = result.dict,
            synsets = result.synsets,
            definitionEntries = result.definitionEntries,
            terms = result.terms,
            transliteration = result.transliteration,
            allExamples = result.allExamples,
            posTerms = result.posTerms
        )
    }

    fun fromEntity(entity: TranslationEntity): TranslationResult {
        return TranslationResult(
            id = entity.id,
            sourceText = entity.sourceText,
            translatedText = entity.translatedText,
            sourceLang = entity.sourceLang,
            targetLang = entity.targetLang,
            providerId = entity.providerId,
            alternatives = entity.alternatives,
            synonyms = entity.synonyms,
            definitions = entity.definitions,
            examples = entity.examples,
            confidence = entity.confidence,
            timestamp = entity.timestamp,
            isBookmarked = entity.isBookmarked,
            isFromCache = true,
            sentences = entity.sentences,
            dict = entity.dict,
            synsets = entity.synsets,
            definitionEntries = entity.definitionEntries,
            terms = entity.terms,
            transliteration = entity.transliteration,
            allExamples = entity.allExamples,
            posTerms = entity.posTerms
        )
    }
}