package com.venom.di

import android.content.Context
import com.venom.data.api.OcrService
import com.venom.data.local.dao.OcrDao
import com.venom.data.repo.OcrRepository
import com.venom.utils.ImageCompressor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OcrModule {

    @Provides
    @Singleton
    fun provideOcrRepository(
        ocrApiService: OcrService, ocrDao: OcrDao
    ): OcrRepository = OcrRepository(ocrApiService, ocrDao)

    @Provides
    @Singleton
    fun provideOcrApiService(@Named("OcrRetrofit") retrofit: Retrofit): OcrService {
        return retrofit.create(OcrService::class.java)
    }

    @Provides
    @Singleton
    fun provideImageCompressor(context: Context): ImageCompressor {
        return ImageCompressor(context)
    }
}