package com.venom.di

import android.content.Context
import com.venom.domain.repo.tts.MediaPlayerFactory
import com.venom.domain.repo.tts.TTSUrlGenerator
import com.venom.domain.repo.tts.TextToSpeechFactory
import com.venom.data.tts.DefaultMediaPlayerFactory
import com.venom.data.tts.DefaultTTSUrlGenerator
import com.venom.data.tts.DefaultTextToSpeechFactory
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