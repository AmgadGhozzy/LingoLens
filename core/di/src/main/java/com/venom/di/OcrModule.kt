package com.venom.di

import android.content.Context
import com.venom.data.api.OcrApiService
import com.venom.data.repo.OcrRepository
import com.venom.utils.ImageCompressor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OcrModule {

    @Provides
    @Singleton
    fun provideOcrRepository(ocrApiService: OcrApiService): OcrRepository {
        return OcrRepository(ocrApiService)
    }

    @Provides
    @Singleton
    fun provideOcrApiService(): OcrApiService {
        return OcrApiService.create()
    }

    @Provides
    @Singleton
    fun provideImageCompressor(context: Context): ImageCompressor {
        return ImageCompressor(context)
    }
}