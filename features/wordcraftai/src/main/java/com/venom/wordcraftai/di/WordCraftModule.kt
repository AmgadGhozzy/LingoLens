package com.venom.wordcraftai.di

import com.google.gson.Gson
import com.venom.wordcraftai.data.api.WordCraftApiService
import com.venom.wordcraftai.data.repository.WordCraftRepositoryImpl
import com.venom.wordcraftai.domain.repository.WordCraftRepository
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
object WordCraftModule {

    @Provides
    @Singleton
    @Named("WordCraftOkHttpClient")
    fun provideWordCraftOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private fun createRetrofit(baseUrl: String, client: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    @Named("WordCraftRetrofit")
    fun provideWordCraftRetrofit(
        @Named("WordCraftOkHttpClient") client: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return createRetrofit(WordCraftApiService.BASE_URL, client, gson)
    }

    @Provides
    @Singleton
    fun provideWordCraftApiService(@Named("WordCraftRetrofit") retrofit: Retrofit): WordCraftApiService {
        return retrofit.create(WordCraftApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideWordCraftRepository(apiService: WordCraftApiService): WordCraftRepository {
        return WordCraftRepositoryImpl(apiService)
    }
}