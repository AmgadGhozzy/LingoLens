package com.venom.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.venom.resources.R

class SoundManager(private val context: Context) {
    private lateinit var soundPool: SoundPool
    private val soundMap: MutableMap<String, Int> = mutableMapOf()

    init {
        initializeSoundPool()
    }

    private fun initializeSoundPool() {
        // Initialize SoundPool with appropriate AudioAttributes
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1) // Set the maximum number of simultaneous streams
            .setAudioAttributes(audioAttributes)
            .build()

        // Load all sounds lazily using lazy delegate
        loadSound("key_sound", R.raw.key_sound)
        loadSound("key_sound2", R.raw.key_sound2)
        loadSound("key_sound3", R.raw.key_sound3)
        loadSound("key_sound4", R.raw.key_sound4)
        loadSound("right_answer", R.raw.correct_answer)
        loadSound("wrong_answer", R.raw.wrong_answer)
    }

    private fun loadSound(soundName: String, soundResourceId: Int) {
        // Use lazy delegate to load sound into SoundPool on first access
        soundMap[soundName] = soundPool.load(context, soundResourceId, 1)
    }

    fun playSound(soundName: String) {
        // Play the sound corresponding to the soundName
        val soundId = soundMap[soundName] ?: return
        soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
    }

    fun release() {
        // Release the SoundPool resources
        soundPool.release()
    }
}
