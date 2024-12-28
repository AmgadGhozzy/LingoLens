package com.venom.di

import android.content.Context
import com.venom.data.stt.SpeechToTextManager
import com.venom.data.stt.SpeechToTextRepository
import com.venom.domain.repo.stt.ISpeechToTextRepository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
object SpeechModule {
    @Provides
    @ViewModelScoped
    fun provideSpeechToTextManager(
        @ApplicationContext context: Context
    ): SpeechToTextManager = SpeechToTextManager(context)

    @Provides
    @ViewModelScoped
    fun provideSpeechToTextRepository(
        speechToTextManager: SpeechToTextManager
    ): ISpeechToTextRepository = SpeechToTextRepository(speechToTextManager)

}
