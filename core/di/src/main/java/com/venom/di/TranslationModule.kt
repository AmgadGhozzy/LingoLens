package com.venom.di

import com.venom.data.api.ChatGPTService
import com.venom.data.api.GeminiService
import com.venom.data.api.TranslationService
import com.venom.data.local.dao.TranslationDao
import com.venom.data.repo.TranslationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
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
        translationDao: TranslationDao,
    ): TranslationRepository = TranslationRepository(
        translationService,
        chatGPTService,
        geminiService,
        translationDao
    )

    @Provides
    @Singleton
    fun provideTranslationService(@Named("TranslationRetrofit") retrofit: Retrofit): TranslationService =
        retrofit.create(TranslationService::class.java)
}