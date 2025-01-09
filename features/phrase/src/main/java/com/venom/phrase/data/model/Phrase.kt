package com.venom.phrase.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "phrases", foreignKeys = [ForeignKey(
        entity = Section::class,
        parentColumns = ["sectionId"],
        childColumns = ["sectionId"],
    )], indices = [Index("sectionId")]
)
data class Phrase(
    @PrimaryKey val phraseId: Int,
    val sectionId: Int,
    override val englishEn: String,
    override val arabicAr: String,
    override val frenchFr: String,
    override val germanDe: String,
    override val spanishEs: String,
    override val chineseZh: String,
    override val portuguesePt: String,
    override val swahiliSw: String,
    override val czechCs: String,
    override val hungarianHu: String,
    override val ukrainianUk: String,
    override val turkishTr: String,
    override val japaneseJa: String,
    override val finnishFi: String,
    override val slovakSk: String,
    override val hebrewHe: String,
    override val malaysMs: String,
    override val croatianHr: String,
    override val vietnameseVi: String,
    override val catalanCa: String,
    override val thaiTh: String,
    override val polishPl: String,
    override val swedishSv: String,
    override val indonesianId: String,
    override val romanianRo: String,
    override val dutchNl: String,
    override val koreanKo: String,
    override val greekEl: String,
    override val italianIt: String,
    override val norwegianNo: String,
    override val hindiHi: String,
    override val russianRu: String,
    val isBookmarked: Boolean = false,
    val isRemembered: Boolean = false,
    val isForgotten: Boolean = false,
) : ITranslatable