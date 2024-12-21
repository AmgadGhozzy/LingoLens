package com.venom.domain.repo.tts

import java.util.Locale

interface TTSUrlGenerator {
    fun generateTTSUrl(text: String, locale: Locale): String
}
