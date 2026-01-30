package com.venom.data.repo

import android.content.Context
import com.venom.data.BuildConfig
import com.venom.data.api.GroqTtsRequest
import com.venom.data.api.GroqTtsService
import com.venom.data.api.GroqTtsService.Companion.ORPHEUS_AR
import com.venom.data.api.GroqTtsService.Companion.ORPHEUS_EN
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale
import javax.inject.Inject

class GroqTtsRepository @Inject constructor(
    private val service: GroqTtsService,
    @ApplicationContext private val context: Context
) {

    suspend fun generateSpeech(
        text: String,
        language: Locale,
        voice: String
    ): File = withContext(Dispatchers.IO) {

        val model = if (language.language == "ar") {
            ORPHEUS_AR
        } else {
            ORPHEUS_EN
        }

        val response = service.textToSpeech(
            apiKey = BuildConfig.GROQ_API_KEY,
            body = GroqTtsRequest(
                model = model,
                input = text,
                voice = voice
            )
        )

        val file = File.createTempFile(
            "groq_tts_",
            ".wav",
            context.cacheDir
        )

        response.byteStream().use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        file
    }
}
