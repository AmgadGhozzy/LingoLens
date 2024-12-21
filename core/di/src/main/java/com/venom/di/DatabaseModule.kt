package com.venom.di

import android.content.Context
import androidx.room.Room
import com.venom.data.TranslationDatabase
import com.venom.data.local.dao.TranslationDao
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
            context.applicationContext, TranslationDatabase::class.java, "translation_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideTranslationDao(database: TranslationDatabase): TranslationDao {
        return database.translationDao()
    }
}