package com.venom.domain.repo.tts

import android.speech.tts.TextToSpeech

interface TextToSpeechFactory {
    fun create(onInitListener: TextToSpeech.OnInitListener): TextToSpeech
}