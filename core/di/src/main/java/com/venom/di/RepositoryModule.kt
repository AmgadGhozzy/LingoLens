package com.venom.di

import com.venom.data.repo.QuizRepositoryImpl
import com.venom.data.repo.RemoteConfigImpl
import com.venom.domain.repo.IQuizRepository
import com.venom.domain.repo.IRemoteConfig
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindQuizRepository(
        quizRepositoryImpl: QuizRepositoryImpl
    ): IQuizRepository

    @Binds
    @Singleton
    abstract fun bindRemoteConfig(
        remoteConfigImpl: RemoteConfigImpl
    ): IRemoteConfig
}