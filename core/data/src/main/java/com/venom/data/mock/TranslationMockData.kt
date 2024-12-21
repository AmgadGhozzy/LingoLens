package com.venom.data.mock

import com.venom.data.model.TranslationEntry

val TRANSLATION_LIST = listOf(
    TranslationEntry(
        sourceText = "Hello",
        translatedText = "Hola",
        sourceLang = "en",
        targetLang = "es",
        isBookmarked = true,
        synonyms = listOf("Hi", "Greetings")
    ), TranslationEntry(
        sourceText = "Goodbye", translatedText = "Adiós", sourceLang = "en", targetLang = "es"
    ), TranslationEntry(
        sourceText = "Thank you",
        translatedText = "Gracias",
        sourceLang = "en",
        targetLang = "es",
        synonyms = listOf("Thanks")
    ), TranslationEntry(
        sourceText = "Yes", translatedText = "Sí", sourceLang = "en", targetLang = "es"
    ), TranslationEntry(
        sourceText = "No", translatedText = "No", sourceLang = "en", targetLang = "es"
    ), TranslationEntry(
        sourceText = "Please", translatedText = "Por favor", sourceLang = "en", targetLang = "es"
    ), TranslationEntry(
        sourceText = "Sorry", translatedText = "Lo siento", sourceLang = "en", targetLang = "es"
    ), TranslationEntry(
        sourceText = "Excuse me", translatedText = "Perdón", sourceLang = "en", targetLang = "es"
    ), TranslationEntry(
        sourceText = "I love you",
        translatedText = "Te quiero",
        sourceLang = "en",
        targetLang = "es",
        isBookmarked = true
    ), TranslationEntry(
        sourceText = "Help", translatedText = "Ayuda", sourceLang = "en", targetLang = "es"
    )
)
