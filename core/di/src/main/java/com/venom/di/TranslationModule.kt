package com.venom.di

import com.venom.data.api.ChatGPTService
import com.venom.data.api.DeepSeekService
import com.venom.data.api.GeminiService
import com.venom.data.api.GoogleTranslateService
import com.venom.data.api.GroqService
import com.venom.data.api.HuggingFaceService
import com.venom.data.local.dao.TranslationDao
import com.venom.data.repo.OfflineTranslationRepositoryImpl
import com.venom.data.repo.OnlineTranslationRepositoryImpl
import com.venom.data.repo.TranslationHistoryRepositoryImpl
import com.venom.data.repo.TranslationRepositoryImpl
import com.venom.domain.repo.IOfflineTranslation
import com.venom.domain.repo.IOnlineTranslation
import com.venom.domain.repo.ITranslationHistory
import com.venom.domain.repo.ITranslationRepository
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
        translationService: GoogleTranslateService,
        chatGPTService: ChatGPTService,
        geminiService: GeminiService,
        groqService: GroqService,
        deepSeekService: DeepSeekService,
        huggingFaceService: HuggingFaceService
    ): IOnlineTranslation = OnlineTranslationRepositoryImpl(
        translationService,
        chatGPTService,
        geminiService,
        groqService,
        deepSeekService,
        huggingFaceService
    )

    @Provides
    @Singleton
    fun provideOfflineTranslationOperations(): IOfflineTranslation = OfflineTranslationRepositoryImpl()

    @Provides
    @Singleton
    fun provideTranslationHistoryOperations(translationDao: TranslationDao): ITranslationHistory =
        TranslationHistoryRepositoryImpl(translationDao)

    @Provides
    @Singleton
    fun provideTranslationRepository(
        dao: TranslationDao,
        onlineTranslationOperations: IOnlineTranslation,
        offlineTranslationOperations: IOfflineTranslation,
        translationHistoryOperations: ITranslationHistory
    ): TranslationRepositoryImpl = TranslationRepositoryImpl(
        dao = dao,
        onlineTranslationOperations,
        offlineTranslationOperations,
        translationHistoryOperations
    )

    @Provides
    @Singleton
    fun provideITranslationRepository(
        repository: TranslationRepositoryImpl
    ): ITranslationRepository = repository
}