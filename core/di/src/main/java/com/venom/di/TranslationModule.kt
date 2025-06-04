package com.venom.di

import com.venom.data.api.ChatGPTService
import com.venom.data.api.DeepSeekService
import com.venom.data.api.GeminiService
import com.venom.data.api.GroqService
import com.venom.data.api.HuggingFaceService
import com.venom.data.api.TranslationService
import com.venom.data.local.dao.TranslationDao
import com.venom.data.repo.TranslationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TranslationModule {

    @Provides
    @Singleton
    fun provideTranslationRepository(
        translationService: TranslationService,
        chatGPTService: ChatGPTService,
        geminiService: GeminiService,
        groqService: GroqService,
        deepSeekService: DeepSeekService,
        huggingFaceService: HuggingFaceService,
        translationDao: TranslationDao
    ): TranslationRepository = TranslationRepository(
        translationService,
        chatGPTService,
        geminiService,
        groqService,
        deepSeekService,
        huggingFaceService,
        translationDao
    )
}