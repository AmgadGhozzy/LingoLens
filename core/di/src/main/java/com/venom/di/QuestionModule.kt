package com.venom.di

import android.content.Context
import androidx.room.Room
import com.venom.data.local.database.QuizDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object QuestionModule {

    @Provides
    @Singleton
    fun provideQuizDatabase(
        @ApplicationContext context: Context
    ): QuizDatabase {
        return Room.databaseBuilder(
            context,
            QuizDatabase::class.java,
            "questions.db"
        )
            .createFromAsset("questions.db")
            .build()
    }

    @Provides
    @Singleton
    fun provideTestClassDao(database: QuizDatabase) = database.testClassDao()

    @Provides
    @Singleton
    fun provideTestCategoryDao(database: QuizDatabase) = database.testCategoryDao()

    @Provides
    @Singleton
    fun provideTestListDao(database: QuizDatabase) = database.testListDao()

    @Provides
    @Singleton
    fun provideQuestionDao(database: QuizDatabase) = database.questionDao()
}