package com.venom.textsnap.di

import android.content.Context
import com.venom.textsnap.data.api.OcrApiService
import com.venom.textsnap.data.api.TranslationApi
import com.venom.textsnap.data.repository.OcrRepository
import com.venom.textsnap.data.repository.TranslateRepository
import com.venom.textsnap.utils.ImageCompressor
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
    fun provideTranslationApi(): TranslationApi {
        return TranslationApi.create()
    }

    @Provides
    @Singleton
    fun provideTranslateRepository(api: TranslationApi): TranslateRepository {
        return TranslateRepository(api)
    }

    @Provides
    @Singleton
    fun provideOcrApiService(): OcrApiService {
        return OcrApiService.create()
    }


    @Provides
    @Singleton
    fun provideOcrRepository(ocrApiService: OcrApiService): OcrRepository {
        return OcrRepository(ocrApiService)
    }

    @Provides
    @Singleton
    fun provideImageCompressor(context: Context): ImageCompressor {
        return ImageCompressor(context)
    }
}
