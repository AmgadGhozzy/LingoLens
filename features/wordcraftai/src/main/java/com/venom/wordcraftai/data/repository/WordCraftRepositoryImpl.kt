package com.venom.wordcraftai.data.repository

import android.util.Log
import com.venom.wordcraftai.data.api.WordCraftApiService
import com.venom.wordcraftai.data.model.GenerateSentencesRequest
import com.venom.wordcraftai.data.model.SentenceExplanationRequest
import com.venom.wordcraftai.data.model.TranslationRequest
import com.venom.wordcraftai.domain.model.ImageAnalysis
import com.venom.wordcraftai.domain.model.Language
import com.venom.wordcraftai.domain.model.LearningExample
import com.venom.wordcraftai.domain.model.SentenceAnalysis
import com.venom.wordcraftai.domain.model.WordBreakdown
import com.venom.wordcraftai.domain.model.WordTranslation
import com.venom.wordcraftai.domain.repository.WordCraftRepository
import java.io.IOException
import java.net.URLEncoder
import javax.inject.Inject

class WordCraftRepositoryImpl @Inject constructor(
    private val apiService: WordCraftApiService
) : WordCraftRepository {

    override suspend fun analyzeImage(imageData: String): ImageAnalysis {
        try {
            val request = mapOf("imageData" to imageData)
            val tags = apiService.getImageTags(request).tagList
            val caption = apiService.getImageCaption(request).caption
            return ImageAnalysis(tags, caption)
        } catch (e: Exception) {
            Log.e("ImageAnalysis", "Error processing image data", e)
            throw IOException("Invalid image data: ${e.message}")
        }
    }

    override suspend fun translateWord(word: String, targetLanguage: String): WordTranslation {
        val request = TranslationRequest(targetLanguage, word)
        val response = apiService.translateWord(request)
        return WordTranslation(
            originalWord = response.originalWord,
            translatedText = response.translation.translatedText,
            romanization = response.translation.romanization,
            language = targetLanguage
        )
    }

    override suspend fun generateExamples(
        targetLanguage: Language,
        detectedObjects: List<String>,
        caption: String,
        count: Int
    ): List<LearningExample> {
        val request = GenerateSentencesRequest(
            languageToTranslateTo = targetLanguage.code,
            numSentencesToGenerate = count,
            detectedObjects = detectedObjects,
            detectedCaption = caption
        )

        val sentences = apiService.generateSentences(request)

        return sentences.map { sentence ->
            LearningExample(
                englishSentence = sentence.englishSentence,
                translatedSentence = sentence.translatedSentence,
                romanization = sentence.romanization,
                emoji = sentence.emoji,
                audioUrl = getTtsUrl(targetLanguage.code, sentence.translatedSentence)
            )
        }
    }

    override suspend fun explainSentence(
        language: String,
        sentence: String,
        englishSentence: String
    ): SentenceAnalysis {
        val request = SentenceExplanationRequest(
            language = language,
            sentence = sentence,
            englishSentence = englishSentence
        )

        val response = apiService.getSentenceExplanation(request)

        return SentenceAnalysis(
            originalSentence = sentence,
            englishSentence = englishSentence,
            language = language,
            wordBreakdowns = response.wordByWordExplanations.map { explanation ->
                WordBreakdown(
                    word = explanation.word,
                    translation = explanation.translation,
                    partOfSpeech = explanation.partOfSpeech,
                    explanation = explanation.explanation
                )
            },
            audioUrl = getTtsUrl(language, sentence)
        )
    }

    override fun getTtsUrl(languageCode: String, text: String, gender: String): String {

        val encodedText = URLEncoder.encode(text, "UTF-8")
        return "https://synthesis-service.scratch.mit.edu/synth?locale=$languageCode&gender=$gender&text=$encodedText"
    }
}