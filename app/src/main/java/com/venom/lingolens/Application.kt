package com.venom.lingolens

import android.app.Application
import com.venom.data.AdsManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application : Application() {

    var adsManager: AdsManager = AdsManager()

    override fun onCreate() {
        super.onCreate()
        adsManager.initialize(this)
    }
}