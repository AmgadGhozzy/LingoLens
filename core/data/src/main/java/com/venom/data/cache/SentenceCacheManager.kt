package com.venom.data.cache

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.venom.data.model.SentenceResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sentence_cache")

@Singleton
class SentenceCacheManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    suspend fun getCachedResponse(word: String): SentenceResponse? {
        val key = stringPreferencesKey(word)
        val json = context.dataStore.data.map { it[key] }.first()
        return json?.let { gson.fromJson(it, SentenceResponse::class.java) }
    }

    suspend fun cacheResponse(word: String, response: SentenceResponse) {
        val key = stringPreferencesKey(word)
        context.dataStore.edit { it[key] = gson.toJson(response) }
    }

    suspend fun clearCache() {
        context.dataStore.edit { it.clear() }
    }

    suspend fun getCachedWordsCount(): Int {
        return context.dataStore.data.map { it.asMap().size }.first()
    }
}