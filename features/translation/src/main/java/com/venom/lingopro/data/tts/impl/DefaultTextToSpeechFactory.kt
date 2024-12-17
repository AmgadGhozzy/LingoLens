package com.venom.lingopro.data.tts.impl

import android.content.Context
import android.speech.tts.TextToSpeech
import com.venom.lingopro.data.tts.TextToSpeechFactory
import javax.inject.Inject

class DefaultTextToSpeechFactory @Inject constructor(
    private val context: Context
) : TextToSpeechFactory {
    override fun create(onInitListener: TextToSpeech.OnInitListener): TextToSpeech {
        return TextToSpeech(context, onInitListener)
    }
}