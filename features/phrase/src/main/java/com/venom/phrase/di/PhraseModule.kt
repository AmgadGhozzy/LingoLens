package com.venom.phrase.di

import android.content.Context
import androidx.room.Room
import com.venom.phrase.data.local.PhraseDatabase
import com.venom.phrase.data.local.dao.CategoryDao
import com.venom.phrase.data.local.dao.PhraseDao
import com.venom.phrase.data.local.dao.SectionDao
import com.venom.phrase.data.repo.PhraseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PhraseDatabase {
        return Room.databaseBuilder(
            context, PhraseDatabase::class.java, "Phrasebook.db"
        ).createFromAsset("Phrasebook.db").build()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(database: PhraseDatabase) = database.categoryDao()

    @Provides
    @Singleton
    fun provideSectionDao(database: PhraseDatabase) = database.sectionDao()


    @Provides
    @Singleton
    fun providePhraseDao(database: PhraseDatabase) = database.phraseDao()


    @Provides
    @Singleton
    fun provideRepository(
        phraseDao: PhraseDao, categoryDao: CategoryDao, sectionDao: SectionDao
    ) = PhraseRepository(phraseDao, categoryDao, sectionDao)
}

