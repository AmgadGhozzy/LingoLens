package com.venom.lingopro.di

import android.content.Context
import com.venom.lingopro.data.tts.MediaPlayerFactory
import com.venom.lingopro.data.tts.TTSUrlGenerator
import com.venom.lingopro.data.tts.TextToSpeechFactory
import com.venom.lingopro.data.tts.impl.DefaultMediaPlayerFactory
import com.venom.lingopro.data.tts.impl.DefaultTTSUrlGenerator
import com.venom.lingopro.data.tts.impl.DefaultTextToSpeechFactory
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