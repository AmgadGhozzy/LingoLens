package com.venom.data.tts

import android.media.MediaPlayer
import com.venom.domain.repo.tts.MediaPlayerFactory
import javax.inject.Inject

class DefaultMediaPlayerFactory @Inject constructor() : MediaPlayerFactory {
    override fun create(): MediaPlayer = MediaPlayer()
}