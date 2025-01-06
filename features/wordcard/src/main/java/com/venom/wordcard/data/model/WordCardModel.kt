package com.venom.wordcard.data.model

data class WordCardModel(
    val id: String,
    val word: String,
    val translation: String? = null,
    val examples: List<String> = emptyList()
)