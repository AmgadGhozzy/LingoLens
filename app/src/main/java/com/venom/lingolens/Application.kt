package com.venom.lingolens

import android.app.Application
import android.os.StrictMode
import android.util.Log
import com.venom.di.NetworkEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@HiltAndroidApp
class Application : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    //var adsManager: AdsManager = AdsManager()

    override fun onCreate() {
        super.onCreate()

        // Pre-warm network clients
        applicationScope.launch(Dispatchers.IO) {
            try {
                val entryPoint = EntryPointAccessors.fromApplication(
                    this@Application,
                    NetworkEntryPoint::class.java
                )

                // Trigger SSL context initialization off main thread
                entryPoint.getRegularClient().connectionPool
                entryPoint.getAiClient().connectionPool

                Log.d("Application", "Network clients initialized successfully")
            } catch (e: Exception) {
                Log.e("Application", "Failed to initialize network clients", e)
            }
        }

        // Initialize AdsManager
//        applicationScope.launch {
//            try {
//                adsManager.initialize(this@Application)
//            } catch (e: Exception) {
//                Log.e("Application", "Failed to initialize AdsManager", e)
//            }
//        }

        if (BuildConfig.DEBUG) {
            enableStrictMode()
        }
    }

    private fun enableStrictMode() {
        Log.d("StrictMode", "StrictMode Enabled in DEBUG mode")

        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        )

        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .build()
        )
    }
}