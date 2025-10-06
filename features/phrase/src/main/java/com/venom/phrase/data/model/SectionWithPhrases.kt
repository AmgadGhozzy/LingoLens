package com.venom.phrase.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class SectionWithPhrases(
    @Embedded val section: Section, @Relation(
        parentColumn = "sectionId", entityColumn = "sectionId"
    ) val phrases: List<PhraseEntity>
)