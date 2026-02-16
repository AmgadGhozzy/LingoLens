package com.venom.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.venom.data.BuildConfig
import com.venom.data.api.ChatGPTService
import com.venom.data.api.DeepSeekService
import com.venom.data.api.GeminiService
import com.venom.data.api.GoogleTranslateService
import com.venom.data.api.GroqService
import com.venom.data.api.GroqTtsService
import com.venom.data.api.HuggingFaceService
import com.venom.data.api.OcrService
import com.venom.data.api.SupabaseSyncService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
        return HttpLoggingInterceptor().apply {
            this.level = level
            redactHeader("Authorization")
            redactHeader("Cookie")
        }
    }

    @Provides
    @Singleton
    @Named("RegularOkHttpClient")
    fun provideRegularOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("AiOkHttpClient")
    fun provideAiOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    fun createRetrofit(baseUrl: String, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("OcrRetrofit")
    fun provideOcrRetrofit(@Named("RegularOkHttpClient") client: OkHttpClient): Retrofit {
        return createRetrofit(OcrService.BASE_URL, client)
    }

    @Provides
    @Singleton
    @Named("TranslationRetrofit")
    fun provideTranslationRetrofit(@Named("RegularOkHttpClient") client: OkHttpClient): Retrofit {
        return createRetrofit(GoogleTranslateService.BASE_URL, client)
    }

    @Provides
    @Singleton
    fun provideTranslationService(@Named("TranslationRetrofit") retrofit: Retrofit): GoogleTranslateService {
        return retrofit.create(GoogleTranslateService::class.java)
    }

    // AI services
    @Provides
    @Singleton
    fun provideChatGPTService(@Named("AiOkHttpClient") client: OkHttpClient): ChatGPTService {
        return createRetrofit(ChatGPTService.BASE_URL, client).create(ChatGPTService::class.java)
    }

    @Provides
    @Singleton
    fun provideGeminiService(@Named("AiOkHttpClient") client: OkHttpClient): GeminiService {
        return createRetrofit(GeminiService.BASE_URL, client).create(GeminiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGroqService(@Named("AiOkHttpClient") client: OkHttpClient): GroqService {
        return createRetrofit(GroqService.BASE_URL, client).create(GroqService::class.java)
    }

    @Provides
    @Singleton
    fun provideGroqTtsService(@Named("AiOkHttpClient") client: OkHttpClient): GroqTtsService {
        return createRetrofit(GroqTtsService.BASE_URL, client).create(GroqTtsService::class.java)
    }

    @Provides
    @Singleton
    fun provideDeepSeekService(@Named("AiOkHttpClient") client: OkHttpClient): DeepSeekService {
        return createRetrofit(DeepSeekService.BASE_URL, client).create(DeepSeekService::class.java)
    }

    @Provides
    @Singleton
    fun provideHuggingFaceService(@Named("AiOkHttpClient") client: OkHttpClient): HuggingFaceService {
        return createRetrofit(HuggingFaceService.BASE_URL, client).create(HuggingFaceService::class.java)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .setLenient()
        .create()

    @Provides
    @Singleton
    fun provideSupabaseSyncService(@Named("AiOkHttpClient") client: OkHttpClient): SupabaseSyncService {
        return createRetrofit(SupabaseSyncService.BASE_URL, client).create(SupabaseSyncService::class.java)
    }
}