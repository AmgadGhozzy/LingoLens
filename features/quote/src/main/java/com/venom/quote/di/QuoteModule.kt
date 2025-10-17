package com.venom.quote.di

import android.content.Context
import androidx.room.Room
import com.venom.data.local.dao.QuoteDao
import com.venom.data.local.database.QuoteDatabase
import com.venom.data.repo.QuoteRepositoryImpl
import com.venom.domain.repo.IQuoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object QuoteModule {

    @Provides
    @Singleton
    fun provideQuoteDatabase(@ApplicationContext context: Context): QuoteDatabase {
        return Room.databaseBuilder(
            context,
            QuoteDatabase::class.java,
            "quotes.db"
        )
        .createFromAsset("quotes.db")
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideQuoteDao(quoteDatabase: QuoteDatabase): QuoteDao {
        return quoteDatabase.quoteDao()
    }

    @Provides
    @Singleton
    fun provideQuoteRepository(quoteDao: QuoteDao): IQuoteRepository {
        return QuoteRepositoryImpl(quoteDao)
    }
}