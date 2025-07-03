package com.venom.wordcraftai.data.model

import com.google.gson.annotations.SerializedName
import com.venom.wordcraftai.domain.model.Language

data class ImageTagsResponse(
    @SerializedName("tagList") val tagList: List<String>,
    @SerializedName("timeTakenInSeconds") val timeTakenInSeconds: Double
)

data class ImageCaptionResponse(
    @SerializedName("caption") val caption: String
)

data class TranslationRequest(
    @SerializedName("languageToTranslateTo") val languageToTranslateTo: String,
    @SerializedName("wordToTranslate") val wordToTranslate: String
)

data class TranslationResponse(
    @SerializedName("originalWord") val originalWord: String,
    @SerializedName("translation") val translation: Translation
)

data class Translation(
    @SerializedName("t") val translatedText: String,
    @SerializedName("r") val romanization: String
)

data class GenerateSentencesRequest(
    @SerializedName("prompt") val prompt: String = "",
    @SerializedName("languageToTranslateTo") val languageToTranslateTo: String,
    @SerializedName("numSentencesToGenerate") val numSentencesToGenerate: Int = 5,
    @SerializedName("temperature") val temperature: Double = 0.4,
    @SerializedName("detectedObjects") val detectedObjects: List<String>,
    @SerializedName("detectedCaption") val detectedCaption: String
)

data class GeneratedSentence(
    @SerializedName("englishSentence") val englishSentence: String,
    @SerializedName("translatedSentence") val translatedSentence: String,
    @SerializedName("romanization") val romanization: String,
    @SerializedName("emoji") val emoji: String
)

data class SentenceExplanationRequest(
    @SerializedName("prompt") val prompt: String = "",
    @SerializedName("language") val language: String,
    @SerializedName("sentence") val sentence: String,
    @SerializedName("englishSentence") val englishSentence: String
)

data class SentenceExplanationResponse(
    @SerializedName("wordByWordExplanations") val wordByWordExplanations: List<WordExplanation>
)

data class WordExplanation(
    @SerializedName("w") val word: String,
    @SerializedName("t") val translation: String,
    @SerializedName("pos") val partOfSpeech: String,
    @SerializedName("e") val explanation: String
)
val SupportedLanguage = listOf(
    Language("Thai", "tha"),
    Language("Spanish", "spa"),
    Language("Vietnamese", "vie"),
    Language("French", "fra"),
    Language("German", "deu"),
    Language("Italian", "ita"),
    Language("Portuguese (Brazilian)", "por"),
    Language("Dutch", "nld"),
    Language("Russian", "rus"),
    Language("Chinese (Simplified)", "cmn"),
    Language("Japanese", "jpn"),
    Language("Korean", "kor"),
    Language("Arabic", "arb"),
    Language("Hindi", "hin"),
    Language("Swedish", "swe"),
    Language("Norwegian", "nor"),
    Language("Danish", "dan"),
    Language("Finnish", "fin"),
    Language("Polish", "pol"),
    Language("Turkish", "tur"),
    Language("Greek", "ell"),
    Language("Hebrew", "heb")
)