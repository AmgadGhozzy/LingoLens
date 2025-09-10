package com.venom.domain.model

data class Word(
    val id: Int = 0,
    val ranking: Int,
    val englishEn: String,
    val arabicAr: String,
    val isBookmarked: Boolean = false,
    val isRemembered: Boolean = false,
    val isForgotten: Boolean = false,
    val synonyms: List<String> = emptyList()
)
