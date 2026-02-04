package com.venom.data.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.venom.data.local.entity.WordMasterEntity
import com.venom.data.remote.respnod.RelatedWordsDto
import com.venom.data.remote.respnod.WordFamilyDto
import com.venom.data.remote.respnod.WordMasterDto
import com.venom.domain.model.CefrLevel
import com.venom.domain.model.RelatedWords
import com.venom.domain.model.WordFamily
import com.venom.domain.model.WordMaster

/**
 * Mapper between WordMasterEntity and WordMaster domain model.
 */
object WordMasterEntityMapper {
    
    private val gson = Gson()
    
    fun toDomain(entity: WordMasterEntity): WordMaster {
        val cefrLevel = try {
            CefrLevel.valueOf(entity.cefrLevel?.uppercase() ?: "B1")
        } catch (_: Exception) {
            CefrLevel.B1
        }
        
        val semanticTagsList: List<String> = entity.semanticTags?.let {
            parseJsonList(it)
        } ?: emptyList()
        
        val synonymsList: List<String> = entity.synonyms?.let { parseJsonList(it) } ?: emptyList()
        val antonymsList: List<String> = entity.antonyms?.let { parseJsonList(it) } ?: emptyList()
        val collocationsList: List<String> = entity.collocations?.let { parseJsonList(it) } ?: emptyList()
        
        val examplesMap: Map<CefrLevel, String> = entity.examples?.let {
            parseExamples(it)
        } ?: emptyMap()
        
        val wordFamilyObj = entity.wordFamily?.let { parseWordFamily(it) } ?: WordFamily()
        val relatedWordsObj = entity.relatedWords?.let { parseRelatedWords(it) } ?: RelatedWords(emptyList(), emptyList())
        
        return WordMaster(
            id = entity.id,
            wordEn = entity.wordEn ?: "",
            pos = entity.pos ?: "noun",
            cefrLevel = cefrLevel,
            fromOxford = entity.fromOxford ?: 0,
            rank = entity.rank ?: 0,
            frequency = entity.frequency ?: 1,
            difficultyScore = entity.difficultyScore ?: 5,
            phoneticUs = entity.phoneticUs ?: "",
            phoneticUk = entity.phoneticUk,
            phoneticAr = entity.phoneticAr ?: "",
            translit = entity.translit ?: "",
            syllabify = entity.syllabify,
            definitionEn = entity.definitionEn ?: "",
            definitionAr = entity.definitionAr,
            usageNote = entity.usageNote,
            category = entity.category,
            primarySense = entity.primarySense,
            semanticTags = semanticTagsList,
            register = entity.register ?: "Neutral",
            mnemonicAr = entity.mnemonicAr,
            wordFamily = wordFamilyObj,
            synonyms = synonymsList,
            antonyms = antonymsList,
            examples = examplesMap,
            collocations = collocationsList,
            relatedWords = relatedWordsObj,
            arabicAr = entity.arabicAr ?: "",
            frenchFr = entity.frenchFr,
            germanDe = entity.germanDe,
            spanishEs = entity.spanishEs,
            chineseZh = entity.chineseZh,
            russianRu = entity.russianRu,
            portuguesePt = entity.portuguesePt,
            japaneseJa = entity.japaneseJa,
            italianIt = entity.italianIt,
            turkishTr = entity.turkishTr
        )

    }

    /**
     * Map WordMasterDto to WordMaster domain model.
     */
    fun toDomain(dto: WordMasterDto, fallbackId: Int = 0): WordMaster {
        // Parse CEFR Level safely
        val cefrLevelEnum = try {
            CefrLevel.valueOf(dto.cefrLevel.uppercase())
        } catch (e: IllegalArgumentException) {
            CefrLevel.B1 // Default fallback
        }

        // Parse frequency as Int
        val frequencyInt = dto.frequency?.toIntOrNull() ?: (cefrLevelEnum.ordinal + 1)

        // Parse difficulty score
        val difficultyInt = dto.difficultyScore?.toIntOrNull()
            ?: calculateDifficulty(dto.wordEn, dto.syllabify, cefrLevelEnum)

        // Parse fromOxford
        val fromOxfordInt = dto.fromOxford?.toIntOrNull() ?: 0

        // Parse examples map
        val examplesMap = dto.examples?.mapNotNull { (key, value) ->
            try {
                CefrLevel.valueOf(key.uppercase()) to value
            } catch (e: IllegalArgumentException) {
                null
            }
        }?.toMap() ?: emptyMap()

        return WordMaster(
            id = dto.id ?: fallbackId,
            wordEn = dto.wordEn,
            pos = dto.pos,
            cefrLevel = cefrLevelEnum,
            fromOxford = fromOxfordInt,
            rank = dto.rank ?: 0,
            frequency = frequencyInt,
            difficultyScore = difficultyInt,
            phoneticUs = dto.phoneticUs,
            phoneticUk = dto.phoneticUk,
            phoneticAr = dto.phoneticAr,
            translit = dto.translit,
            syllabify = dto.syllabify,
            definitionEn = dto.definitionEn,
            definitionAr = dto.definitionAr,
            usageNote = dto.usageNote,
            category = dto.category,
            primarySense = dto.primarySense,
            semanticTags = dto.semanticTags ?: emptyList(),
            register = dto.register ?: "Neutral",
            mnemonicAr = dto.mnemonicAr,
            wordFamily = dto.wordFamily?.toDomain() ?: WordFamily(),
            synonyms = dto.synonyms ?: emptyList(),
            antonyms = dto.antonyms ?: emptyList(),
            examples = examplesMap,
            collocations = dto.collocations ?: emptyList(),
            relatedWords = dto.relatedWords?.toDomain() ?: RelatedWords(emptyList(), emptyList()),
            arabicAr = dto.arabicAr,
            frenchFr = dto.frenchFr,
            germanDe = dto.germanDe,
            spanishEs = dto.spanishEs,
            chineseZh = dto.chineseZh,
            russianRu = dto.russianRu,
            portuguesePt = dto.portuguesePt,
            japaneseJa = dto.japaneseJa,
            italianIt = dto.italianIt,
            turkishTr = dto.turkishTr
        )
    }

    private fun WordFamilyDto.toDomain(): WordFamily {
        return WordFamily(
            noun = noun?.takeIf { it.isNotBlank() },
            verb = verb?.takeIf { it.isNotBlank() },
            adj = adj?.takeIf { it.isNotBlank() },
            adv = adv?.takeIf { it.isNotBlank() }
        )
    }

    private fun RelatedWordsDto.toDomain(): RelatedWords {
        return RelatedWords(
            english = en ?: emptyList(),
            arabic = ar ?: emptyList()
        )
    }

    private fun calculateDifficulty(
        word: String,
        syllabify: String?,
        cefrLevel: CefrLevel
    ): Int {
        val syllableCount = syllabify?.count { it == '•' }?.plus(1) ?: 1
        val lengthFactor = word.length * 0.2
        val syllableFactor = syllableCount * 0.4
        val cefrFactor = cefrLevel.ordinal * 1.0

        return (lengthFactor + syllableFactor + cefrFactor)
            .toInt()
            .coerceIn(1, 10)
    }
    
    fun toEntity(domain: WordMaster): WordMasterEntity {
        return WordMasterEntity(
            id = domain.id,
            wordEn = domain.wordEn,
            pos = domain.pos,
            cefrLevel = domain.cefrLevel.name,
            fromOxford = domain.fromOxford,
            rank = domain.rank,
            frequency = domain.frequency,
            difficultyScore = domain.difficultyScore,
            phoneticUs = domain.phoneticUs,
            phoneticUk = domain.phoneticUk,
            phoneticAr = domain.phoneticAr,
            translit = domain.translit,
            syllabify = domain.syllabify,
            definitionEn = domain.definitionEn,
            definitionAr = domain.definitionAr,
            usageNote = domain.usageNote,
            category = domain.category,
            primarySense = domain.primarySense,
            semanticTags = gson.toJson(domain.semanticTags),
            register = domain.register,
            mnemonicAr = domain.mnemonicAr,
            wordFamily = gson.toJson(domain.wordFamily),
            synonyms = gson.toJson(domain.synonyms),
            antonyms = gson.toJson(domain.antonyms),
            examples = gson.toJson(domain.examples.mapKeys { it.key.name }),
            collocations = gson.toJson(domain.collocations),
            relatedWords = gson.toJson(domain.relatedWords),
            arabicAr = domain.arabicAr,
            frenchFr = domain.frenchFr,
            germanDe = domain.germanDe,
            spanishEs = domain.spanishEs,
            chineseZh = domain.chineseZh,
            russianRu = domain.russianRu,
            portuguesePt = domain.portuguesePt,
            japaneseJa = domain.japaneseJa,
            italianIt = domain.italianIt,
            turkishTr = domain.turkishTr
        )
    }
    
    fun toDomainList(entities: List<WordMasterEntity>): List<WordMaster> = entities.map { toDomain(it) }
    
    fun toEntityList(domains: List<WordMaster>): List<WordMasterEntity> = domains.map { toEntity(it) }
    
    private fun parseJsonList(json: String): List<String> {
        return try {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }
    
    private fun parseExamples(json: String): Map<CefrLevel, String> {
        return try {
            val type = object : TypeToken<Map<String, String>>() {}.type
            val map: Map<String, String> = gson.fromJson(json, type) ?: emptyMap()
            map.mapNotNull { (key, value) ->
                try {
                    CefrLevel.valueOf(key.uppercase()) to value
                } catch (e: Exception) {
                    null
                }
            }.toMap()
        } catch (_: Exception) {
            emptyMap()
        }
    }
    
    private fun parseWordFamily(json: String): WordFamily {
        return try {
            gson.fromJson(json, WordFamily::class.java) ?: WordFamily()
        } catch (e: Exception) {
            WordFamily()
        }
    }
    
    private fun parseRelatedWords(json: String): RelatedWords {
        return try {
            val type = object : TypeToken<Map<String, List<String>>>() {}.type
            val map: Map<String, List<String>> = gson.fromJson(json, type) ?: emptyMap()
            RelatedWords(
                english = map["en"] ?: map["english"] ?: emptyList(),
                arabic = map["ar"] ?: map["arabic"] ?: emptyList()
            )
        } catch (e: Exception) {
            RelatedWords(emptyList(), emptyList())
        }
    }
}
