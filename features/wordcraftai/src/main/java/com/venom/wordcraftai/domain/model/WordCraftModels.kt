package com.venom.wordcraftai.domain.model

import android.net.Uri

data class ImageAnalysis(
    val tags: List<String> = emptyList(),
    val caption: String = ""
)

data class WordTranslation(
    val originalWord: String,
    val translatedText: String,
    val romanization: String,
    val language: String
)

data class LearningExample(
    val englishSentence: String,
    val translatedSentence: String,
    val romanization: String,
    val emoji: String,
    val audioUrl: String = ""
)

data class WordBreakdown(
    val word: String,
    val translation: String,
    val partOfSpeech: String,
    val explanation: String
)

data class SentenceAnalysis(
    val originalSentence: String,
    val englishSentence: String,
    val language: String,
    val wordBreakdowns: List<WordBreakdown>,
    val audioUrl: String = ""
)

data class LearningSession(
    val imageUrl: Uri? = null,
    val imageAnalysis: ImageAnalysis = ImageAnalysis(),
    val selectedWord: String = "",
    val wordTranslation: WordTranslation? = null,
    val examples: List<LearningExample> = emptyList(),
    val selectedExample: LearningExample? = null,
    val sentenceAnalysis: SentenceAnalysis? = null
)

data class Language(
    val name: String,
    val code: String
)