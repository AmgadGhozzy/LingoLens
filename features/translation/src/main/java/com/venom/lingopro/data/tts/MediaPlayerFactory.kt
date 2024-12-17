package com.venom.lingopro.data.tts

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import java.util.Locale

interface MediaPlayerFactory {
    fun create(): MediaPlayer
}
