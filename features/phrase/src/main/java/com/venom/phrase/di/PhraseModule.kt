package com.venom.phrase.di

import android.content.Context
import androidx.room.Room
import com.venom.phrase.data.local.PhraseDatabase
import com.venom.phrase.data.local.dao.PhraseDao
import com.venom.phrase.data.repo.PhraseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PhraseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PhraseDatabase {
        return Room.databaseBuilder(
            context, PhraseDatabase::class.java, "phrases.db"
        ).createFromAsset("phrases.db").build()
    }

    @Provides
    @Singleton
    fun providePhaseDao(database: PhraseDatabase) = database.phraseDao()

    @Provides
    @Singleton
    fun provideRepository(phraseDao: PhraseDao) = PhraseRepository(phraseDao)
}

