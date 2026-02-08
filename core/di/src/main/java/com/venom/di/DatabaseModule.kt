package com.venom.di

import android.content.Context
import androidx.room.Room
import com.venom.data.local.dao.OcrDao
import com.venom.data.local.dao.TranslationDao
import com.venom.data.local.database.OcrDatabase
import com.venom.data.local.database.TranslationDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideTranslationDatabase(@ApplicationContext context: Context): TranslationDatabase {
        return Room.databaseBuilder(
            context.applicationContext, TranslationDatabase::class.java, "translationDatabase.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideOcrDatabase(@ApplicationContext context: Context): OcrDatabase {
        return Room.databaseBuilder(
            context.applicationContext, OcrDatabase::class.java, "ocrDatabase.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideOcrDao(database: OcrDatabase): OcrDao {
        return database.ocrDao()
    }

    @Provides
    @Singleton
    fun provideTranslationDao(database: TranslationDatabase): TranslationDao {
        return database.translationDao()
    }
}