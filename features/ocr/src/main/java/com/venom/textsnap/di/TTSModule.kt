package com.venom.textsnap.di

import android.content.Context
import com.venom.textsnap.data.tts.MediaPlayerFactory
import com.venom.textsnap.data.tts.TTSUrlGenerator
import com.venom.textsnap.data.tts.TextToSpeechFactory
import com.venom.textsnap.data.tts.impl.DefaultMediaPlayerFactory
import com.venom.textsnap.data.tts.impl.DefaultTTSUrlGenerator
import com.venom.textsnap.data.tts.impl.DefaultTextToSpeechFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TTSModule {
    @Provides
    @Singleton
    fun provideTextToSpeechFactory(context: Context): TextToSpeechFactory =
        DefaultTextToSpeechFactory(context)

    @Provides
    @Singleton
    fun provideMediaPlayerFactory(): MediaPlayerFactory =
        DefaultMediaPlayerFactory()

    @Provides
    @Singleton
    fun provideTTSUrlGenerator(): TTSUrlGenerator =
        DefaultTTSUrlGenerator()
}