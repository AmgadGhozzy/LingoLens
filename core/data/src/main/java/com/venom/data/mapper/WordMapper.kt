package com.venom.data.mapper

import com.venom.data.local.Entity.WordEntity
import com.venom.domain.model.Word

object WordMapper {
    fun toDomain(entity: WordEntity): Word {
        return Word(
            id = entity.id,
            ranking = entity.ranking,
            englishEn = entity.englishEn,
            arabicAr = entity.arabicAr,
            isBookmarked = entity.isBookmarked,
            isRemembered = entity.isRemembered,
            isForgotten = entity.isForgotten,
            synonyms = entity.synonyms
        )
    }

    fun toData(domain: Word): WordEntity {
        return WordEntity(
            id = domain.id,
            ranking = domain.ranking,
            englishEn = domain.englishEn,
            arabicAr = domain.arabicAr,
            isBookmarked = domain.isBookmarked,
            isRemembered = domain.isRemembered,
            isForgotten = domain.isForgotten,
            synonyms = domain.synonyms
        )
    }

    fun toDomainList(entities: List<WordEntity>): List<Word> {
        return entities.map { toDomain(it) }
    }

    fun toDataList(domains: List<Word>): List<WordEntity> {
        return domains.map { toData(it) }
    }
}
