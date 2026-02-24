package com.venom.data.repo

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.venom.data.BuildConfig
import com.venom.domain.repo.IRemoteConfig
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteConfigImpl @Inject constructor() : IRemoteConfig {

    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 60 else 3600
            fetchTimeoutInSeconds = 10
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
    }

    override suspend fun fetchAndActivate() {
        try {
            remoteConfig.fetchAndActivate().await()
            Log.d("RemoteConfig", "Config fetched and activated")
        } catch (e: Exception) {
            Log.e("RemoteConfig", "Fetch failed, using defaults: ${e.message}")
        }
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return if (remoteConfig.all.containsKey(key)) {
            remoteConfig.getBoolean(key)
        } else {
            defaultValue
        }
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return if (remoteConfig.all.containsKey(key)) {
            remoteConfig.getLong(key)
        } else {
            defaultValue
        }
    }

    override fun getDouble(key: String, defaultValue: Double): Double {
        return if (remoteConfig.all.containsKey(key)) {
            remoteConfig.getDouble(key)
        } else {
            defaultValue
        }
    }

    override fun getString(key: String, defaultValue: String): String {
        val value = remoteConfig.getString(key)
        return if (value.isNotEmpty() && remoteConfig.all.containsKey(key)) {
            value
        } else {
            defaultValue
        }
    }
}
