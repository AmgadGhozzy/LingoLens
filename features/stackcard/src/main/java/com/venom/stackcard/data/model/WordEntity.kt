package com.venom.stackcard.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ranking: Int,
    val englishEn: String,
    val arabicAr: String,
    val isBookmarked: Boolean = false,
    val isRemembered: Boolean = false,
    val isForgotten: Boolean = false,
    val synonyms: List<String> = emptyList()
)

val wordList = listOf(
    WordEntity(
        ranking = 1,
        englishEn = "Hello",
        arabicAr = "مرحبا",
        isBookmarked = true,
        synonyms = listOf("Hi", "Greetings")
    ),
    WordEntity(
        ranking = 2,
        englishEn = "World",
        arabicAr = "عالم",
        isRemembered = true
    ),
    WordEntity(
        ranking = 3,
        englishEn = "Book",
        arabicAr = "كتاب",
        isBookmarked = true,
        synonyms = listOf("Volume", "Tome")
    ),
    WordEntity(
        ranking = 4,
        englishEn = "Computer",
        arabicAr = "حاسوب",
        isForgotten = true
    ),
    WordEntity(
        ranking = 5,
        englishEn = "Apple",
        arabicAr = "تفاحة",
        synonyms = listOf("fruit")
    ),
    WordEntity(
        ranking = 6,
        englishEn = "House",
        arabicAr = "بيت",
        isRemembered = true
    ),
    WordEntity(
        ranking = 7,
        englishEn = "Sun",
        arabicAr = "شمس",
        isBookmarked = true,
        synonyms = listOf("star","solar")
    ),
    WordEntity(
        ranking = 8,
        englishEn = "Moon",
        arabicAr = "قمر",
        isForgotten = true
    ),
    WordEntity(
        ranking = 9,
        englishEn = "Car",
        arabicAr = "سيارة",
        synonyms = listOf("vehicle","automobile")
    ),
    WordEntity(
        ranking = 10,
        englishEn = "Water",
        arabicAr = "ماء",
        isRemembered = true
    )
)