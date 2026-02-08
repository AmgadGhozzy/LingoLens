package com.venom.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Named

@EntryPoint
@InstallIn(SingletonComponent::class)
interface NetworkEntryPoint {
    @Named("RegularOkHttpClient")
    fun getRegularClient(): OkHttpClient

    @Named("AiOkHttpClient")
    fun getAiClient(): OkHttpClient
}