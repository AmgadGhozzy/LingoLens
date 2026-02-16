package com.venom.di

import android.content.Context
import androidx.room.Room
import com.venom.data.local.dao.QuizAttemptDao
import com.venom.data.local.dao.UsageDailyDao
import com.venom.data.local.dao.UserActivityDao
import com.venom.data.local.dao.UserIdentityDao
import com.venom.data.local.dao.UserProfileDao
import com.venom.data.local.dao.UserWordProgressDao
import com.venom.data.local.dao.WordMasterDao
import com.venom.data.local.dao.XpEventDao
import com.venom.data.local.database.LearningDatabase
import com.venom.data.repo.EnrichmentRepositoryImpl
import com.venom.data.repo.WordMasterRepositoryImpl
import com.venom.domain.repo.IEnrichmentRepository
import com.venom.domain.repo.IWordMasterRepository
import com.venom.domain.srs.SrsEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LearningModule {

    @Provides
    @Singleton
    fun provideLearningDatabase(@ApplicationContext context: Context): LearningDatabase {
        return Room.databaseBuilder(
            context,
            LearningDatabase::class.java,
            "WordsMaster.db"
        )
            .createFromAsset("WordsMaster.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideWordMasterDao(db: LearningDatabase): WordMasterDao = db.wordMasterDao()

    @Provides
    @Singleton
    fun provideUserIdentityDao(db: LearningDatabase): UserIdentityDao = db.userIdentityDao()

    @Provides
    @Singleton
    fun provideUserWordProgressDao(db: LearningDatabase): UserWordProgressDao =
        db.userWordProgressDao()

    @Provides
    @Singleton
    fun provideUserActivityDao(database: LearningDatabase): UserActivityDao =
        database.userActivityDao()

    @Provides
    @Singleton
    fun provideUserProfileDao(db: LearningDatabase): UserProfileDao = db.userProfileDao()

    @Provides
    @Singleton
    fun provideXpEventDao(db: LearningDatabase): XpEventDao = db.xpEventDao()

    @Provides
    @Singleton
    fun provideQuizAttemptDao(db: LearningDatabase): QuizAttemptDao = db.quizAttemptDao()

    @Provides
    @Singleton
    fun provideUsageDailyDao(db: LearningDatabase): UsageDailyDao = db.usageDailyDao()

    @Provides
    @Singleton
    fun provideSrsEngine(): SrsEngine = SrsEngine()

    @Provides
    @Singleton
    fun provideWordMasterRepository(impl: WordMasterRepositoryImpl): IWordMasterRepository = impl

    @Provides
    @Singleton
    fun provideEnrichmentRepository(impl: EnrichmentRepositoryImpl): IEnrichmentRepository = impl
}
