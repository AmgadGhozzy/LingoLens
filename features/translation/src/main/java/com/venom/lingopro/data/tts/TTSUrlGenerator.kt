package com.venom.lingopro.data.tts

import java.util.Locale

interface TTSUrlGenerator {
    fun generateTTSUrl(text: String, locale: Locale): String
}
