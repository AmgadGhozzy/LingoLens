package com.venom.lingopro.data.tts.impl

import android.media.MediaPlayer
import com.venom.lingopro.data.tts.MediaPlayerFactory
import javax.inject.Inject

class DefaultMediaPlayerFactory @Inject constructor() : MediaPlayerFactory {
    override fun create(): MediaPlayer = MediaPlayer()
}