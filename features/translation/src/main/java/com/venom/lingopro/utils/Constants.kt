package com.venom.lingopro.utils

import com.venom.lingopro.R
import com.venom.lingopro.data.model.TranslationEntry
import com.venom.lingopro.domain.model.LanguageItem

object Constants {

    val LANGUAGES_LIST = listOf(
        LanguageItem(
            code = "en",
            name = "English",
            nativeName = "English",
            flagResId = R.drawable.flag_en,
        ), LanguageItem(
            code = "ar",
            name = "Arabic",
            nativeName = "العربية",
            flagResId = R.drawable.flag_ar,
        ), LanguageItem(
            code = "es",
            name = "Spanish",
            nativeName = "Español",
            //flagResId = R.drawable.flag_es,
        ), LanguageItem(
            code = "fr",
            name = "French",
            nativeName = "Français",
            //flagResId = R.drawable.flag_fr,
        ), LanguageItem(
            code = "de",
            name = "German",
            nativeName = "Deutsch",
            //lagResId = R.drawable.flag_de,
        ), LanguageItem(
            code = "zh",
            name = "Chinese",
            nativeName = "中文",
            //flagResId = R.drawable.flag_zh,
        ), LanguageItem(
            code = "ru",
            name = "Russian",
            nativeName = "Русский",
            //flagResId = R.drawable.flag_ru,
        )
    )

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
            sourceText = "Please",
            translatedText = "Por favor",
            sourceLang = "en",
            targetLang = "es"
        ), TranslationEntry(
            sourceText = "Sorry", translatedText = "Lo siento", sourceLang = "en", targetLang = "es"
        ), TranslationEntry(
            sourceText = "Excuse me",
            translatedText = "Perdón",
            sourceLang = "en",
            targetLang = "es"
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
}