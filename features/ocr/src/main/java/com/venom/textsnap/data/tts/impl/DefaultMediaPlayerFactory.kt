package com.venom.textsnap.data.tts.impl

import android.media.MediaPlayer
import com.venom.textsnap.data.tts.MediaPlayerFactory
import javax.inject.Inject

class DefaultMediaPlayerFactory @Inject constructor() : MediaPlayerFactory {
    override fun create(): MediaPlayer = MediaPlayer()
}