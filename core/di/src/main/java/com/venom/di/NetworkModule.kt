package com.venom.di

import com.venom.data.api.ChatGPTService
import com.venom.data.api.GeminiService
import com.venom.data.api.GithubApi
import com.venom.data.api.OcrService
import com.venom.data.api.TranslationService
import com.venom.data.repo.UpdateChecker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(UpdateChecker.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideGithubApi(retrofit: Retrofit): GithubApi {
        return retrofit.create(GithubApi::class.java)
    }

    @Provides
    @Singleton
    @Named("RegularOkHttpClient")
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    @Provides
    @Singleton
    @Named("AiOkHttpClient")
    fun provideAiOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("OcrRetrofit")
    fun provideOcrRetrofit(@Named("RegularOkHttpClient") okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(OcrService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("TranslationRetrofit")
    fun provideTranslationRetrofit(@Named("RegularOkHttpClient") okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(TranslationService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("ChatGPTRetrofit")
    fun provideChatGPTRetrofit(@Named("AiOkHttpClient") aiOkHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ChatGPTService.BASE_URL)
            .client(aiOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("GeminiRetrofit")
    fun provideGeminiRetrofit(@Named("AiOkHttpClient") aiOkHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(GeminiService.BASE_URL)
            .client(aiOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideChatGPTService(@Named("ChatGPTRetrofit") retrofit: Retrofit): ChatGPTService {
        return retrofit.create(ChatGPTService::class.java)
    }

    @Provides
    @Singleton
    fun provideGeminiService(@Named("GeminiRetrofit") retrofit: Retrofit): GeminiService {
        return retrofit.create(GeminiService::class.java)
    }
}