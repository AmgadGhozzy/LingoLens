package com.venom.lingopro.di

import com.venom.lingopro.data.api.TranslationService
import com.venom.lingopro.data.local.dao.TranslationDao
import com.venom.lingopro.data.repository.TranslationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideTranslationRepository(
        translationService: TranslationService, translationDao: TranslationDao
    ): TranslationRepository = TranslationRepository(
        translationService, translationDao
    )
}