package com.venom.phrase.data.mapper

import android.util.Log
import com.venom.phrase.data.model.ITranslatable

fun ITranslatable.getTranslation(langCode: String): String {
    Log.d("getTranslation", "langCode: $langCode")
    return when (langCode) {
        "en" -> this.englishEn
        "ar" -> this.arabicAr
        "fr" -> this.frenchFr
        "de" -> this.germanDe
        "es" -> this.spanishEs
        "zh" -> this.chineseZh
        "pt" -> this.portuguesePt
        "sw" -> this.swahiliSw
        "cs" -> this.czechCs
        "hu" -> this.hungarianHu
        "uk" -> this.ukrainianUk
        "tr" -> this.turkishTr
        "ja" -> this.japaneseJa
        "fi" -> this.finnishFi
        "sk" -> this.slovakSk
        "he" -> this.hebrewHe
        "ms" -> this.malaysMs
        "hr" -> this.croatianHr
        "vi" -> this.vietnameseVi
        "ca" -> this.catalanCa
        "th" -> this.thaiTh
        "pl" -> this.polishPl
        "sv" -> this.swedishSv
        "id" -> this.indonesianId
        "ro" -> this.romanianRo
        "nl" -> this.dutchNl
        "ko" -> this.koreanKo
        "el" -> this.greekEl
        "it" -> this.italianIt
        "no" -> this.norwegianNo
        "hi" -> this.hindiHi
        "ru" -> this.russianRu
        else -> this.englishEn
    }
}