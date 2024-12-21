package com.venom.data.tts

import com.venom.domain.repo.tts.TTSUrlGenerator
import java.net.URLEncoder
import java.util.Locale
import javax.inject.Inject

class DefaultTTSUrlGenerator @Inject constructor() : TTSUrlGenerator {
    override fun generateTTSUrl(text: String, locale: Locale): String {
        return "https://st.tokhmi.xyz/api/tts/?engine=google&lang=$locale&text=${
            URLEncoder.encode(text, "UTF-8")
        }"
    }
}