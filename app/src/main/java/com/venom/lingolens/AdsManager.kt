package com.venom.lingolens

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.MobileAds
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdsManager @Inject constructor() {
    private var isInitialized = false

    fun initialize(context: Context, onComplete: () -> Unit = {}) {
        if (!isInitialized) {
            val applicationContext = context.applicationContext
            MobileAds.initialize(applicationContext) {
                isInitialized = true
                Log.d(TAG, "Mobile Ads SDK Version: ${MobileAds.getVersion()}")
                onComplete()
            }
        }
    }

    companion object {
        private const val TAG = "AdsManager"
        const val TEST_AD_UNIT_ID = "ca-app-pub-3940256099942544/9214589741"
    }
}