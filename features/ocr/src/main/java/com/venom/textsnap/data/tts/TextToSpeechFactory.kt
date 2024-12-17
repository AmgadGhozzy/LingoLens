package com.venom.textsnap.data.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface TextToSpeechFactory {
    fun create(onInitListener: TextToSpeech.OnInitListener): TextToSpeech
}