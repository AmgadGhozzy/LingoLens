package com.venom.domain.repo.tts

import android.media.MediaPlayer

interface MediaPlayerFactory {
    fun create(): MediaPlayer
}
