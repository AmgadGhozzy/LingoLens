package com.venom.wordcraftai.domain.repository

import com.venom.wordcraftai.domain.model.ImageAnalysis
import com.venom.wordcraftai.domain.model.Language
import com.venom.wordcraftai.domain.model.LearningExample
import com.venom.wordcraftai.domain.model.SentenceAnalysis
import com.venom.wordcraftai.domain.model.WordTranslation

interface WordCraftRepository {
    suspend fun analyzeImage(imageData: String): ImageAnalysis

    suspend fun translateWord(word: String, targetLanguage: String): WordTranslation

    suspend fun generateExamples(
        targetLanguage: Language,
        detectedObjects: List<String>,
        caption: String,
        count: Int = 5
    ): List<LearningExample>

    suspend fun explainSentence(
        language: String,
        sentence: String,
        englishSentence: String
    ): SentenceAnalysis

    fun getTtsUrl(languageCode: String, text: String, gender: String = "female"): String
}