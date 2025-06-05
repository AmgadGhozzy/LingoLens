package com.venom.di

import com.venom.data.api.ChatGPTService
import com.venom.data.api.DeepSeekService
import com.venom.data.api.GeminiService
import com.venom.data.api.GroqService
import com.venom.data.api.HuggingFaceService
import com.venom.data.api.TranslationService
import com.venom.data.local.dao.TranslationDao
import com.venom.data.repo.OfflineTranslationOperations
import com.venom.data.repo.OfflineTranslationRepository
import com.venom.data.repo.OnlineTranslationOperations
import com.venom.data.repo.OnlineTranslationRepository
import com.venom.data.repo.TranslationHistoryOperations
import com.venom.data.repo.TranslationHistoryRepository
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
    fun provideOnlineTranslationOperations(
        translationService: TranslationService,
        chatGPTService: ChatGPTService,
        geminiService: GeminiService,
        groqService: GroqService,
        deepSeekService: DeepSeekService,
        huggingFaceService: HuggingFaceService
    ): OnlineTranslationOperations = OnlineTranslationRepository(
        translationService,
        chatGPTService,
        geminiService,
        groqService,
        deepSeekService,
        huggingFaceService
    )

    @Provides
    @Singleton
    fun provideOfflineTranslationOperations(): OfflineTranslationOperations = OfflineTranslationRepository()

    @Provides
    @Singleton
    fun provideTranslationHistoryOperations(translationDao: TranslationDao): TranslationHistoryOperations = 
        TranslationHistoryRepository(translationDao)

    @Provides
    @Singleton
    fun provideTranslationRepository(
        onlineTranslationOperations: OnlineTranslationOperations,
        offlineTranslationOperations: OfflineTranslationOperations,
        translationHistoryOperations: TranslationHistoryOperations
    ): TranslationRepository = TranslationRepository(
        onlineTranslationOperations,
        offlineTranslationOperations,
        translationHistoryOperations
    )
}