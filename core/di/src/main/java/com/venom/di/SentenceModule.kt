package com.venom.di

import com.venom.data.api.SentenceService
import com.venom.di.NetworkModule.createRetrofit
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SentenceModule {

    @Provides
    @Singleton
    fun provideSentenceService(@Named("AiOkHttpClient") client: OkHttpClient): SentenceService {
        return createRetrofit(SentenceService.BASE_URL, client).create(SentenceService::class.java)
    }
}