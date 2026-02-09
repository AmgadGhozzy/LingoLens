package com.venom.data.repo

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.venom.data.BuildConfig
import com.venom.data.api.GeminiConfig
import com.venom.data.api.GeminiContent
import com.venom.data.api.GeminiPart
import com.venom.data.api.GeminiRequest
import com.venom.data.api.GeminiService
import com.venom.data.local.dao.WordMasterDao
import com.venom.data.local.entity.WordMasterEntity
import com.venom.data.mapper.WordMasterEntityMapper
import com.venom.data.remote.respnod.WordEntriesResponse
import com.venom.domain.model.EnrichmentStatus
import com.venom.domain.model.WordEnrichmentInput
import com.venom.domain.model.WordMaster
import com.venom.domain.repo.IEnrichmentRepository
import com.venom.utils.BatchUserPrompt
import com.venom.utils.SystemInstruction
import com.venom.utils.wordResponseSchema
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

// Enriches NEW words each time user clicks Start
@Singleton
class EnrichmentRepositoryImpl @Inject constructor(
    private val wordMasterDao: WordMasterDao,
    private val geminiService: GeminiService,
    private val gson: Gson
) : IEnrichmentRepository {

    companion object {
        const val CURRENT_VERSION = "1.0.0"
    }

    override val enrichmentVersion: String = CURRENT_VERSION

    override suspend fun enrichAndGetWords(size: Int): List<WordMaster> =
        withContext(Dispatchers.IO) {
            try {
                // 1. Get unenriched words from DB (different words each time)
                val batch = wordMasterDao.getUnenrichedBatch(size).map { entity ->
                    WordEnrichmentInput(
                        id = entity.id,
                        wordEn = entity.wordEn ?: "",
                        pos = entity.pos ?: "noun",
                        cefrLevel = entity.cefrLevel ?: "B1",
                        rank = entity.rank ?: 0,
                        frequency = entity.frequency ?: 1
                    )
                }

                // If no unenriched words available, return empty
                if (batch.isEmpty()) return@withContext emptyList()

                // 2. Build prompt and call Gemini
                val wordsJson = gson.toJson(batch)
                val prompt = BatchUserPrompt + wordsJson

                val request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(
                            role = "user",
                            parts = listOf(GeminiPart(prompt))
                        )
                    ),
                    systemInstruction = GeminiContent(
                        role = "system",
                        parts = listOf(GeminiPart(SystemInstruction))
                    ),
                    generationConfig = GeminiConfig(
                        responseSchema = getResponseSchema()
                    )
                )

                val response = geminiService.generate(
                    model = GeminiService.FLASH_MODEL_3,
                    apiKey = BuildConfig.GEMINI_API_KEY,
                    request = request
                )

                val jsonText =
                    response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                        ?: return@withContext emptyList()

                // 3. Parse response, save to DB, and collect enriched words
                val wordsResponse = gson.fromJson(jsonText, WordEntriesResponse::class.java)
                val enrichedAt = System.currentTimeMillis()
                val enrichedWords = mutableListOf<WordMaster>()

                for (dto in wordsResponse.wordEntries) {
                    val wordId = dto.id ?: continue
                    val existing = wordMasterDao.getWordById(wordId) ?: continue
                    val enrichedWord = WordMasterEntityMapper.toDomain(dto, wordId)
                    val updated = mergeEnrichmentData(existing, enrichedWord, enrichedAt)
                    wordMasterDao.insert(updated)
                    // Use the merged entity to ensure we return DB truth (id, rank) mixed with enrichment
                    enrichedWords.add(WordMasterEntityMapper.toDomain(updated))
                }

                enrichedWords
            } catch (e: Exception) {
                emptyList()
            }
        }

    override suspend fun generateWords(prompt: String): Result<List<WordMaster>> =
        withContext(Dispatchers.IO) {
            try {
                val request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(
                            role = "user",
                            parts = listOf(GeminiPart(prompt))
                        )
                    ),
                    systemInstruction = GeminiContent(
                        role = "system",
                        parts = listOf(GeminiPart(SystemInstruction))
                    ),
                    generationConfig = GeminiConfig(
                        responseSchema = getResponseSchema()
                    )
                )

                val response = geminiService.generate(
                    model = GeminiService.FLASH_MODEL_3,
                    apiKey = BuildConfig.GEMINI_API_KEY,
                    request = request
                )

                val jsonText =
                    response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                        ?: return@withContext Result.failure(Exception("Gemini returned empty response"))

                val wordsResponse = gson.fromJson(jsonText, WordEntriesResponse::class.java)
                val words = wordsResponse.wordEntries.mapIndexed { index, dto ->
                    WordMasterEntityMapper.toDomain(dto, dto.id ?: index)
                }

                Result.success(words)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private fun getResponseSchema(): Map<String, Any> {
        val type = object : TypeToken<Map<String, Any>>() {}.type
        return gson.fromJson(wordResponseSchema, type)
    }

    private fun mergeEnrichmentData(
        existing: WordMasterEntity,
        enriched: WordMaster,
        enrichedAt: Long
    ): WordMasterEntity {
        return existing.copy(
            pos = enriched.pos.takeIf { it.isNotBlank() } ?: existing.pos,
            cefrLevel = enriched.cefrLevel.name,
            phoneticUs = enriched.phoneticUs.takeIf { it.isNotBlank() } ?: existing.phoneticUs,
            phoneticUk = enriched.phoneticUk ?: enriched.phoneticUs,
            phoneticAr = enriched.phoneticAr.takeIf { it.isNotBlank() } ?: existing.phoneticAr,
            syllabify = enriched.syllabify ?: existing.syllabify,
            translit = enriched.translit.takeIf { it.isNotBlank() } ?: existing.translit,
            definitionEn = enriched.definitionEn.takeIf { it.isNotBlank() }
                ?: existing.definitionEn,
            definitionAr = enriched.definitionAr ?: existing.definitionAr,
            usageNote = enriched.usageNote ?: existing.usageNote,
            category = enriched.category ?: existing.category,
            primarySense = enriched.primarySense ?: existing.primarySense,
            semanticTags = if (enriched.semanticTags.isNotEmpty())
                gson.toJson(enriched.semanticTags) else existing.semanticTags,
            register = enriched.register,
            mnemonicAr = enriched.mnemonicAr ?: existing.mnemonicAr,
            wordFamily = gson.toJson(enriched.wordFamily),
            synonyms = if (enriched.synonyms.isNotEmpty())
                gson.toJson(enriched.synonyms) else existing.synonyms,
            antonyms = if (enriched.antonyms.isNotEmpty())
                gson.toJson(enriched.antonyms) else existing.antonyms,
            examples = if (enriched.examples.isNotEmpty())
                gson.toJson(enriched.examples.mapKeys { it.key.name }) else existing.examples,
            collocations = if (enriched.collocations.isNotEmpty())
                gson.toJson(enriched.collocations) else existing.collocations,
            relatedWords = gson.toJson(enriched.relatedWords),
            arabicAr = enriched.arabicAr.takeIf { it.isNotBlank() } ?: existing.arabicAr,
            frenchFr = enriched.frenchFr ?: existing.frenchFr,
            germanDe = enriched.germanDe ?: existing.germanDe,
            spanishEs = enriched.spanishEs ?: existing.spanishEs,
            chineseZh = enriched.chineseZh ?: existing.chineseZh,
            russianRu = enriched.russianRu ?: existing.russianRu,
            portuguesePt = enriched.portuguesePt ?: existing.portuguesePt,
            japaneseJa = enriched.japaneseJa ?: existing.japaneseJa,
            italianIt = enriched.italianIt ?: existing.italianIt,
            turkishTr = enriched.turkishTr ?: existing.turkishTr,
            difficultyScore = enriched.difficultyScore,
            isEnriched = true,
            enrichedAt = enrichedAt,
            enrichmentVersion = enrichmentVersion,
            enrichmentSource = "gemini",
            enrichmentStatus = EnrichmentStatus.SUCCESS.value
        )
    }
}
