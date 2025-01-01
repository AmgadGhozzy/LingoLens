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
    val englishEn: String,
    val arabicAr: String,
    val frenchFr: String,
    val germanDe: String,
    val spanishEs: String,
    val chineseZh: String,
    val portuguesePt: String,
    val swahiliSw: String,
    val czechCs: String,
    val hungarianHu: String,
    val ukrainianUk: String,
    val turkishTr: String,
    val japaneseJa: String,
    val finnishFi: String,
    val slovakSk: String,
    val hebrewHe: String,
    val malaysMs: String,
    val croatianHr: String,
    val vietnameseVi: String,
    val catalanCa: String,
    val thaiTh: String,
    val polishPl: String,
    val swedishSv: String,
    val indonesianId: String,
    val romanianRo: String,
    val dutchNl: String,
    val koreanKo: String,
    val greekEl: String,
    val italianIt: String,
    val norwegianNo: String,
    val hindiHi: String,
    val russianRu: String
)