package com.venom.wordcard.di

import android.content.Context
import androidx.room.Room
import com.venom.wordcard.data.local.WordDatabase
import com.venom.wordcard.data.local.dao.WordDao
import com.venom.wordcard.data.repo.WordRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WordCardModule {

    @Provides
    @Singleton
    fun provideWordDatabase(@ApplicationContext context: Context): WordDatabase {
        return Room.databaseBuilder(
            context, WordDatabase::class.java, "words.db"
        ).createFromAsset("words.db").build()
    }

    @Provides
    @Singleton
    fun provideWordDao(database: WordDatabase) = database.wordDao()

    @Provides
    @Singleton
    fun provideWordRepository(wordDao: WordDao) = WordRepository(wordDao)
}
