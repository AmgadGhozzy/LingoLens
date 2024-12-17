package com.venom.lingopro.utils

import com.venom.lingopro.data.model.TranslationResponse

fun extractSynonyms(translationResponse: TranslationResponse): List<String> {
    return translationResponse.synsets
        ?.flatMap { it.entry.flatMap { entry -> entry.synonym } }
        ?.distinct()
        .orEmpty()
}

