package com.venom.dialog.di

import android.content.Context
import androidx.room.Room
import com.venom.dialog.data.local.DialogDatabase
import com.venom.dialog.data.local.dao.DialogMessageDao
import com.venom.dialog.data.repo.DialogRepository
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
    fun provideAppDatabase(@ApplicationContext context: Context): DialogDatabase {
        return Room.databaseBuilder(
            context, DialogDatabase::class.java, "dialog_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideDialogMessageDao(database: DialogDatabase): DialogMessageDao {
        return database.dialogMessageDao()
    }

    @Provides
    @Singleton
    fun provideDialogRepository(dao: DialogMessageDao): DialogRepository {
        return DialogRepository(dao)
    }
}