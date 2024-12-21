package com.venom.data.mapper

import com.venom.data.model.TranslationResponse


fun extractSynonyms(translationResponse: TranslationResponse): List<String> {
    return translationResponse.synsets?.flatMap { it.entry.flatMap { entry -> entry.synonym } }
        ?.distinct().orEmpty()
}

